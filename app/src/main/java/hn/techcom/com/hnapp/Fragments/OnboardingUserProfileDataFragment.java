package hn.techcom.com.hnapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class OnboardingUserProfileDataFragment extends Fragment implements View.OnClickListener{

    private static final int Image_Capture_Code = 1;
    private CircleImageView profileImage;

    public OnboardingUserProfileDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_profile_data, container, false);

        LinearLayout openImage = view.findViewById(R.id.fab_add_image);
        profileImage = view.findViewById(R.id.circleimageview_profile_onboarding);

        openImage.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_add_image){
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cInt,Image_Capture_Code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
//                profileImage.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}