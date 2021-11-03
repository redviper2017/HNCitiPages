package hn.techcom.com.hncitipages.Fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anirudh.locationfetch.EasyLocationFetch;
import com.anirudh.locationfetch.GeoLocationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class OnboardingUserLocationFragment
        extends Fragment
        implements LocationListener {

    private static final String TAG = "LocationFragment";

    private MaterialTextView city, country;
    private ProgressBar progressBar;
    private Profile userProfile = null;
    private Utils myUtils;

    private LocationManager locationManager;

    public OnboardingUserLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_location, container, false);

        myUtils = new Utils();

        //Hooks
        city = view.findViewById(R.id.city_onboarding);
        country = view.findViewById(R.id.country_onboarding);
        progressBar = view.findViewById(R.id.progressbar);
        MaterialCardView getCurrentLocationButton = view.findViewById(R.id.button_get_current_location);

        //Runtime Permission
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{ACCESS_FINE_LOCATION},100);
        }

        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        //Click listeners
        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                boolean network_enabled = false;
                network_enabled = locationManager.isProviderEnabled(LocationManager. NETWORK_PROVIDER );
                if (network_enabled)
                    getLocation();
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Please turn on your device's location from settings then come back.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        Log.d(TAG,"new user = "+userProfile.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!country.getText().toString().equals("Country"))
            userProfile.setCountry(country.getText().toString());
        else
            userProfile.setCountry("N/A");

        if (!city.getText().toString().equals("City/State"))
            userProfile.setCity(city.getText().toString());
        else
            userProfile.setCity("N/A");


        myUtils.storeNewUserToSharedPref(requireContext(),userProfile);

        Log.d(TAG,"new user = "+userProfile.toString());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String countryName = addresses.get(0).getCountryName();
            String cityName = addresses.get(0).getLocality();

            country.setText(countryName);
            city.setText(cityName);

            progressBar.setVisibility(View.GONE);

//            Toast.makeText(requireContext(),"user country = "+countryName,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

