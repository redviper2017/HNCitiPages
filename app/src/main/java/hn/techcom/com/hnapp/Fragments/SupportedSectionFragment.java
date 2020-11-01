package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hn.techcom.com.hnapp.R;

public class SupportedSectionFragment extends Fragment {

    private static final String TAG = "SupportedProfileSectionFragment";

    public SupportedSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}