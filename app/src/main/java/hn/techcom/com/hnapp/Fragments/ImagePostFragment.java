package hn.techcom.com.hnapp.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.R;
import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

public class ImagePostFragment extends Fragment implements View.OnClickListener, BottomSheetImagePicker.OnImagesSelectedListener, PermissionListener {


    private static final int REQUEST_SINGLE_PERMISSION = 1;
    private CardView selectImageButton, captureImageButton;

    private  Sheriff sheriffPermission;


    public ImagePostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_post_2, container, false);


        selectImageButton = view.findViewById(R.id.select_image_button);
        captureImageButton = view.findViewById(R.id.capture_image_button);

        ((CardView) selectImageButton).setOnClickListener(this);
        ((CardView) captureImageButton).setOnClickListener(this);

        //Sherif permission object
        sheriffPermission = Sheriff.Builder()
                .with(this)
                .requestCode(REQUEST_SINGLE_PERMISSION)
                .setPermissionResultCallback(this)
                .askFor(SheriffPermission.CAMERA)
                .build();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.capture_image_button) {

            Toast.makeText(getContext(), "opening gallery app!", Toast.LENGTH_LONG).show();
            sheriffPermission.requestPermissions();

        } else if (v.getId() == R.id.select_image_button) {
            Toast.makeText(getContext(), "opening camera app!", Toast.LENGTH_LONG).show();
            new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .multiSelect(1, 4)
                    .multiSelectTitles(
                            R.plurals.pick_multi,
                            R.plurals.pick_multi_more,
                            R.string.pick_multi_limit
                    )
                    .peekHeight(R.dimen.peekHeight)
                    .columnSize(R.dimen.columnSize)
                    .requestTag("multi")
                    .show(getChildFragmentManager(), null);
        }
    }

    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> uris, @Nullable String s) {
        for (Uri uri : uris) {
            Log.d(TAG, "selected image uri = " + uri);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {

    }
}