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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.NewPostResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PostImageActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetImagePicker.OnImagesSelectedListener, PermissionListener {
    //Constants
    private static final String TAG = "PostVideoActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private Spinner postCategorySpinner;
    private ProgressBar progressBar;
    private String postCategory;
    private ImageView imageview;
    private MaterialCardView captureImageButton, selectImageButton, shareImageButton, clearImageButton;

    private Utils myUtils;
    private Profile userProfile;
    private TextInputEditText imageCaption;
    private File newImageFile;
    private String newImageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        //Hooks
        ImageButton backButton     = findViewById(R.id.image_button_back);
        CircleImageView userAvatar = findViewById(R.id.user_avatar_shareimage);
        captureImageButton         = findViewById(R.id.capture_image_button);
        selectImageButton          = findViewById(R.id.select_image_button);
        shareImageButton           = findViewById(R.id.share_image_button);
        clearImageButton           = findViewById(R.id.clear_image_button);
        postCategorySpinner        = findViewById(R.id.spinner_post_type);
        imageview                  = findViewById(R.id.imageview);
        progressBar                = findViewById(R.id.share_image_progressbar);
        imageCaption               = findViewById(R.id.textInputEditText_image_caption);

        postCategory = "r";

        //Setting up post types for spinner
        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Commedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postCategorySpinner.setAdapter(adapter);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        captureImageButton.setOnClickListener(this);
        selectImageButton.setOnClickListener(this);
        shareImageButton.setOnClickListener(this);
        clearImageButton.setOnClickListener(this);

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);
        String profilePhotoUrl = "http://167.99.13.238:8000" + userProfile.getProfileImg();
        Picasso
                .get()
                .load(profilePhotoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(userAvatar);

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
        if(view.getId() == R.id.capture_image_button)
            if(checkPermission())
                startImageCaptureIntent();
            else
                requestPermission();
        if(view.getId() == R.id.select_image_button)
            startImagePickIntent();
        if(view.getId() == R.id.clear_image_button){
            imageCaption.setText("");
            postCategorySpinner.setSelection(0);
            imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
            changeButtonsUI("select");
        }
        if(view.getId() == R.id.share_image_button){
            if(!TextUtils.isEmpty(imageCaption.getText()))
                shareNewImage();
            else
                Toast.makeText(this,"Oops! You've forgot to enter a caption for your picture..",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageview.setImageURI(Uri.fromFile(new File(newImageFileName)));

            //Change visibility of function buttons
            changeButtonsUI("upload");

        }
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Log.d(TAG,"multi video output = "+data.getData());
            imageview.setImageURI(data.getData());

            String filePath = getRealPathFromURIPath(data.getData(), this);
            newImageFile = new File(filePath);

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
                        startImageCaptureIntent();
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

    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> list, String s) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {

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

    private void startImageCaptureIntent(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent inent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");
        String newImageName = df.format(date) + ".jpg";
        String newImagePath = "/sdcard/" + newImageName;
        newImageFile = new File(newImagePath);
        newImageFileName = newImageFile.toString();
        Uri outuri = Uri.fromFile(newImageFile);

        inent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(inent, REQUEST_IMAGE_CAPTURE);
    }

    public void startImagePickIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_IMAGE_PICK);
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
                captureImageButton.setVisibility(View.GONE);
                selectImageButton.setVisibility(View.GONE);
                shareImageButton.setVisibility(View.VISIBLE);
                clearImageButton.setVisibility(View.VISIBLE);
                break;
            case "select":
                captureImageButton.setVisibility(View.VISIBLE);
                selectImageButton.setVisibility(View.VISIBLE);
                shareImageButton.setVisibility(View.GONE);
                clearImageButton.setVisibility(View.GONE);
        }
    }

    private void shareNewImage(){
        progressBar.setVisibility(View.VISIBLE);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody posttype = RequestBody.create(MediaType.parse("text/plain"),"I");
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"),postCategory);
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(imageCaption.getText()).toString());

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                newImageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), newImageFile)
        );

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NewPostResponse> call = service.shareImage(user,city,country,posttype,category,text,filePart);

        call.enqueue(new Callback<NewPostResponse>() {
            @Override
            public void onResponse(Call<NewPostResponse> call, Response<NewPostResponse> response) {
                if (response.code() == 201) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostImageActivity.this,"Image shared successfully!",Toast.LENGTH_LONG).show();
                    imageCaption.setText("");
                    postCategorySpinner.setSelection(0);
                    imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                    changeButtonsUI("select");
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostImageActivity.this,"Unable to share image! Try again later..",Toast.LENGTH_LONG).show();
                    imageCaption.setText("");
                    postCategorySpinner.setSelection(0);
                    imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                    changeButtonsUI("select");
                }
            }

            @Override
            public void onFailure(Call<NewPostResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostImageActivity.this,"Unable to share image! Try again later..",Toast.LENGTH_LONG).show();
            }
        });
    }
}