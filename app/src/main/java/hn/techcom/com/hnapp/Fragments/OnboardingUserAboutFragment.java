package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import hn.techcom.com.hnapp.R;

public class OnboardingUserAboutFragment extends Fragment implements View.OnClickListener {

    private MaterialTextView dateOfBirth;
    private RelativeLayout genderLayout;

    private String gender;

    public OnboardingUserAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_about, container, false);

        //Hooks
        Spinner spinner = view.findViewById(R.id.spinner_gender);
        dateOfBirth = view.findViewById(R.id.text_dob);
        genderLayout = view.findViewById(R.id.layout_gender);

        //CLick listeners
        dateOfBirth.setOnClickListener(this);

        String[] arrayGender = new String[]
                {"Gender", "Male", "Female", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
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
                    genderLayout.setBackground(getResources().getDrawable(R.drawable.custom_textview_shape_selected));
                    genderLayout.setTag("selected");
                }
                return v.performClick();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
                genderLayout.setBackground(getResources().getDrawable(R.drawable.custom_textview_shape));
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
            dateOfBirth.setBackground(getResources().getDrawable(R.drawable.custom_textview_shape_selected));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
    }
}