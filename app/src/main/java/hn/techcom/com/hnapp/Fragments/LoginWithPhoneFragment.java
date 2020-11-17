package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import hn.techcom.com.hnapp.R;

public class LoginWithPhoneFragment extends Fragment implements View.OnClickListener {

    private LinearLayout phoneLayout, codeLayout;
    private TextInputEditText firstOtpBox, secondOtpBox, thirdOtpBox, fourthOtpBox, fifthOtpBox;

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
            phoneLayout.setVisibility(View.GONE);
            codeLayout.setVisibility(View.VISIBLE);
        }
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