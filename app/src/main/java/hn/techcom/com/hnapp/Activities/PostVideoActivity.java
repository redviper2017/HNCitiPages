package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.NewPostResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PostVideoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PostVideoActivity";
    private Spinner postCategorySpinner;
    private VideoView videoview;
    private MaterialCardView captureVideoButton, selectVideoButton, shareVideoButton, clearVideoButton;
    private Utils myUtils;
    private Profile userProfile;
    private File newVideoFile;
    private String mVideoFileName;
    private ProgressBar progressBar;
    private String postCategory;
    private TextInputEditText videoCaption;
    private AndExoPlayerView videoPlayer;

    //Constants
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_VIDEO_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private String newVideoAspectRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_sharevideo);
        captureVideoButton           = findViewById(R.id.capture_video_button);
        selectVideoButton            = findViewById(R.id.select_video_button);
        shareVideoButton             = findViewById(R.id.share_video_button);
        clearVideoButton             = findViewById(R.id.clear_video_button);
        postCategorySpinner          = findViewById(R.id.spinner_post_type);
        videoview                    = findViewById(R.id.videoview);
        progressBar                  = findViewById(R.id.share_video_progressbar);
        videoCaption                 = findViewById(R.id.textInputEditText_video_caption);
        videoPlayer                  = findViewById(R.id.video_player);

        postCategory = "r";

        screenTitle.setText(R.string.share_video);

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
        captureVideoButton.setOnClickListener(this);
        selectVideoButton.setOnClickListener(this);
        shareVideoButton.setOnClickListener(this);
        clearVideoButton.setOnClickListener(this);

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);

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
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
        if(view.getId() == R.id.capture_video_button)
            if(checkPermission())
                startVideoCaptureIntent();
            else requestPermission();
        if(view.getId() == R.id.select_video_button)
            startVideoPickIntent();
        if(view.getId() == R.id.clear_video_button) {
            videoCaption.setText("");
            postCategorySpinner.setSelection(0);
            recreate();
        }
        if(view.getId() == R.id.share_video_button){
            if(!TextUtils.isEmpty(videoCaption.getText()))
                shareNewVideo();
            else
                Toast.makeText(this,"Oops! You've forgot to enter a caption for your video..",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG,"inside onActivityResult of PostVideoActivity = "+"YES");
//            videoview.setVideoURI(data.getData());
//            videoview.requestFocus();
            videoPlayer.setSource(mVideoFileName);

            newVideoAspectRatio = getVideoDimension(mVideoFileName);
            Log.d(TAG, "selected video aspect ratio = "+newVideoAspectRatio);

            //Change visibility of function buttons
            changeButtonsUI("upload");
        }
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK) {
            Log.d(TAG,"multi video output = "+data.getData());
//            videoview.setVideoURI(data.getData());
//            videoview.requestFocus();

            String filePath = getRealPathFromURIPath(data.getData(), this);
            newVideoFile = new File(filePath);

            videoPlayer.setSource(filePath);

            newVideoAspectRatio = getVideoDimension(filePath);
            Log.d(TAG, "selected video aspect ratio = "+newVideoAspectRatio);

            //Change visibility of function buttons
            changeButtonsUI("upload");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted && cameraAccepted)
                        startVideoCaptureIntent();
                    else {
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
                            } } } }
                break;
        }
    }

    //Custom methods
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void startVideoCaptureIntent(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");
        String newVideoeName = df.format(date) + ".mp4";
        String newVideoPath = "/sdcard/" + newVideoeName;
        newVideoFile = new File(newVideoPath);
        mVideoFileName = newVideoFile.toString();
        Uri outuri = Uri.fromFile(newVideoFile);

        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
    }

    public void startVideoPickIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");

        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String string = cursor.getString(idx);
            return string;
        }
    }

    private void changeButtonsUI(String layoutName){
        switch (layoutName){
            case "upload":
                captureVideoButton.setVisibility(View.GONE);
                selectVideoButton.setVisibility(View.GONE);
                shareVideoButton.setVisibility(View.VISIBLE);
                clearVideoButton.setVisibility(View.VISIBLE);
                break;
            case "select":
                captureVideoButton.setVisibility(View.VISIBLE);
                selectVideoButton.setVisibility(View.VISIBLE);
                shareVideoButton.setVisibility(View.GONE);
                clearVideoButton.setVisibility(View.GONE);
        }
    }

    private void shareNewVideo(){
        Log.d(TAG, "posted video aspect ratio = "+newVideoAspectRatio);
        progressBar.setVisibility(View.VISIBLE);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody posttype = RequestBody.create(MediaType.parse("text/plain"),"V");
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"),postCategory);
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(videoCaption.getText()).toString());
        RequestBody aspect = RequestBody.create(MediaType.parse("text/plain"),newVideoAspectRatio);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                newVideoFile.getName(),
                RequestBody.create(MediaType.parse("video/*"), newVideoFile)
        );

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NewPostResponse> call = service.shareVideo(user,city,country,posttype,category,text,aspect,filePart);

        call.enqueue(new Callback<NewPostResponse>() {
            @Override
            public void onResponse(Call<NewPostResponse> call, Response<NewPostResponse> response) {
                if (response.code() == 201) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostVideoActivity.this,"Video shared successfully!",Toast.LENGTH_LONG).show();
                    videoCaption.setText("");
                    postCategorySpinner.setSelection(0);
                    recreate();
                }
                else{
                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(PostVideoActivity.this,"Unable to share video! Try again later..",Toast.LENGTH_LONG).show();
                    videoCaption.setText("");
                    postCategorySpinner.setSelection(0);
                    recreate();
                }
            }

            @Override
            public void onFailure(Call<NewPostResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostVideoActivity.this,"Unable to share video! Try again later..",Toast.LENGTH_LONG).show();
                videoCaption.setText("");
                postCategorySpinner.setSelection(0);
                recreate();
            }
        });
    }

    private String getVideoDimension(String videoPath){
        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
        mRetriever.setDataSource(videoPath);
        Bitmap frame = mRetriever.getFrameAtTime();

        int width = frame.getWidth();
        int height = frame.getHeight();

        if(width>height)
            return "landscape";
        else
            return "portrait";
    }
}