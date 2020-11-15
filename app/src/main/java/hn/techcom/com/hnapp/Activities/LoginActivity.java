package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import java.util.Objects;

import hn.techcom.com.hnapp.Fragments.HomeFragment;
import hn.techcom.com.hnapp.Fragments.LoginWithFragment;
import hn.techcom.com.hnapp.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment fragment = new LoginWithFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_login, Objects.requireNonNull(fragment)).commit();
    }
}