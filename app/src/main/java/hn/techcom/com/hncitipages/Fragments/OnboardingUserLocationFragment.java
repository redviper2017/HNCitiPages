package hn.techcom.com.hncitipages.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.NetworkLocation;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class OnboardingUserLocationFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "LocationFragment";
    private MapView mMapView;
    private GoogleMap googleMap;
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
        mMapView = view.findViewById(R.id.myMap);
        frameLayout = view.findViewById(R.id.frameLayout_location_onboarding);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

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
        mMapView.onResume();

        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        Log.d(TAG,"new user = "+userProfile.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

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
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        //Initialize address list
                        List<Address> address = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //Set latitude and longitude
                        latitude = address.get(0).getLatitude();
                        longitude = address.get(0).getLongitude();

                        //Set city and country
                        city.setText(address.get(0).getLocality());
                        country.setText(address.get(0).getCountryName());

                        Log.d(TAG, "user city: "+location.getLatitude()+ " "+ "user country: "+address.get(0).getCountryName());

                        setLocationOnMap();

                        Log.d(TAG,"User's location taken from = " + " Device");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    getNetworkLocationFromAPI();
                }
            }
        });
    }

    private void setLocationOnMap() {
        MapsInitializer.initialize(requireActivity().getApplicationContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                LatLng currentLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location").snippet("This is the location that we'll be using as your primary location for HN CitiPages."));
                // For zooming functionality
                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
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

    private void getNetworkLocationFromAPI(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NetworkLocation> call = service.getNetworkLocation();
        call.enqueue(new Callback<NetworkLocation>() {
            @Override
            public void onResponse(Call<NetworkLocation> call, Response<NetworkLocation> response) {
                if (response.code() == 200) {
                    NetworkLocation networkLocation = response.body();
                    if (networkLocation != null){

                        //Set city and country
                        city.setText(networkLocation.getCity());
                        country.setText(networkLocation.getCountry());

                        //Set latitude and longitude
                        latitude = networkLocation.getLat();
                        longitude = networkLocation.getLon();

                        Log.d(TAG,"User's location taken from = " + " Network");
                        setLocationOnMap();
                    }
                }
                else
                    Toast.makeText(getContext(),"Sorry you're location cannot be fetched at this moment, please try again.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<NetworkLocation> call, Throwable t) {

            }
        });
    }
}

