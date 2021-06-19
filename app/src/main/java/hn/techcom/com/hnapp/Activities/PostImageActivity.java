package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
    private String newImageFileName, newImageAspectRatio;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_shareimage);
        captureImageButton           = findViewById(R.id.capture_image_button);
        selectImageButton            = findViewById(R.id.select_image_button);
        shareImageButton             = findViewById(R.id.share_image_button);
        clearImageButton             = findViewById(R.id.clear_image_button);
        postCategorySpinner          = findViewById(R.id.spinner_post_type);
        imageview                    = findViewById(R.id.imageview);
        progressBar                  = findViewById(R.id.share_image_progressbar);
        imageCaption                 = findViewById(R.id.textInputEditText_image_caption);

        postCategory = "r";

        screenTitle.setText(R.string.share_picture);

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
        captureImageButton.setOnClickListener(this);
        selectImageButton.setOnClickListener(this);
        shareImageButton.setOnClickListener(this);
        clearImageButton.setOnClickListener(this);

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
            imageview.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

            String imageAspect = getImageDimension(currentPhotoPath);
            Log.d(TAG,"aspect of image = "+imageAspect);

            newImageAspectRatio = imageAspect;

            //Change visibility of function buttons
            changeButtonsUI("upload");

        }
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Log.d(TAG,"multi video output = "+data.getData());
            imageview.setImageURI(data.getData());

            String filePath = getRealPathFromURIPath(data.getData(), this);
            newImageFile = new File(filePath);

            String imageAspect = getImageDimension(filePath);
            Log.d(TAG,"aspect of image = "+imageAspect);

            newImageAspectRatio = imageAspect;

            //Change visibility of function buttons
            changeButtonsUI("upload");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                            }
                        }
                    }
                }
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "hn.techcom.com.hnapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        newImageFile = image;
        return image;
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
        Log.d(TAG, "posted image aspect ratio = "+newImageAspectRatio);
        progressBar.setVisibility(View.VISIBLE);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody posttype = RequestBody.create(MediaType.parse("text/plain"),"I");
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"),postCategory);
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(imageCaption.getText()).toString());
        RequestBody aspect = RequestBody.create(MediaType.parse("text/plain"),newImageAspectRatio);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                newImageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), newImageFile)
        );

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NewPostResponse> call = service.shareImage(user,city,country,posttype,category,text,aspect,filePart);

        call.enqueue(new Callback<NewPostResponse>() {
            @Override
            public void onResponse(Call<NewPostResponse> call, @NonNull Response<NewPostResponse> response) {
                Log.d(TAG, "post image api response code = "+response.code());
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
//                    Toast.makeText(PostImageActivity.this,"Unable to share image! Try again later..",Toast.LENGTH_LONG).show();
                    imageCaption.setText("");
                    postCategorySpinner.setSelection(0);
                    imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                    changeButtonsUI("select");
                }
            }

            @Override
            public void onFailure(Call<NewPostResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                imageCaption.setText("");
                postCategorySpinner.setSelection(0);
                imageview.setImageDrawable(ContextCompat.getDrawable(PostImageActivity.this,R.drawable.image_1));
                changeButtonsUI("select");
//                Toast.makeText(PostImageActivity.this,"Unable to share image! Try again later..",Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getImageDimension(String imagePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(imagePath, options);
        int width = options.outWidth;

        int height = options.outHeight;

        Log.d(TAG,"width of this image = "+width);
        Log.d(TAG,"height of this image = "+height);
        //If you want, the MIME type will also be decoded (if possible)
        String type = options.outMimeType;

        if(width>height)
            return "landscape";
        else
            return "portrait";
    }
}