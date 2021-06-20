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

public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        MaterialTextView name        = view.findViewById(R.id.textview_name_view_profile);
        MaterialTextView username    = view.findViewById(R.id.textview_username_view_profile);
        MaterialTextView email       = view.findViewById(R.id.textview_email_view_profile);
        MaterialTextView phone       = view.findViewById(R.id.textview_phone_view_profile);
        MaterialTextView hnid        = view.findViewById(R.id.textview_hnid_view_profile);
        CircleImageView profilePhoto = view.findViewById(R.id.circleimageview_user_profile);
        ProgressBar progressBar      = view.findViewById(R.id.user_profile_photo_progressbar);

        Profile registeredUser = getNewUserFromSharedPreference();

        Log.d("ProfileFragment","registered user profile = "+registeredUser.toString());

        progressBar.setVisibility(View.VISIBLE);

        name.setText(registeredUser.getFullName());
        username.setText(registeredUser.getUsername());
        email.setText(registeredUser.getEmail());
        phone.setText(registeredUser.getMobileNumber());
        hnid.setText(registeredUser.getHnid());

        String profilePhotoUrl = registeredUser.getProfileImg();
        Log.d(TAG,"loaded profile photo url = "+profilePhotoUrl);
        Picasso
                .get()
                .load(profilePhotoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(profilePhoto);
        progressBar.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return view;
    }

    private Profile getNewUserFromSharedPreference() {
        SharedPreferences pref = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("NewUser","");
        Profile user = gson.fromJson(json, Profile.class);

        return user;
    }
}
