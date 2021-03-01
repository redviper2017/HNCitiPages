package hn.techcom.com.hnapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import hn.techcom.com.hnapp.Activities.MainActivity;
import hn.techcom.com.hnapp.R;

public class LoginWithPhoneFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginWithPhoneFragment";
    private LinearLayout phoneLayout, codeLayout;
    private TextInputEditText firstOtpBox, secondOtpBox, thirdOtpBox, fourthOtpBox, fifthOtpBox, phoneText;
    private CountryCodePicker countryCodePicker;
    private String verificationCodeBySystem;

    public LoginWithPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_with_phone, container, false);

        //Hooks
        MaterialCardView getCodeButton = view.findViewById(R.id.button_get_code);

        phoneLayout = view.findViewById(R.id.layout_phone_login);
        codeLayout = view.findViewById(R.id.layout_code_login);

        firstOtpBox = view.findViewById(R.id.first_otp_box);
        secondOtpBox = view.findViewById(R.id.second_otp_box);
        thirdOtpBox = view.findViewById(R.id.third_otp_box);
        fourthOtpBox = view.findViewById(R.id.fourth_otp_box);
        fifthOtpBox = view.findViewById(R.id.fifth_otp_box);

        phoneText = view.findViewById(R.id.textInputEditText_phone);
        countryCodePicker = view.findViewById(R.id.country_code_picker);


        //Click Listeners
        getCodeButton.setOnClickListener(this);

        firstOtpBox.addTextChangedListener(new GenericTextWatcher(firstOtpBox));
        secondOtpBox.addTextChangedListener(new GenericTextWatcher(secondOtpBox));
        thirdOtpBox.addTextChangedListener(new GenericTextWatcher(thirdOtpBox));
        fourthOtpBox.addTextChangedListener(new GenericTextWatcher(fourthOtpBox));
        fifthOtpBox.addTextChangedListener(new GenericTextWatcher(fifthOtpBox));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_get_code) {

            String phoneNumberWithCode = countryCodePicker.getSelectedCountryCodeWithPlus()+phoneText.getText().toString();
            sendVerificationCodeToUser(phoneNumberWithCode);
        }
    }

    private void sendVerificationCodeToUser(String phone) {
        Log.d(TAG, "selected country code = " + phone);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(getContext(),"Phone number verified! Please enter the CODE that you have just received in your phone.",Toast.LENGTH_LONG).show();

            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                verifyCode(code);
                phoneLayout.setVisibility(View.GONE);
                codeLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,code);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }else{
                            Toast.makeText(getContext(),"Phone number verification failed! Please try again with a valid number",Toast.LENGTH_LONG).show();
                        }
                    }
                });
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