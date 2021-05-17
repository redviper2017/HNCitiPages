package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PostVideoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PostVideoActivity";
    private Spinner postTypeSpinner;
    private VideoView videoview;

    private Utils myUtils;
    private Profile userProfile;
    private File newVideoFile;
    private String mVideoFileName;

    //Constants
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_VIDEO_PICK = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        //Hooks
        MaterialCardView captureVideoButton = findViewById(R.id.capture_video_button);
        MaterialCardView selectVideoButton    = findViewById(R.id.select_video_button);
        ImageButton backButton              = findViewById(R.id.image_button_back);
        CircleImageView userAvatar          = findViewById(R.id.user_avatar_sharevideo);
        postTypeSpinner                     = findViewById(R.id.spinner_post_type);
        videoview                           = findViewById(R.id.videoview);

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
        postTypeSpinner.setAdapter(adapter);

        //Set MediaController  to enable play, pause, forward, etc options.
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoview);

        videoview.setMediaController(mediaController);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        captureVideoButton.setOnClickListener(this);
        selectVideoButton.setOnClickListener(this);

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);
        String profilePhotoUrl = "http://167.99.13.238:8000" + userProfile.getProfileImg();
        Picasso
                .get()
                .load(profilePhotoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(userAvatar);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG,"inside onActivityResult of PostVideoActivity = "+"YES");
            videoview.setVideoURI(data.getData());
            videoview.requestFocus();
        }
        if (requestCode == REQUEST_VIDEO_PICK && resultCode == RESULT_OK) {
            Log.d(TAG,"multi video output = "+data.getData());
            videoview.setVideoURI(data.getData());
            videoview.requestFocus();
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
        String newVideoPath = Environment.getExternalStorageDirectory().getPath() + newVideoeName;
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
}