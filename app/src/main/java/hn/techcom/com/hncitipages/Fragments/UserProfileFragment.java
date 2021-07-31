package hn.techcom.com.hncitipages.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class UserProfileFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "UserProfileFragment";
    private Utils myUtils;
    private Profile userProfile;
    private String currentPhotoPath, newImageAspectRatio;
    private File newImageFile;
    private CircleImageView profilePhoto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ActivityResultLauncher<Intent> activityResultLauncher;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    profilePhoto.setImageURI(Uri.fromFile(new File(currentPhotoPath)));

                    String imageAspect = getImageDimension(currentPhotoPath);
                    Log.d(TAG,"aspect of image = "+imageAspect);

                    newImageAspectRatio = imageAspect;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_profile);
        MaterialTextView name        = view.findViewById(R.id.textview_name_view_profile);
        MaterialTextView username    = view.findViewById(R.id.textview_username_view_profile);
        MaterialTextView email       = view.findViewById(R.id.textview_email_view_profile);
        MaterialTextView phone       = view.findViewById(R.id.textview_phone_view_profile);
        MaterialTextView hnid        = view.findViewById(R.id.textview_hnid_view_profile);
        profilePhoto                 = view.findViewById(R.id.circleimageview_user_profile);
        ProgressBar progressBar      = view.findViewById(R.id.user_profile_photo_progressbar);
        View updatePhotoButton       = view.findViewById(R.id.fab_update_image);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

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

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_update_image)
            updatePhotoDialog();
    }

    private void updatePhotoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

            }
        });

        builder.setView(alertView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startImageCaptureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "hn.techcom.com.hncitipages.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                activityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
}
