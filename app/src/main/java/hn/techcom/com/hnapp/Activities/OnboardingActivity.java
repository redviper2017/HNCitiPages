package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import java.util.Objects;

import hn.techcom.com.hnapp.Fragments.UserOnboardingFragment;
import hn.techcom.com.hnapp.R;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Fragment fragment = new UserOnboardingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_onboarding, Objects.requireNonNull(fragment)).commit();
    }


}