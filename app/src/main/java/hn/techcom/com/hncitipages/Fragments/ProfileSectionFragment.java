package hn.techcom.com.hncitipages.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;

import hn.techcom.com.hncitipages.Activities.SupportersOrSuppoertingProfilesActivity;
import hn.techcom.com.hncitipages.Activities.UserProfileActivity;
import hn.techcom.com.hncitipages.Activities.ViewCommentsActivity;
import hn.techcom.com.hncitipages.Activities.ViewLikesActivity;
import hn.techcom.com.hncitipages.Adapters.ProfilePostAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Interfaces.OnPostCountClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnSupporterSupportingCountClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnUpdateProfileClickListener;
import hn.techcom.com.hncitipages.Models.FavoriteResponse;
import hn.techcom.com.hncitipages.Models.LikeResponse;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Models.SupportingProfileList;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSectionFragment
        extends Fragment
        implements
        OnOptionsButtonClickListener,
        OnLikeButtonClickListener,
        OnFavoriteButtonClickListener,
        OnLikeCountButtonListener,
        OnCommentClickListener,
        OnPlayerPlayedListener,
        OnUpdateProfileClickListener,
        OnPostCountClickListener,
        OnSupporterSupportingCountClickListener {

    private static final String TAG = "ProfileSectionFragment";
    private Utils myUtils;
    private Profile userProfile;
    private ArrayList<User> supportingProfilesArrayList, supporterProfilesArrayList;
    private ArrayList<Result> initialPostList;
    private int postCount, supportingProfileCount, supporterProfileCount;
    private String nextPageUrl;
    private ProfilePostAdapter profilePostAdapter;

    private MaterialTextView postCountText, supportingCountText, supporterCountText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;

    public ProfileSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_section, container, false);

        //getting user profile from local storage
        myUtils                     = new Utils();
        userProfile                 = myUtils.getNewUserFromSharedPreference(getContext());

        supporterProfilesArrayList  = new ArrayList<>();
        supportingProfilesArrayList = new ArrayList<>();
        initialPostList             = new ArrayList<>();

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_profile);
        postCountText                = view.findViewById(R.id.post_count_viewprofile);
        supportingCountText          = view.findViewById(R.id.supporting_count_viewprofile);
        supporterCountText           = view.findViewById(R.id.supporter_count_viewprofile);
        recyclerView                 = view.findViewById(R.id.recyclerview_posts_profile_section);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);

        getLatestPostsListBySingleUser(userProfile.getHnid());
        getSupportingProfiles();
        getSupporterProfiles();

        screenTitle.setText(R.string.my_profile);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialPostList.clear();
                getLatestPostsListBySingleUser(userProfile.getHnid());
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
                    if (initialPostList.get(initialPostList.size()-1) == null) {
                        initialPostList.remove(initialPostList.size() - 1);
                        getPostsListBySingleUserFromPage(nextPageUrl);
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //get initial supporting profile list
    public void getSupportingProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupportingProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supportingProfileList = response.body();
                    Log.d(TAG, "number of supporting profile = "+ supportingProfileList.getCount());
                    supportingProfileCount = supportingProfileList.getCount();
                    if(supportingProfileList.getCount()>0) {
                        supportingProfilesArrayList.addAll(supportingProfileList.getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }

    //get initial supporter profile list
    public void getSupporterProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupporterProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supporterProfileList = response.body();
                    Log.d(TAG, "number of supporter profile = "+ supporterProfileList.getCount());
                    supporterProfileCount = supporterProfileList.getCount();
                    if(supporterProfileList.getCount()>0) {
                        supporterProfilesArrayList.addAll(supporterProfileList.getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }

    //get initial user posts list
    public void getLatestPostsListBySingleUser(String target_hnid) {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsBySingleUser(target_hnid,userProfile.getHnid());
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.code() == 200){
                    Log.d(TAG,"total number of post by this user = "+response.body().getResults().size());
                    PostList postList = response.body();

                    postCount = postList.getCount();

                    if (postList.getCount() > 0) {
                        initialPostList.clear();
                        initialPostList.addAll(postList.getResults());
                    }

                    if (postList.getNext() != null) {
                        nextPageUrl = postList.getNext();
                        initialPostList.add(null);
                    }

                    initialPostList.add(0,initialPostList.get(0));
                    setRecyclerView(initialPostList);
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    //get user posts list from next page
    public void getPostsListBySingleUserFromPage(String nextPage){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsBySingleUserFromPage(nextPage);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
                    Log.d(TAG, "total number of supporting profile posts = "+postList.getCount());
                    if(postList != null){
                        nextPageUrl = postList.getNext();
                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());

                        initialPostList.remove(initialPostList.size() - 1);
                        initialPostList.addAll(postArrayList);


                        profilePostAdapter.notifyDataSetChanged();
                        if (postList.getNext() != null) {
                            initialPostList.add(null);
                            Log.d(TAG, "total number of user posts fetched = " + initialPostList.size());
                            getPostsListBySingleUserFromPage(postList.getNext());
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profilePostAdapter = new ProfilePostAdapter(
                initialPostList,
                postCount,
                supporterProfileCount,
                supportingProfileCount,
                getContext(),
                this,
                this,
                this,
                this,
                this,
                this,
                this,
                this,
                this);
        recyclerView.setAdapter(profilePostAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        onPlayerPlayed(playerView, imageView, playButton);
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
        Intent intent = new Intent(getContext(), ViewLikesActivity.class);
        intent.putExtra("POST_ID",postId);
        startActivity(intent);
    }

    @Override
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {
//        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment(position, postId, initialPostList, profilePostAdapter, hnid_user, supporting);
//        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
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

    @Override
    public void onUpdateProfileClick() {
        startActivity(new Intent(getContext(),UserProfileActivity.class));
    }

    @Override
    public void onPostCountClick() {
        initialPostList.clear();
        getLatestPostsListBySingleUser(userProfile.getHnid());
    }


    @Override
    public void onSupporterSupportingCountClick(String show, String count) {
        Intent intent = new Intent(getContext(), SupportersOrSuppoertingProfilesActivity.class);
        intent.putExtra("Show", show);
        if (show.equals("Supporters"))
            intent.putExtra("SupporterCount", String.valueOf(supporterProfilesArrayList.size()));
        else
            intent.putExtra("SupportingCount", String.valueOf(supportingProfilesArrayList.size()));

        storeProfiles(show);

        startActivity(intent);
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
                    initialPostList.get(position).setFavourite(!initialPostList.get(position).getFavourite());

                    profilePostAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to like the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Your request has been failed! Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
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
                    initialPostList.get(position).setLiked(!initialPostList.get(position).getLiked());

                    //Toggling like count on post
                    if(initialPostList.get(position).getLiked())
                        initialPostList.get(position).setLikeCount(initialPostList.get(position).getLikeCount() + 1);
                    else
                        initialPostList.get(position).setLikeCount(initialPostList.get(position).getLikeCount() - 1);

                    profilePostAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to like the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Your request has been failed! Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void storeRecentPosts(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(initialPostList);
        editor.putString("RecentPosts", json);
        editor.apply();
    }

    private void storeProfiles(String profilesListType){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json;
        if (profilesListType.equals("Supporters"))
            json = gson.toJson(supporterProfilesArrayList);
        else
            json = gson.toJson(supportingProfilesArrayList);

        editor.putString(profilesListType, json);
        editor.apply();
    }
}