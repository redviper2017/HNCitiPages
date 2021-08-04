package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hncitipages.R;

public class ProfileSectionFragment extends Fragment {

    public ProfileSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_profile);


        screenTitle.setText(R.string.my_profile);

        // Inflate the layout for this fragment
        return view;
    }
}