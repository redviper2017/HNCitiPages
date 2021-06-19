package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;


public class OnboardingUserContactFragment extends Fragment {

    private static final String TAG = "ContactFragment";
    private TextInputEditText emailText, phoneText;
    private CountryCodePicker countryCodePicker;
    private Profile userProfile = null;

    private Utils myUtils;

    public OnboardingUserContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_contact, container, false);

        myUtils = new Utils();

        //hooks
        LinearLayout phoneLayout = view.findViewById(R.id.layout_phone_onboarding_contact);
        LinearLayout emailLayout = view.findViewById(R.id.layout_email_onboarding_contact);

        emailText = view.findViewById(R.id.textinputedittext_email);
        phoneText = view.findViewById(R.id.textinputedittext_phone);
        countryCodePicker = view.findViewById(R.id.country_code_picker);

        // Shows layout based on user's sign-in method
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        String email = userProfile.getEmail();

        if(email != null){
            phoneLayout.setVisibility(View.VISIBLE);
        }else{
            emailLayout.setVisibility(View.VISIBLE);
        }



        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        String currentPhoneFieldValue = countryCodePicker.getSelectedCountryCodeWithPlus() + Objects.requireNonNull(phoneText.getText()).toString();

        Log.d(TAG,"email = "+emailText.getText().toString());
        Log.d(TAG,"phone = "+ currentPhoneFieldValue);

        //adding user's phone number to shared preference
        userProfile.setMobileNumber(currentPhoneFieldValue);
        myUtils.storeNewUserToSharedPref(Objects.requireNonNull(getContext()),userProfile);
    }
}