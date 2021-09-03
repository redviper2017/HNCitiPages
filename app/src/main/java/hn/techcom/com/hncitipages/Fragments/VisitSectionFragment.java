package hn.techcom.com.hncitipages.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;
import java.util.List;

import hn.techcom.com.hncitipages.Activities.ViewCommentsActivity;
import hn.techcom.com.hncitipages.Adapters.PostListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Models.FavoriteResponse;
import hn.techcom.com.hncitipages.Models.LikeResponse;
import hn.techcom.com.hncitipages.Models.Location;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
        OnLoadMoreListener,
        OnPlayerPlayedListener {

    private static final String TAG = "VisitSectionFragment";
    private Utils myUtils;
    private Profile userProfile;
    private ArrayList<String> citiesList, countriesList;
    private ArrayList<Result> recentPostList;
    ArrayList<Location> locations;
    private String nextCityPostListUrl, nextCountryPostListUrl, citySelected, countrySelected, locationText;

    private FloatingActionButton changeLocationButton, currentLocationButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AlertDialog dialog;
    private MaterialTextView location;
    private LinearLayout cityLayout;
    private PostListAdapter postListAdapter;

    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;

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
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);
        citiesList     = new ArrayList<>();
        countriesList  = new ArrayList<>();
        recentPostList = new ArrayList<>();
        locations = new ArrayList<>();

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
    public void onPause() {
        super.onPause();
        onPlayerPlayed(playerView, imageView, playButton);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.change_location_fab)
            showLocationDialog();
        if (view.getId() == R.id.current_location_fab) {
            countrySelected = userProfile.getCountry();
            citySelected = userProfile.getCity();
//            Toast.makeText(getContext(),"Fetching posts from "+locationText+"...",Toast.LENGTH_LONG).show();
            getLatestPostsByCity(userProfile.getCity());
            location.setText(locationText);
        }

    }

    public void getLocations(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Location>> call = service.getLocations();

        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.code() == 200){
                    List<Location> locationList = response.body();
                    if (locationList != null && locationList.size() > 0) {
                        locations = new ArrayList<>(locationList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {

            }
        });
    }

    public void showLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View alertView = getLayoutInflater().inflate(R.layout.location_alert_layout, null);

        Spinner countrySpinner = alertView.findViewById(R.id.spinner_country);
        Spinner citySpinner = alertView.findViewById(R.id.spinner_city);
        MaterialCardView visitButton = alertView.findViewById(R.id.button_visit_dialog);
        MaterialCardView closeButton = alertView.findViewById(R.id.button_clear_dialog);
        cityLayout                   = alertView.findViewById(R.id.parent_layout_city);

        countriesList.clear();
        countriesList.add("Select a country");
        for(Location location: locations) {
            if (location.getCountry()!=null)
                countriesList.add(location.getCountry());
        }

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

        builder.setView(alertView);
        dialog = builder.create();
        dialog.show();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countrySelected = parent.getItemAtPosition(position).toString();
                for (Location location : locations)
                    if (location.getCountry() != null) {
                        if (location.getCountry().equals(countrySelected)) {
                            citiesList.clear();
                            citiesList.add("Select a city");
                            citiesList.add("All");
                            citiesList.addAll(location.getCities());
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

                            cityLayout.setVisibility(View.VISIBLE);
                        }
                    }
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
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

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
                        postList = myUtils.setPostRelativeTime(postList);

                        recentPostList.clear();
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));

                        if (nextCityPostListUrl != null)
                            recentPostList.add(null);

                        setRecyclerView(recentPostList);

                        locationText = citySelected+", "+countrySelected;
                        location.setText(locationText);
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
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));

                        if (nextCityPostListUrl != null)
                            recentPostList.add(null);

                        setRecyclerView(recentPostList);
                        locationText = countrySelected;
                        location.setText(locationText);
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
                        postList = myUtils.setPostRelativeTime(postList);

                        recentPostList.remove(recentPostList.size() - 1);
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));

                        postListAdapter.notifyDataSetChanged();

                        if(nextCityPostListUrl != null)
                            recentPostList.add(null);
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


                        recentPostList.remove(recentPostList.size() - 1);
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));


                        postListAdapter.notifyDataSetChanged();

                        if(nextCountryPostListUrl != null) {
                            recentPostList.add(null);
                            getCountryPostsFromNextPage(nextCountryPostListUrl);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    public void setRecyclerView(ArrayList<Result> postList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postListAdapter = new PostListAdapter(
                postList,
                getContext(),
                this,
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
        Intent intent = new Intent(getContext(), ViewCommentsActivity.class);
        intent.putExtra("POST_ID",postId);
        startActivity(intent);
    }

    @Override
    public void onFavoriteButtonClick(int position, int postId) {
        favoriteOrUnfavoritePost(userProfile.getHnid(), postId, position);
    }

    @Override
    public void onLikeButtonClick(int position, int postId) {
        likeOrUnlikeThisPost(userProfile.getHnid(), postId, position);
    }

    @Override
    public void onLikeCountButtonClick(int postId) {
        myUtils.onLikeCountClick(postId,getContext());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {
        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment(position, postId, recentPostList, postListAdapter, hnid_user, supporting);
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
    }


    public void likeOrUnlikeThisPost(String hnid, int postId, int position){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LikeResponse> call = service.likeOrUnlikePost(user,post);

        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if(response.code() == 201){
                    LikeResponse likeResponse = response.body();
                    Toast.makeText(getContext(), likeResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Toggling like button image
                    recentPostList.get(position).setLiked(!recentPostList.get(position).getLiked());

                    //Toggling like count on post
                    if(recentPostList.get(position).getLiked())
                        recentPostList.get(position).setLikeCount(recentPostList.get(position).getLikeCount() + 1);
                    else
                        recentPostList.get(position).setLikeCount(recentPostList.get(position).getLikeCount() - 1);

                    postListAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to like the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Your request has been failed! Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //favorite or un-favorite post
    public void favoriteOrUnfavoritePost(String hnid, int postId, int position){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<FavoriteResponse> call = service.favoriteOrUnfavoritePost(user,post);

        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if(response.code() == 201){
                    FavoriteResponse favoriteResponse = response.body();
                    Toast.makeText(getContext(), favoriteResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Toggling favorite button image
                    recentPostList.get(position).setFavourite(!recentPostList.get(position).getFavourite());

                    postListAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to like the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Your request has been failed! Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPlayerPlayed(AndExoPlayerView playerView, ImageView imageview, ImageView playButton) {
        if (this.playerView != null) {
            this.playerView.stopPlayer();
            this.playerView.setVisibility(View.GONE);
            if (this.imageView != null)
                this.imageView.setVisibility(View.VISIBLE);
            this.playButton.setVisibility(View.VISIBLE);
        }

        this.playerView = playerView;
        this.imageView = imageview;
        this.playButton = playButton;
    }
}