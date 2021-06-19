package hn.techcom.com.hnapp.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VideoPostFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final String TAG = "VideoPostFragment";
    private Spinner postTypeSpinner;
    private VideoView videoview;
    private MaterialCardView captureVideoButton;

    private Utils myUtils;
    private Profile userProfile;
    private File newVideoFile;
    private String mVideoFileName;


    public VideoPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_video, container, false);

        //Hooks
        videoview          = view.findViewById(R.id.videoview);
        captureVideoButton = view.findViewById(R.id.capture_video_button);


        //OnClick Listeners
        captureVideoButton.setOnClickListener(this);

        //Set MediaController  to enable play, pause, forward, etc options.
//        MediaController mediaController = new MediaController(getContext());
//        mediaController.setAnchorView(videoview);
//
//        videoview.setMediaController(mediaController);

        CircleImageView userAvatar = view.findViewById(R.id.user_avatar_sharevideo);
                        postTypeSpinner = view.findViewById(R.id.spinner_post_type);


        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        String profilePhotoUrl = "http://167.99.13.238:8000" + userProfile.getProfileImg();

        Picasso
                .get()
                .load(profilePhotoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(userAvatar);

        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Commedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.capture_video_button){
            if(checkPermission()){
                startVideoCaptureIntent();
            }
            else{
                requestPermission();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG,"inside onActivityResult fragment = "+"YES");
            videoview.setVideoURI(Uri.fromFile(newVideoFile));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}