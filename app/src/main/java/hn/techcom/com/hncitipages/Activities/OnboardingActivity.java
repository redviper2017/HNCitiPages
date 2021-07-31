package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Objects;

import hn.techcom.com.hncitipages.Fragments.UserOnboardingFragment;
import hn.techcom.com.hncitipages.R;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Fragment fragment = new UserOnboardingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_onboarding, Objects.requireNonNull(fragment)).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        locationEnabled();
    }

    private void locationEnabled () {
        LocationManager lm = (LocationManager)
                this.getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setMessage( "Please turn your device's location first and then come back." )
                    .setPositiveButton( "Ok" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    onBackPressed();
                                }
                            })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            getActivity().onBackPressed();
//                        }
//                    })
                    .show() ;
        }
    }
}