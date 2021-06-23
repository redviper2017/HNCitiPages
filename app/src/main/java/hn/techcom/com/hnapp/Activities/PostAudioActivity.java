package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.io.File;
import java.util.Objects;

import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.NewPostResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.UriUtils;
import hn.techcom.com.hnapp.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PostAudioActivity extends AppCompatActivity implements View.OnClickListener{
    //Constants
    private static final int REQUEST_AUDIO_CAPTURE = 1;
    private static final int REQUEST_AUDIO_PICK = 2;
    private static final String TAG = "PostAudioActivity";
    private MaterialCardView captureAudioButton, selectAudioButton, shareAudioButton, clearAudioButton;
    private Spinner postCategorySpinner;
    private TextInputEditText audioCaption;
    private AndExoPlayerView audioPlayer;
    private ProgressBar progressBar;
    private String postCategory;
    private File newAudioFile;
    private Utils myUtils;
    private Profile userProfile;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_audio);

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_shareaudio);
        captureAudioButton           = findViewById(R.id.capture_audio_button);
        selectAudioButton            = findViewById(R.id.select_audio_button);
        shareAudioButton             = findViewById(R.id.share_audio_button);
        clearAudioButton             = findViewById(R.id.clear_audio_button);
        postCategorySpinner          = findViewById(R.id.spinner_post_type);
        audioCaption                 = findViewById(R.id.textInputEditText_audio_caption);
        audioPlayer                  = findViewById(R.id.audio_player);
        progressBar                  = findViewById(R.id.share_audio_progressbar);


        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);

        screenTitle.setText(R.string.share_audio);

        //Setting up post types for spinner
        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Comedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postCategorySpinner.setAdapter(adapter);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        captureAudioButton.setOnClickListener(this);
        selectAudioButton.setOnClickListener(this);
        shareAudioButton.setOnClickListener(this);
        clearAudioButton.setOnClickListener(this);

        postCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(parent.getItemAtPosition(position).toString()){
                    case "Random":
                        postCategory = "r";
                        break;
                    case "Positive Thoughts":
                        postCategory = "p";
                        break;
                    case "Talent":
                        postCategory = "t";
                        break;
                    case "Lifestyle":
                        postCategory = "l";
                        break;
                    case "Culture":
                        postCategory = "c";
                        break;
                    case "Hustle":
                        postCategory = "h";
                        break;
                    case "Commedy":
                        postCategory = "o";
                        break;
                    case "News":
                        postCategory = "n";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_AUDIO_PICK && resultCode == RESULT_OK){
            audioPlayer.setSource(String.valueOf(data.getData()));
            audioPlayer.setPlayWhenReady(true);

            String filePath = UriUtils.getPathFromUri(this,data.getData());
            newAudioFile = new File(filePath);

            //Change visibility of function buttons
            changeButtonsUI("upload");
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
        if(view.getId() == R.id.capture_audio_button)
            startAudioCaptureIntent();
        if(view.getId() == R.id.select_audio_button)
            if(checkPermission())
                startAudioPick();
            else
                requestPermission();
        if(view.getId() == R.id.clear_audio_button) {
            audioCaption.setText("");
            postCategorySpinner.setSelection(0);
            recreate();
        }
        if(view.getId() == R.id.share_audio_button){
            if(!TextUtils.isEmpty(audioCaption.getText()))
                shareNewAudio();
            else
                Toast.makeText(this,"Oops! You've forgot to enter a caption for your picture..",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted)
                        startAudioPick();
                    else
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel(
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        });
                                return;
                            }
                        }
                }
        }
    }

    //Custom methods
    private void startAudioCaptureIntent(){
//        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        startActivityForResult(intent, REQUEST_AUDIO_CAPTURE);
        AlertDialog.Builder builder = new AlertDialog.Builder(PostAudioActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.voice_note_alert_layout, null);

        FloatingActionButton recordVoiceNote = (FloatingActionButton) alertView.findViewById(R.id.capture_voice_button);

        Toast.makeText(this,"This feature is coming soon!!",Toast.LENGTH_LONG).show();

        recordVoiceNote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        builder.setView(alertView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startAudioPick(){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,REQUEST_AUDIO_PICK);
    }

    public String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            String string = cursor.getString(idx);
            return string;
        }
    }

    private void changeButtonsUI(String layoutName){
        switch (layoutName){
            case "upload":
                captureAudioButton.setVisibility(View.GONE);
                selectAudioButton.setVisibility(View.GONE);
                shareAudioButton.setVisibility(View.VISIBLE);
                clearAudioButton.setVisibility(View.VISIBLE);
                break;
            case "select":
                captureAudioButton.setVisibility(View.VISIBLE);
                selectAudioButton.setVisibility(View.VISIBLE);
                shareAudioButton.setVisibility(View.GONE);
                clearAudioButton.setVisibility(View.GONE);
        }
    }

    private void shareNewAudio(){
//        Log.d(TAG, "posted image aspect ratio = "+newImageAspectRatio);
        progressBar.setVisibility(View.VISIBLE);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody posttype = RequestBody.create(MediaType.parse("text/plain"),"A");
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"),postCategory);
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(audioCaption.getText()).toString());
//        RequestBody aspect = RequestBody.create(MediaType.parse("text/plain"),newImageAspectRatio);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                newAudioFile.getName(),
                RequestBody.create(MediaType.parse("audio/*"), newAudioFile)
        );

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NewPostResponse> call = service.shareAudio(user,city,country,posttype,category,text,filePart);

        call.enqueue(new Callback<NewPostResponse>() {
            @Override
            public void onResponse(Call<NewPostResponse> call, @NonNull Response<NewPostResponse> response) {
                Log.d(TAG, "post image api response code = "+response.code());
                if (response.code() == 201) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostAudioActivity.this,"Audio shared successfully!",Toast.LENGTH_LONG).show();
                    audioCaption.setText("");
                    postCategorySpinner.setSelection(0);
//                    imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                    changeButtonsUI("select");
                    startActivity(new Intent(PostAudioActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(PostImageActivity.this,"Unable to share image! Try again later..",Toast.LENGTH_LONG).show();
                    audioCaption.setText("");
                    postCategorySpinner.setSelection(0);
//                    imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                    changeButtonsUI("select");
                }
            }

            @Override
            public void onFailure(Call<NewPostResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                audioCaption.setText("");
                postCategorySpinner.setSelection(0);
//                imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                changeButtonsUI("select");
                Toast.makeText(PostAudioActivity.this,"Unable to share audio! Try again later..",Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to this permission in order to continue with this feature.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}