package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";
    private Utils myUtils;
    private Profile userProfile;

    public UserProfileFragment() {
        // Required empty public constructor
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
        CircleImageView profilePhoto = view.findViewById(R.id.circleimageview_user_profile);
        ProgressBar progressBar      = view.findViewById(R.id.user_profile_photo_progressbar);



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

        String profilePhotoUrl = userProfile.getProfileImg();
        Log.d(TAG,"loaded profile photo url = "+profilePhotoUrl);
        Picasso
                .get()
                .load(profilePhotoUrl)
                .into(profilePhoto);
        progressBar.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return view;
    }
}
