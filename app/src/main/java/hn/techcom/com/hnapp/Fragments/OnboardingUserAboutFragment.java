package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hn.techcom.com.hnapp.R;

public class OnboardingUserAboutFragment extends Fragment {

    public OnboardingUserAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_about, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}