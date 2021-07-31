package hn.techcom.com.hncitipages.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.Objects;

import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class OnboardingUserAboutFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AboutFragment";
    private MaterialTextView dateOfBirth;
    private TextInputEditText fullname;
    private RelativeLayout genderLayout;

    private String gender, genderCode;

    private Profile userProfile = null;

    private Utils myUtils;

    public OnboardingUserAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_about, container, false);

        myUtils = new Utils();

        //Hooks
        Spinner spinner = view.findViewById(R.id.spinner_gender);
        dateOfBirth = view.findViewById(R.id.text_dob);
        fullname = view.findViewById(R.id.textinputeditext_fullname);
        genderLayout = view.findViewById(R.id.layout_gender);

        //CLick listeners
        dateOfBirth.setOnClickListener(this);

        String[] arrayGender = new String[]
                {"Gender", "Male", "Female", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayGender) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;

            }
        };
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    genderLayout.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.custom_textview_shape_selected,null));
                    genderLayout.setTag("selected");
                }
                return v.performClick();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();

                switch(gender){
                    case "Male":
                        genderCode = "M";
                        break;
                    case "Female":
                        genderCode = "F";
                        break;
                    case "Other":
                        genderCode = "O";
                        break;
                }

                genderLayout.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.custom_textview_shape,null));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_dob) {
            showDatePickerDialog(v);
            dateOfBirth.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.custom_textview_shape_selected,null));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //getting stored user information so far from shared preference
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        userProfile.setFullName(Objects.requireNonNull(fullname.getText()).toString());
        userProfile.setDateOfBirth(dateOfBirth.getText().toString());
        userProfile.setGender(genderCode);

        myUtils.storeNewUserToSharedPref(requireContext(),userProfile);

        Log.d(TAG,"new user = "+userProfile.toString());
    }

    public void showDatePickerDialog(View v) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),R.style.customDatePickerStyle,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        dateOfBirth.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.custom_textview_shape,null));

                    }
                }, year, month, day);
//        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        datePickerDialog.show();
    }
}