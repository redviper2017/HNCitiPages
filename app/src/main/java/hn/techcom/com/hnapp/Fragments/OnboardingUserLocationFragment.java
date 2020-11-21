package hn.techcom.com.hnapp.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Objects;

import hn.techcom.com.hnapp.R;

public class OnboardingUserLocationFragment extends Fragment{

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    public OnboardingUserLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_location, container, false);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        // Inflate the layout for this fragment
        return view;
    }

}
