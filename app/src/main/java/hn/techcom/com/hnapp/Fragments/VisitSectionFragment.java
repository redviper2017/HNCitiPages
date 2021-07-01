package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

public class VisitSectionFragment extends Fragment {

    private Utils myUtils;
    private Profile userProfile;


    public VisitSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_visitsection);
        MaterialTextView location    = view.findViewById(R.id.location_visitsection);


        //Getting user profile from local storage
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        String locationText = userProfile.getCity() + ", " + userProfile.getCountry();

        screenTitle.setText(R.string.visit_section);
        location.setText(locationText);


        // Inflate the layout for this fragment
        return view;
    }
}