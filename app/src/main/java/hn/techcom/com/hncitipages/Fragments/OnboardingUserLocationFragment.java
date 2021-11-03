package hn.techcom.com.hncitipages.Fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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

import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class OnboardingUserLocationFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "LocationFragment";

    private MaterialTextView city, country;
    private MaterialCardView getCurrentLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Profile userProfile = null;
    private Utils myUtils;

    double latitude, longitude;
    private FrameLayout frameLayout;

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
        getCurrentLocationButton = view.findViewById(R.id.button_get_current_location);

        frameLayout = view.findViewById(R.id.frameLayout_location_onboarding);

        //Initialise fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        //Click listeners
        getCurrentLocationButton.setOnClickListener(this);

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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GeoLocationModel geoLocationModel = new EasyLocationFetch(requireContext(),getResources().getString(R.string.location_api)).getLocationData();
        Toast.makeText(getContext(),"address = "+geoLocationModel.getCity(),Toast.LENGTH_LONG).show();
//        geoLocationModel.getAddress()
//        geoLocationModel.getCity()
//        geoLocationModel.getLattitude()
//        geoLocationModel.getLongitude()

//        city.setText(geoLocationModel.getCity());
//        country.setText(geoLocationModel.());

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_get_current_location) {

            if(!checkPermission()){
                requestPermission();
            }else {
                getLocation();
                getCurrentLocationButton.setVisibility(View.GONE);
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(requireActivity(), new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {
                        getLocation();
                        getCurrentLocationButton.setVisibility(View.GONE);
                    }
                    else {

                        Snackbar.make(frameLayout, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to this permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getCurrentLocationButton.setVisibility(View.VISIBLE);
                    }
                })
                .create()
                .show();
    }
}

