package hn.techcom.com.hnapp.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ImagePostFragment extends Fragment implements View.OnClickListener {



    private View view;

    String permissions[] = {"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"};
    int PERMISSION_REQUEST_CODE = 200;


    public ImagePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_post, container, false);

        this.view = view;

        Spinner spinner = view.findViewById(R.id.spinner_post_type);

        LinearLayout uploadFromFileButton = view.findViewById(R.id.button_upload_from_gallery);

        LinearLayout uploadFromCameraButton = view.findViewById(R.id.button_upload_from_camera);

        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "News",
                "Emergency"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        uploadFromFileButton.setOnClickListener(this);

        uploadFromCameraButton.setOnClickListener(this);

//        getLatestCameraImage();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    boolean storageAccessAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccessAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccessAccepted && cameraAccessAccepted) {
//                        getLatestCameraImage();
                    }
                    else {
                        Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("To use this feature you need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
                break;
        }
    }

//    public void getLatestCameraImage() {
//        if (checkPermission()) {
//            File dcimPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera");
//            if (dcimPath.exists()) {
//                Log.i("DCIM PATH", dcimPath.toString());
//
//                File[] files = dcimPath.listFiles();
//                Arrays.sort(files, new Comparator<File>() {
//                    public int compare(File f1, File f2) {
//                        return Long.compare(f1.lastModified(), f2.lastModified());
//                    }
//                });
//                Picasso.get()
//                        .load(files[files.length - 2])
//                        .into(galleryPreview);
//
//                Log.d("ImagePostFragment", "first file path = " + files[files.length - 2].getName());
//            }
//        } else {
//            requestPermission();
//        }
//    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void getFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_upload_from_gallery) {

//            getFromGallery();

            Toast.makeText(getContext(),"opening gallery app!",Toast.LENGTH_LONG).show();

        } else if (v.getId() == R.id.button_upload_from_camera) {
            Toast.makeText(getContext(),"opening camera app!",Toast.LENGTH_LONG).show();
        }
    }
}