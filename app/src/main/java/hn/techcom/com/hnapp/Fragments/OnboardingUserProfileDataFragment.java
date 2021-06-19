package hn.techcom.com.hnapp.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Activities.MainActivity;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;

public class OnboardingUserProfileDataFragment extends Fragment implements View.OnClickListener{

    private static final int Image_Capture_Code = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "ProfileDataFragment";
    private CircleImageView profileImage;
    private MaterialCardView buttonCreateAccount;

    private FrameLayout frameLayout;


    private File newImageFile;
    private String mCameraFileName;

    private Profile userProfile = null;
    private Utils myUtils;

    private ProgressBar progressBar;
    private String currentPhotoPath;


    public OnboardingUserProfileDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_profile_data, container, false);
        myUtils = new Utils();

        //Hooks
        LinearLayout openImage = view.findViewById(R.id.fab_add_image);
        profileImage = view.findViewById(R.id.circleimageview_profile_onboarding);
        frameLayout = view.findViewById(R.id.frameLayout);
        buttonCreateAccount = view.findViewById(R.id.button_create_account);
        progressBar = view.findViewById(R.id.registration_progressbar);

        openImage.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_add_image){
            if (checkPermission()) {
                dispatchTakePictureIntent();
            } else {
                requestPermission();
            }
        }
        if(view.getId() == R.id.button_create_account){
            progressBar.setVisibility(View.VISIBLE);
            registerNewUserAccount();
//            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Capture_Code && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                profileImage.setImageBitmap(imageBitmap);
//            }
            profileImage.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

            Log.d(TAG,"new image file = "+newImageFile);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "hn.techcom.com.hnapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Image_Capture_Code);
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted && cameraAccepted)
                        Snackbar.make(frameLayout, "Permission Granted, Now you can access storage data and camera.", Snackbar.LENGTH_LONG).show();
                    else {

                        Snackbar.make(frameLayout, "Permission Denied, You cannot access storage data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // function to create new user account
    public void registerNewUserAccount(){
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        userProfile.setFirstImg(newImageFile.toString());
        Log.d(TAG,"onboarding new user = "+userProfile.toString());

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), userProfile.getEmail());
        RequestBody mobile_number = RequestBody.create(MediaType.parse("text/plain"), userProfile.getMobileNumber());
        RequestBody full_name = RequestBody.create(MediaType.parse("text/plain"), userProfile.getFullName());
        RequestBody date_of_birth = RequestBody.create(MediaType.parse("text/plain"), userProfile.getDateOfBirth());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), userProfile.getGender());

//        File file = new File(android.os.Environment.getExternalStorageDirectory(), mCameraFileName);

//        Toast.makeText(getContext(),"image = "+file,Toast.LENGTH_LONG).show();

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "first_img",
                newImageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), newImageFile)
        );
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Profile> call = service.registerNewUser(
                email,
                mobile_number,
                full_name,
                date_of_birth,
                city,
                country,
                gender,
                filePart //registration without image file
        );

        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call,@NonNull Response<Profile> response) {

                Log.d(TAG,"new registered user = "+ response);
                if (response.code() == 201) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Registered Successfully!",Toast.LENGTH_LONG).show();

                    userProfile = response.body();
                    Log.d(TAG,"new created user = "+userProfile);



                    myUtils.storeNewUserToSharedPref(Objects.requireNonNull(getContext()),userProfile);
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),response.toString(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Registration Failed! Try again later.",Toast.LENGTH_LONG).show();
            }
        });
    }
}