package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Random;

import hn.techcom.com.hnapp.R;


public class OnboardingUserContactFragment extends Fragment {

    public OnboardingUserContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_contact, container, false);

        LinearLayout phoneLayout = view.findViewById(R.id.layout_phone_onboarding_contact);
        LinearLayout emailLayout = view.findViewById(R.id.layout_email_onboarding_contact);

        // this will later be replaced by user's sign in method
        Random r = new Random();
        int layoutToDisplay = r.nextInt(2);

        switch(layoutToDisplay){
            case 0:
                phoneLayout.setVisibility(View.VISIBLE);
                break;
            default:
                emailLayout.setVisibility(View.VISIBLE);
        }

        // Inflate the layout for this fragment
        return view;
    }
}