package hn.techcom.com.hncitipages.Activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.LikeResponse;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.SingleUserInfoResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity
        extends AppCompatActivity
        implements View.OnClickListener, LocationListener {

    private static final String TAG = "UpdateProfileFragment";
    private Utils myUtils;
    private Profile userProfile;
    private String currentPhotoPath;
    private File newImageFile;
    private CircleImageView profilePhoto;
    private ProgressBar progressBar;
    private MaterialCardView updateProfileButton, updateLocationButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private AlertDialog dialog;
    private LinearLayout titleLayout;
    private MaterialTextView titleText, locationText;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        //Hooks
        MaterialTextView screenTitle         = findViewById(R.id.text_screen_title_profile);
        MaterialTextView name                = findViewById(R.id.textview_name_view_profile);
        MaterialTextView username            = findViewById(R.id.textview_username_view_profile);
        MaterialTextView email               = findViewById(R.id.textview_email_view_profile);
        MaterialTextView phone               = findViewById(R.id.textview_phone_view_profile);
        MaterialTextView hnid                = findViewById(R.id.textview_hnid_view_profile);
        locationText                         = findViewById(R.id.textview_location);

        profilePhoto                         = findViewById(R.id.circleimageview_user_profile);
        progressBar                          = findViewById(R.id.user_profile_photo_progressbar);
        View updatePhotoButton               = findViewById(R.id.fab_update_image);
        updateProfileButton                  = findViewById(R.id.update_profile_button);
        updateLocationButton                 = findViewById(R.id.button_get_current_location);
        titleLayout                          = findViewById(R.id.title_text_layout);
        titleText                            = findViewById(R.id.textView_title_view_profile);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);

        //Runtime Permission
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},100);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!userProfile.getTitle().equals("User")){
            String title = userProfile.getTitle() + ", HN CitiPages";
            titleText.setText(title);
            titleLayout.setVisibility(View.VISIBLE);
        }

        Log.d("ProfileFragment","registered user profile = "+userProfile.toString());

        screenTitle.setText(R.string.update_profile);
        progressBar.setVisibility(View.VISIBLE);

        name.setText(userProfile.getFullName());
        username.setText(userProfile.getUsername());
        email.setText(userProfile.getEmail());
        phone.setText(userProfile.getMobileNumber());
        hnid.setText(userProfile.getHnid());

        String userLocation = userProfile.getCity() + ", " + userProfile.getCountry();
        Log.d(TAG,"user location in profile = "+userLocation);
        locationText.setText(userLocation);

        String profilePhotoUrl = userProfile.getProfileImgThumbnail();
        Log.d(TAG,"loaded profile photo url = "+profilePhotoUrl);
//        Picasso
//                .get()
//                .load(profilePhotoUrl)
//                .into(profilePhoto);

        Glide.with(this).load(profilePhotoUrl).centerCrop().into(profilePhoto);

        progressBar.setVisibility(View.GONE);

        updatePhotoButton.setOnClickListener(this);
        updateProfileButton.setOnClickListener(this);
        updateLocationButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_update_image)
            updatePhotoDialog();
        if (view.getId() == R.id.update_profile_button)
            updateProfilePhoto();
        if (view.getId() == R.id.button_get_current_location){
            progressBar.setVisibility(View.VISIBLE);
            boolean network_enabled;
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (network_enabled)
                getLocation();
            else {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(this)
                        .setTitle("Enable Location!")
                        .setMessage("Please enable your device's location first then come back.")
                        .setCancelable(false)
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).setNegativeButton("Cancel",null)
                        .show();
//                    Toast.makeText(requireContext(), "Please turn on your device's location from settings then come back.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            profilePhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

            dialog.cancel();
            updateProfileButton.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            profilePhoto.setImageURI(Objects.requireNonNull(data).getData());

            String filePath = getRealPathFromURIPath(data.getData(), this);
            newImageFile = new File(filePath);

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
                        "hn.techcom.com.hncitipages.fileprovider",
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
                    Toast.makeText(getApplicationContext(),"Profile photo updated successfully!",Toast.LENGTH_LONG).show();
                    getUser();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),"Unable to update profile! Try again later..",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUser() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SingleUserInfoResponse> call = service.getUserInfo(userProfile.getHnid());
        call.enqueue(new Callback<SingleUserInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<SingleUserInfoResponse> call, @NonNull Response<SingleUserInfoResponse> response) {
                if (response.code() == 200){
                    SingleUserInfoResponse user = response.body();
                    if (user != null) {
                        userProfile.setProfileImgThumbnail(user.getProfileImgThumbnail());
                        myUtils.storeNewUserToSharedPref(getApplicationContext(),userProfile);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleUserInfoResponse> call, @NonNull Throwable t) {

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

    @SuppressLint("MissingPermission")
    private void getLocation(){
        Log.d(TAG,"getLocation() called = YES");
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG,"onLocationChanged() called = YES");
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String countryName = addresses.get(0).getCountryName();
            String cityName = addresses.get(0).getLocality();

            String userNewLocation = cityName + ", " + countryName;

            Log.d(TAG,"user new location in profile = " + userNewLocation);

            locationText.setText(userNewLocation);

            progressBar.setVisibility(View.GONE);

//            Toast.makeText(requireContext(),"user country = "+countryName,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}