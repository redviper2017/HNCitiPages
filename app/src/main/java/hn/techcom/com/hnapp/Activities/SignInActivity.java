package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.hbb20.CountryCodePicker;

import hn.techcom.com.hnapp.Fragments.LoginWithPhoneFragment;
import hn.techcom.com.hnapp.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String  TAG = "SignInActivity";
    private MaterialCardView googleSignInButton, phoneSignInButton, getCodeButton;
    private MaterialTextView termsButton;
    private LinearLayout loginWithLayout, loginWithPhoneLayout, enterPhoneLayout, enterCodelayout;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText firstOtpBox, secondOtpBox, thirdOtpBox, fourthOtpBox, fifthOtpBox, phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Hooks
        googleSignInButton   = findViewById(R.id.button_login_with_gmail);
        phoneSignInButton    = findViewById(R.id.button_login_with_phone_final);
        termsButton          = findViewById(R.id.button_terms_click);
        getCodeButton        = findViewById(R.id.button_get_code);
        loginWithLayout      = findViewById(R.id.layout_login_with_signin);
        loginWithPhoneLayout = findViewById(R.id.layout_login_with_phone_signin);
        enterPhoneLayout     = findViewById(R.id.layout_phone_login);
        enterCodelayout      = findViewById(R.id.layout_code_login);
        countryCodePicker    = findViewById(R.id.country_code_picker);
        phoneText            = findViewById(R.id.textInputEditText_phone);
        firstOtpBox          = findViewById(R.id.first_otp_box);
        secondOtpBox         = findViewById(R.id.second_otp_box);
        thirdOtpBox          = findViewById(R.id.third_otp_box);
        fourthOtpBox         = findViewById(R.id.fourth_otp_box);
        fifthOtpBox          = findViewById(R.id.fifth_otp_box);

        //Click listeners
        phoneSignInButton.setOnClickListener(this);
        getCodeButton.setOnClickListener(this);
        termsButton.setOnClickListener(this);


        firstOtpBox.addTextChangedListener(new GenericTextWatcher(firstOtpBox));
        secondOtpBox.addTextChangedListener(new GenericTextWatcher(secondOtpBox));
        thirdOtpBox.addTextChangedListener(new GenericTextWatcher(thirdOtpBox));
        fourthOtpBox.addTextChangedListener(new GenericTextWatcher(fourthOtpBox));
        fifthOtpBox.addTextChangedListener(new GenericTextWatcher(fifthOtpBox));
    }

    @Override
    public void onBackPressed() {
        if(enterPhoneLayout.getVisibility() == View.VISIBLE) {
            loginWithPhoneLayout.setVisibility(View.GONE);
            loginWithLayout.setVisibility(View.VISIBLE);
        }else if(enterCodelayout.getVisibility() == View.VISIBLE){
            enterCodelayout.setVisibility(View.GONE);
            enterPhoneLayout.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_login_with_phone_final){
            Log.d(TAG,"login with = "+"PHONE");
            loginWithLayout.setVisibility(View.GONE);
            loginWithPhoneLayout.setVisibility(View.VISIBLE);
            enterPhoneLayout.setVisibility(View.VISIBLE);
        }
        if(view.getId() == R.id.button_get_code){
            String phoneWithCode = countryCodePicker.getSelectedCountryCodeWithPlus()+phoneText.getText().toString();
            enterCodelayout.setVisibility(View.VISIBLE);
            enterPhoneLayout.setVisibility(View.GONE);
        }
        if(view.getId() == R.id.button_terms_click)
            startActivity(new Intent(this, TermsActivity.class));
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();

            if (view.getId() == R.id.first_otp_box) {
                if (text.length() == 1)
                    secondOtpBox.requestFocus();
            } else if (view.getId() == R.id.second_otp_box) {
                if (text.length() == 1)
                    thirdOtpBox.requestFocus();
                else
                    firstOtpBox.requestFocus();
            } else if (view.getId() == R.id.third_otp_box) {
                if (text.length() == 1)
                    fourthOtpBox.requestFocus();
                else
                    secondOtpBox.requestFocus();
            } else if (view.getId() == R.id.fourth_otp_box) {
                if (text.length() == 1)
                    fifthOtpBox.requestFocus();
                else
                    thirdOtpBox.requestFocus();
            } else if (text.length() == 0)
                fourthOtpBox.requestFocus();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

}