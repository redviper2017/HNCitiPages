package hn.techcom.com.hncitipages.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Activities.UpdateProfileActivity;
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
import hn.techcom.com.hncitipages.Models.SingleUserInfoResponse;
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

    private ArrayList<User> supportingProfilesArrayList, supporterProfilesArrayList;
    private ArrayList<Result> initialPostList;

    private String nextPageUrl,hnid,name,type,postId;
    private boolean isSupported;
    private ProfilePostAdapter profilePostAdapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout profileInfoLayout;
    private SingleUserInfoResponse profile;
    private Profile userProfile;
    private MaterialTextView screenTitle;

    //for users with no posts
    private MaterialTextView postCountText, supportingCountText, supporterCountText, nameText, locationText, usernameText, updateProfileButton;
    private CircleImageView profilePhoto;
    private View profilePhotoRing, shimmerProfileInfoLayout;

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
        screenTitle                  = view.findViewById(R.id.text_screen_title_profile);

        recyclerView                 = view.findViewById(R.id.recyclerview_posts_profile_section);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);
        profileInfoLayout            = view.findViewById(R.id.profileinfo_layout);
        shimmerProfileInfoLayout     = view.findViewById(R.id.shimmer_profileinfo_layout);

        //for users with no posts
        postCountText         = view.findViewById(R.id.post_count_viewprofile_section);
        supporterCountText    = view.findViewById(R.id.supporter_count_viewprofile_section);
        supportingCountText   = view.findViewById(R.id.supporting_count_viewprofile_section);
        nameText              = view.findViewById(R.id.profile_name_section);
        locationText          = view.findViewById(R.id.profile_location_section);
        usernameText          = view.findViewById(R.id.profile_username_section);
        updateProfileButton   = view.findViewById(R.id.update_profile_button_section);
        profilePhoto          = view.findViewById(R.id.circleimageview_profile_view_section);
        profilePhotoRing      = view.findViewById(R.id.profile_circle_view_section);

        Bundle bundle = this.getArguments();


        hnid = bundle.getString("hnid");
        nameText.setText(hnid);

        getUserProfile();

        // Inflate the layout for this fragment
        return view;
    }

    //get user profile
    public void getUserProfile(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SingleUserInfoResponse> call = service.getUserInfo(hnid);
        call.enqueue(new Callback<SingleUserInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<SingleUserInfoResponse> call, @NonNull Response<SingleUserInfoResponse> response) {
                if (response.code() == 200) {
                    profile = response.body();

                    if (profile.getPostCount()>0)
                        getLatestPostsListBySingleUser();
                    else {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        if (userProfile.getHnid().equals(profile.getHnid()))
                            screenTitle.setText(R.string.my_profile);
                        else
                            screenTitle.setText(profile.getFullName());

                        //removing all layouts except for profile info layout
                        shimmerFrameLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        shimmerProfileInfoLayout.setVisibility(View.VISIBLE);
                        setUserProfileInfo(profile);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleUserInfoResponse> call, @NonNull Throwable t) {

            }
        });
    }

    //for users with no post
    public void setUserProfileInfo(SingleUserInfoResponse profile){
        Log.d(TAG,"set user profile called = "+profile.getCountry());
        postCountText.setText(String.valueOf(profile.getPostCount()));
        supporterCountText.setText(String.valueOf(profile.getSupporterCount()));
        supportingCountText.setText(String.valueOf(profile.getSupportingCount()));
        nameText.setText(profile.getFullName());

        String location = profile.getCity() + ", " + profile.getCountry();
        locationText.setText(location);

        usernameText.setText(profile.getUsername());
        Glide.with(requireContext()).load(profile.getProfileImgThumbnail()).centerCrop().into(profilePhoto);

        if (profile.getHnid().equals(userProfile.getHnid())){
            updateProfileButton.setText(R.string.update_profile);
            updateProfileButton.setTag("update");
        }else {
            if (isSupported) {
                profilePhotoRing.setVisibility(View.VISIBLE);
                profilePhotoRing.setBackground(ResourcesCompat.getDrawable(requireContext().getResources(),R.drawable.circle_hollow,requireContext().getTheme()));
                updateProfileButton.setText(R.string.un_support_user);
            }
            else{
//                    profilePhotoRing.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.circle_hollow_1,context.getTheme()));
                profilePhotoRing.setVisibility(View.GONE);
                updateProfileButton.setText(R.string.support_user);
            }
            updateProfileButton.setTag("support");
        }
        shimmerProfileInfoLayout.setVisibility(View.GONE);
        profileInfoLayout.setVisibility(View.VISIBLE);
    }

    //get initial user posts list
    public void getLatestPostsListBySingleUser() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsBySingleUser(hnid,userProfile.getHnid());
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if (response.code() == 200){
                    Log.d(TAG,"total number of post by this user = "+response.body().getResults().size());
                    PostList latestPostListByUser = response.body();

                    ArrayList<Result> postList = new ArrayList<>(latestPostListByUser.getResults());
                    postList = myUtils.setPostRelativeTime(postList);
                    initialPostList.clear();
                    initialPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));
                    initialPostList.add(0, initialPostList.get(0));
                    if (latestPostListByUser.getNext() != null) {
                        nextPageUrl = latestPostListByUser.getNext();
                        initialPostList.add(null);
                    }
                    setRecyclerView(initialPostList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {

            }
        });
    }

    //get user posts list from next page
    public void getPostsListBySingleUserFromPage(String nextPage){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsBySingleUserFromPage(nextPage);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if(response.code() == 200){
                    PostList postListByUser = response.body();
//                    Log.d(TAG, "total number of supporting profile posts = "+postListByUser.getCount());
                    if(postListByUser != null){
                        nextPageUrl = postListByUser.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postListByUser.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        initialPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));


                        profilePostAdapter.notifyItemRangeChanged(0,initialPostList.size());
                        if (postListByUser.getNext() != null) {
                            initialPostList.add(null);
                            Log.d(TAG, "total number of user posts fetched = " + initialPostList.size());
                            getPostsListBySingleUserFromPage(postListByUser.getNext());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {

            }
        });
    }

    public void getSinglePostNotified(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Result> call = service.getSinglePost(postId);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if (response.code() == 200){
                    initialPostList.clear();
                    Result post = response.body();
                    initialPostList.add(post);
                    initialPostList.add(post);
                    initialPostList = myUtils.setPostRelativeTime(initialPostList);
                    setRecyclerView(initialPostList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {

            }
        });
    }


    public void setRecyclerView(ArrayList<Result> postList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profilePostAdapter = new ProfilePostAdapter(
                postList,
                profile,
                isSupported,
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
    public void onCommentClick(int postId, int count) {
        myUtils.onCommentCountClick(postId, count, getContext());
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
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {
        InteractWithPostBottomSheetFragment2 interactWithPostBottomSheetFragment2 = new InteractWithPostBottomSheetFragment2(position, postId, initialPostList, profilePostAdapter, hnid_user, supporting);
        interactWithPostBottomSheetFragment2.show(getParentFragmentManager(), interactWithPostBottomSheetFragment2.getTag());
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
//        Fragment fragment = new UserProfileFragment();
//        ((AppCompatActivity) requireContext()).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment),null).addToBackStack(null).commit();
        startActivity(new Intent(getContext(), UpdateProfileActivity.class));
    }

    @Override
    public void onPostCountClick() {
        initialPostList.clear();
        getLatestPostsListBySingleUser();
    }


    @Override
    public void onSupporterSupportingCountClick(String show, String count, String hnid) {
        Fragment fragment = new SupportingSupporterListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("show", show);
        bundle.putString("count", count);
        bundle.putString("hnid", hnid);

        fragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.framelayout_main, fragment,null).addToBackStack(null).commit();
    }

    //favorite or un-favorite post
    public void favoriteOrUnfavoritePost(String hnid, int postId, int position){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<FavoriteResponse> call = service.favoriteOrUnfavoritePost(user,post);

        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                if(response.code() == 201){
                    FavoriteResponse favoriteResponse = response.body();
//                    Toast.makeText(getContext(), favoriteResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Toggling favorite button image
                    initialPostList.get(position).setFavourite(!initialPostList.get(position).getFavourite());

                    profilePostAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to favorite the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<LikeResponse> call, @NonNull Response<LikeResponse> response) {

                    LikeResponse likeResponse = response.body();
//                    Toast.makeText(getContext(), likeResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Toggling like button image
                    initialPostList.get(position).setLiked(!initialPostList.get(position).getLiked());

                    //Toggling like count on post
                    if(initialPostList.get(position).getLiked())
                        initialPostList.get(position).setLikeCount(initialPostList.get(position).getLikeCount() + 1);
                    else
                        initialPostList.get(position).setLikeCount(initialPostList.get(position).getLikeCount() - 1);

                    profilePostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<LikeResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Your request has been failed! Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });
    }
}