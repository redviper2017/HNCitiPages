package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hnapp.Activities.PostAudioActivity;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.Location;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitSectionFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "VisitSectionFragment";
    private Utils myUtils;
    private Profile userProfile;
    private ArrayList<String> citiesList, countriesList;
    private FloatingActionButton changeLocationButton;


    public VisitSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_visitsection);
        MaterialTextView location    = view.findViewById(R.id.location_visitsection);
        changeLocationButton         = view.findViewById(R.id.change_location_fab);

        citiesList    = new ArrayList<>();
        countriesList = new ArrayList<>();

        getLocations();

        //Getting user profile from local storage
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        String locationText = userProfile.getCity() + ", " + userProfile.getCountry();

        screenTitle.setText(R.string.visit_section);
        location.setText(locationText);

        changeLocationButton.setOnClickListener(this);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.change_location_fab)
            showLocationDialog();
    }

    public void getLocations(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Location> call = service.getLocations();
        call.enqueue(new Callback<Location>() {
            @Override
            public void onResponse(Call<Location> call, Response<Location> response) {
                if(response.code() == 200){
                    Location location = response.body();
                    if (location != null) {
                        citiesList.addAll(location.getCities());
                        countriesList.addAll(location.getCountries());

                        Log.d(TAG,"number of cities = " + citiesList.size() + " & number of countries = " + countriesList.size());
                    }

                }
            }

            @Override
            public void onFailure(Call<Location> call, Throwable t) {

            }
        });
    }

    public void showLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View alertView = getLayoutInflater().inflate(R.layout.location_alert_layout, null);

        Spinner countrySpinner = alertView.findViewById(R.id.spinner_country);
        Spinner citySpinner = alertView.findViewById(R.id.spinner_city);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>((getContext()),
                android.R.layout.simple_spinner_dropdown_item, countriesList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;

            }
        };
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        countrySpinner.setSelection(0);

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>((getContext()),
                android.R.layout.simple_spinner_dropdown_item, citiesList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;

            }
        };
        adapterCity.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapterCity);
        citySpinner.setSelection(0);

        builder.setView(alertView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}