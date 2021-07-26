package hn.techcom.com.hnapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.LikeResponse;
import hn.techcom.com.hnapp.Models.NewPostResponse;
import hn.techcom.com.hnapp.Models.PostList;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Models.SingleUserInfoResponse;
import hn.techcom.com.hnapp.Models.SupportingProfileList;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "UserProfileActivity";
    private Utils myUtils;
    private Profile userProfile;
    private String currentPhotoPath, newImageAspectRatio;
    private File newImageFile;
    private CircleImageView profilePhoto;
    private ProgressBar progressBar;
    private MaterialCardView updateProfileButton;
    private MaterialTextView postCountText, supportingCountText, supporterCountText;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private AlertDialog dialog;

    ArrayList<User> supportingProfilesArrayList, supporterProfilesArrayList;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Hooks
        MaterialTextView screenTitle         = findViewById(R.id.text_screen_title_profile);
        MaterialTextView name                = findViewById(R.id.textview_name_view_profile);
        MaterialTextView username            = findViewById(R.id.textview_username_view_profile);
        MaterialTextView email               = findViewById(R.id.textview_email_view_profile);
        MaterialTextView phone               = findViewById(R.id.textview_phone_view_profile);
        MaterialTextView hnid                = findViewById(R.id.textview_hnid_view_profile);
        postCountText                        = findViewById(R.id.post_count_viewprofile);
        supportingCountText                  = findViewById(R.id.supporting_count_viewprofile);
        supporterCountText                   = findViewById(R.id.supporter_count_viewprofile);
        profilePhoto                         = findViewById(R.id.circleimageview_user_profile);
        progressBar                          = findViewById(R.id.user_profile_photo_progressbar);
        View updatePhotoButton               = findViewById(R.id.fab_update_image);
        updateProfileButton                  = findViewById(R.id.update_profile_button);
        CardView viewPostsButton             = findViewById(R.id.view_posts_button);
        CardView viewSupportersButton        = findViewById(R.id.view_supporters_button);
        CardView viewSupportingButton        = findViewById(R.id.view_supporting_button);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);

        getLatestPostsListBySingleUser(userProfile.getHnid());
        getSupportingProfiles();
        getSupporterProfiles();

        Log.d("ProfileFragment","registered user profile = "+userProfile.toString());

        screenTitle.setText(R.string.my_profile);
        progressBar.setVisibility(View.VISIBLE);

        name.setText(userProfile.getFullName());
        username.setText(userProfile.getUsername());
        email.setText(userProfile.getEmail());
        phone.setText(userProfile.getMobileNumber());
        hnid.setText(userProfile.getHnid());

        String profilePhotoUrl = userProfile.getProfileImgThumbnail();
        Log.d(TAG,"loaded profile photo url = "+profilePhotoUrl);
        Picasso
                .get()
                .load(profilePhotoUrl)
                .into(profilePhoto);
        progressBar.setVisibility(View.GONE);

        updatePhotoButton.setOnClickListener(this);
        updateProfileButton.setOnClickListener(this);
        viewPostsButton.setOnClickListener(this);
        viewSupportersButton.setOnClickListener(this);
        viewSupportingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_update_image)
            updatePhotoDialog();
        if (view.getId() == R.id.update_profile_button)
            updateProfilePhoto();
        if (view.getId() == R.id.view_posts_button)
            Toast.makeText(this,"Loading all posts...",Toast.LENGTH_SHORT).show();
        if (view.getId() == R.id.view_supporters_button)
            Toast.makeText(this,"Loading all supporters...",Toast.LENGTH_SHORT).show();
        if (view.getId() == R.id.view_supporting_button)
            Toast.makeText(this,"Loading all supporting profiles...",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            profilePhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

            String imageAspect = getImageDimension(currentPhotoPath);
            Log.d(TAG,"aspect of image = "+imageAspect);

            newImageAspectRatio = imageAspect;

            dialog.cancel();
            updateProfileButton.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            profilePhoto.setImageURI(Objects.requireNonNull(data).getData());

            String filePath = getRealPathFromURIPath(data.getData(), this);
            newImageFile = new File(filePath);

            String imageAspect = getImageDimension(filePath);
            Log.d(TAG,"aspect of image = "+imageAspect);

            newImageAspectRatio = imageAspect;

            dialog.cancel();
            updateProfileButton.setVisibility(View.VISIBLE);
        }
    }

    private void updatePhotoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = getLayoutInflater().inflate(R.layout.update_profile_photo_alert_layout, null);

        MaterialCardView captureButton = alertView.findViewById(R.id.button_capture);
        MaterialCardView selectButton = alertView.findViewById(R.id.button_select);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageCaptureIntent();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImagePickIntent();
            }
        });

        builder.setView(alertView);
        dialog = builder.create();
        dialog.show();
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

    public void startImagePickIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_IMAGE_PICK);
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

    private void updateProfilePhoto(){
        progressBar.setVisibility(View.VISIBLE);

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "profile_img",
                newImageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), newImageFile)
        );

        Log.d(TAG,"user = "+userProfile.getHnid()+ " image = "+newImageFile.getName());

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LikeResponse> call = service.updateProfileImage(user,filePart);

        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(@NonNull Call<LikeResponse> call, @NonNull Response<LikeResponse> response) {
                Log.d(TAG, "post image api response code = "+response.code());
                if (response.code() == 200) {
                    progressBar.setVisibility(View.GONE);
                    updateProfileButton.setVisibility(View.GONE);
                    Toast.makeText(UserProfileActivity.this,"Profile photo updated successfully!",Toast.LENGTH_LONG).show();
                    getUser();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(UserProfileActivity.this,"Unable to update profile! Try again later..",Toast.LENGTH_LONG).show();
            }
        });
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

    public void getUser() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SingleUserInfoResponse> call = service.getUserInfo(userProfile.getHnid());
        call.enqueue(new Callback<SingleUserInfoResponse>() {
            @Override
            public void onResponse(Call<SingleUserInfoResponse> call, Response<SingleUserInfoResponse> response) {
                if (response.code() == 200){
                    SingleUserInfoResponse user = response.body();
                    if (user != null) {
                        userProfile.setProfileImgThumbnail(user.getProfileImgThumbnail());
                        myUtils.storeNewUserToSharedPref(UserProfileActivity.this,userProfile);
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleUserInfoResponse> call, Throwable t) {

            }
        });
    }

    //get initial supporting profile list
    public void getSupportingProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupportingProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supportingProfileList = response.body();
                    Log.d(TAG, "number of supporting profile = "+ supportingProfileList.getCount());

                    if(supportingProfileList.getCount()>0) {
                        supportingProfilesArrayList = new ArrayList<>();
                        supportingProfilesArrayList.addAll(supportingProfileList.getResults());
                        supportingCountText.setText(String.valueOf(supportingProfilesArrayList.size()));
                    }else{
                        supportingCountText.setText("0");
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }

    //get initial supporter profile list
    public void getSupporterProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupporterProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supporterProfileList = response.body();
                    Log.d(TAG, "number of supporter profile = "+ supporterProfileList.getCount());

                    if(supporterProfileList.getCount()>0) {
                        supporterProfilesArrayList = new ArrayList<>();
                        supporterProfilesArrayList.addAll(supporterProfileList.getResults());
                        supporterCountText.setText(String.valueOf(supporterProfilesArrayList.size()));
                    }else{
                        supporterCountText.setText("0");
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }

    //get initial user posts list
    public void getLatestPostsListBySingleUser(String target_hnid) {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsBySingleUser(target_hnid,userProfile.getHnid());
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.code() == 200){
                    Log.d(TAG,"total number of post by this user = "+response.body().getResults().size());
                    PostList postList = response.body();
                    if (postList.getCount() > 0)
                        postCountText.setText(String.valueOf(postList.getCount()));
                    else
                        postCountText.setText("0");
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }
}