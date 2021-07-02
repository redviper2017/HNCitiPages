package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hnapp.Activities.PostAudioActivity;
import hn.techcom.com.hnapp.Adapters.PostListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Interfaces.OnCommentClickListener;
import hn.techcom.com.hnapp.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hnapp.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hnapp.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hnapp.Models.Location;
import hn.techcom.com.hnapp.Models.PostList;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitSectionFragment
        extends Fragment
        implements
        View.OnClickListener,
        OnOptionsButtonClickListener,
        OnLikeButtonClickListener,
        OnFavoriteButtonClickListener,
        OnLikeCountButtonListener,
        OnCommentClickListener,
        OnLoadMoreListener {

    private static final String TAG = "VisitSectionFragment";
    private Utils myUtils;
    private Profile userProfile;
    private ArrayList<String> citiesList, countriesList;
    private ArrayList<Result> recentPostList;
    private String nextCityPostListUrl, nextCountryPostListUrl, citySelected, countrySelected, locationText;

    private FloatingActionButton changeLocationButton, currentLocationButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  AlertDialog dialog;
    private MaterialTextView location;

    private PostListAdapter postListAdapter;

    public VisitSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_visitsection);
        location                     = view.findViewById(R.id.location_visitsection);
        changeLocationButton         = view.findViewById(R.id.change_location_fab);
        currentLocationButton        = view.findViewById(R.id.current_location_fab);
        recyclerView                 = view.findViewById(R.id.recyclerview_posts_visitsection);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);

        citiesList     = new ArrayList<>();
        countriesList  = new ArrayList<>();
        recentPostList = new ArrayList<>();

        //Getting user profile from local storage
        myUtils     = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        citySelected = userProfile.getCity();
        countrySelected = userProfile.getCountry();

        locationText = userProfile.getCity() + ", " + userProfile.getCountry();

        screenTitle.setText(R.string.visit_section);
        location.setText(locationText);

        changeLocationButton.setOnClickListener(this);
        currentLocationButton.setOnClickListener(this);

        getLocations();
        getLatestPostsByCity(citySelected);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!citySelected.equals("All")) {
                    recentPostList.clear();
                    getLatestPostsByCity(citySelected);
                }else {
                    recentPostList.clear();
                    getLatestPostsByCountry(countrySelected);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && dy>0){
                    //scrolled to bottom
                    Log.d(TAG,"Recycler view scroll position = "+"BOTTOM");
                    if (recentPostList.get(recentPostList.size()-1) == null) {
                        recentPostList.remove(recentPostList.size() - 1);
                        if (!citySelected.equals("All"))
                            getCityPostsFromNextPage(nextCityPostListUrl);
                        else
                            getCountryPostsFromNextPage(nextCountryPostListUrl);

                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.change_location_fab)
            showLocationDialog();
        if (view.getId() == R.id.current_location_fab) {
            Toast.makeText(getContext(),"Fetching posts from "+locationText+"...",Toast.LENGTH_LONG).show();
            getLatestPostsByCity(userProfile.getCity());
            location.setText(locationText);
        }

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

                        citiesList.add(0,"All");

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
        MaterialCardView visitButton = alertView.findViewById(R.id.button_visit_dialog);
        MaterialCardView closeButton = alertView.findViewById(R.id.button_close_dialog);

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
        dialog = builder.create();
        dialog.show();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countrySelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        visitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!citySelected.equals("All"))
                    getLatestPostsByCity(citySelected);
                else
                    getLatestPostsByCountry(countrySelected);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public void getLatestPostsByCity(String city){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsFromCity(userProfile.getHnid(),city);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList cityPostList = response.body();
                    if (cityPostList.getCount() != 0){
                        location.setText(city);

                        if (dialog!=null)
                            dialog.cancel();

                        nextCityPostListUrl = cityPostList.getNext();

                        ArrayList<Result> postList = new ArrayList<>(cityPostList.getResults());

                        recentPostList.clear();
                        recentPostList.addAll(postList);

                        if (nextCityPostListUrl != null)
                            recentPostList.add(null);

                        setRecyclerView(recentPostList);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    public void getLatestPostsByCountry(String country){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsFromCountry(userProfile.getHnid(),country);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList countryPostList = response.body();
                    if (countryPostList.getCount() != 0){
                        location.setText(country);
                        if (dialog!=null)
                            dialog.cancel();

                        nextCountryPostListUrl = countryPostList.getNext();

                        ArrayList<Result> postList = new ArrayList<>(countryPostList.getResults());

                        recentPostList.clear();
                        recentPostList.addAll(postList);

                        if (nextCityPostListUrl != null)
                            recentPostList.add(null);

                        setRecyclerView(recentPostList);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    public void getCityPostsFromNextPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsFromCityFromPage(nextCityPostListUrl);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList cityPostList = response.body();
                    if (cityPostList.getCount() != 0){
                        nextCityPostListUrl = cityPostList.getNext();

                        ArrayList<Result> postList = new ArrayList<>(cityPostList.getResults());

                        recentPostList.addAll(postList);
                        recentPostList.add(null);

                        postListAdapter.notifyDataSetChanged();

                        if(nextCityPostListUrl != null)
                            getCityPostsFromNextPage(nextCityPostListUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    public void getCountryPostsFromNextPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsFromCountryFromPage(nextCountryPostListUrl);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList countryPostList = response.body();
                    if (countryPostList.getCount() != 0){
                        nextCountryPostListUrl = countryPostList.getNext();

                        ArrayList<Result> postList = new ArrayList<>(countryPostList.getResults());

                        recentPostList.addAll(postList);
                        recentPostList.add(null);

                        postListAdapter.notifyDataSetChanged();

                        if(nextCountryPostListUrl != null)
                            getCountryPostsFromNextPage(nextCountryPostListUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    public void setRecyclerView(ArrayList<Result> postList){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postListAdapter = new PostListAdapter(
                postList,
                getContext(),
                this,
                this,
                this,
                this,
                this,
                this);
        recyclerView.setAdapter(postListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCommentClick(int postId) {

    }

    @Override
    public void onFavoriteButtonClick(int position, int postId) {

    }

    @Override
    public void onLikeButtonClick(int position, int postId) {

    }

    @Override
    public void onLikeCountButtonClick(int postId) {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {

    }
}