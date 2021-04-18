package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import hn.techcom.com.hnapp.Models.NewUser;
import hn.techcom.com.hnapp.R;

public class UserProfileFragment extends Fragment {
    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        MaterialTextView name = view.findViewById(R.id.textview_name_view_profile);
        MaterialTextView username = view.findViewById(R.id.textview_username_view_profile);
        MaterialTextView email = view.findViewById(R.id.textview_email_view_profile);
        MaterialTextView phone = view.findViewById(R.id.textview_phone_view_profile);
        MaterialTextView hnid = view.findViewById(R.id.textview_hnid_view_profile);

        NewUser registeredUser = getNewUserFromSharedPreference();

        Log.d("ProfileFragment","registered user profile = "+registeredUser.toString());

        name.setText(registeredUser.getFullName());
        username.setText(registeredUser.getUsername());
        email.setText(registeredUser.getEmail());
        phone.setText(registeredUser.getMobileNumber());
        hnid.setText(registeredUser.getHnid());

        // Inflate the layout for this fragment
        return view;
    }

    private NewUser getNewUserFromSharedPreference() {
        SharedPreferences pref = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("NewUser","");
        NewUser user = gson.fromJson(json, NewUser.class);

        return user;
    }
}
