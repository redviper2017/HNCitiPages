package hn.techcom.com.hncitipages.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.potyvideo.library.AndExoPlayerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.AvatarLoaderAdapter;
import hn.techcom.com.hncitipages.Adapters.PostListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnAvatarLongClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
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

public class SupportSectionFragment
        extends Fragment
        implements
        OnOptionsButtonClickListener,
        OnLikeButtonClickListener,
        OnFavoriteButtonClickListener,
        OnLikeCountButtonListener,
        OnCommentClickListener,
        OnLoadMoreListener,
        OnAvatarLongClickListener,
        OnPlayerPlayedListener,
        ViewProfileListener {

    private Utils myUtils;
    private Profile userProfile;

    private ProgressBar progressBar;
    private RecyclerView recyclerView, profileRecyclerView;
    private EditText searchView;
    private PostListAdapter postListAdapter;
    private AvatarLoaderAdapter avatarLoaderAdapter;
    private String nextSupportingPostListUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ShimmerFrameLayout avatarShimmerFrameLayout;
    private ArrayList<Result> recentPostList;

    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;

    //Constants
    private static final String TAG = "SupportSectionFragment";

    public SupportSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_supportsection);
        progressBar                  = view.findViewById(R.id.progress);
        recyclerView                 = view.findViewById(R.id.recyclerview_posts_supportsection);
        profileRecyclerView          = view.findViewById(R.id.recyclerview_supported_avatars_supportsection);
        searchView                   = view.findViewById(R.id.searchview_supportedsection);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);
        avatarShimmerFrameLayout     = view.findViewById(R.id.shimmerLayout_avatar);

        recentPostList = new ArrayList<>();

        //Getting user profile from local storage
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        screenTitle.setText(R.string.support_section);

        getSupportingProfiles();
        getSupportingProfilePosts();

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
                        getSupportingProfilePostsFromNextPage(nextSupportingPostListUrl);
                    }
                }
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString().toLowerCase());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentPostList.clear();
                getSupportingProfilePosts();
                getSupportingProfiles();
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

    private void filter(String text) {
        ArrayList<Result> filterNames = new ArrayList<>();

        for (Result post : recentPostList)
            if(post != null)
                if (post.getUser().getFullName().toLowerCase().contains(text))
                    filterNames.add(post);

        postListAdapter.filterList(filterNames);
    }

    //get initial supporting profile list
    public void getSupportingProfiles(){
        avatarShimmerFrameLayout.setVisibility(View.VISIBLE);
        profileRecyclerView.setVisibility(View.GONE);
        avatarShimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupportingProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supportingProfileList = response.body();
                    Log.d(TAG, "number of supporting profile = "+ supportingProfileList.getCount());

                    if(supportingProfileList.getCount()>0) {
                        ArrayList<User> profilesArraytList = new ArrayList<>();
                        profilesArraytList.addAll(supportingProfileList.getResults());
                        Log.d(TAG,"number of posts of user = "+profilesArraytList.get(0).getPostCount());
                        Log.d(TAG,"number of supporting of user = "+profilesArraytList.get(0).getSupportingCount());
                        Log.d(TAG,"number of supporter of user = "+profilesArraytList.get(0).getSupporterCount());
                        setProfilesRecyclerView(profilesArraytList);
                    }else{
                        Toast.makeText(getContext(),"Oops! seems like you haven't supported anyone yet. Please support someone and come back hear to see their posts.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }

    //get initial supporting profile posts list
    public void getSupportingProfilePosts(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestSupportingProfilePosts(userProfile.getHnid());

        call.enqueue(new Callback<PostList>(){
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
                    if(postList != null) {
                        Log.d(TAG, "number of supporting profile posts = " + postList.getCount());
                        nextSupportingPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        recentPostList.clear();
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));
                        if(postList.getNext() != null)
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

    //get supporting profile posts list from next page
    public void getSupportingProfilePostsFromNextPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getSupportingProfilePostsFromPage(nextPageUrl);

        call.enqueue(new Callback<PostList>(){
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
                    Log.d(TAG, "number of supporting profile posts = "+postList.getCount());
                    if (postList != null) {
                        nextSupportingPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));

                        postListAdapter.notifyDataSetChanged();
                        if (postList.getNext() != null) {
                            recentPostList.add(null);
                            Log.d(TAG,"total number of global posts fetched = "+recentPostList.size());
                            getSupportingProfilePostsFromNextPage(postList.getNext());
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
                postList, getContext(),
                this,
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

    public void setProfilesRecyclerView(ArrayList<User> supportingProfileList){
        avatarShimmerFrameLayout.setVisibility(View.GONE);
        profileRecyclerView.setVisibility(View.VISIBLE);

        ArrayList<String> avatarList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<String> locationList = new ArrayList<>();
        ArrayList<String> hnidList = new ArrayList<>();
        ArrayList<String> thumbnailList = new ArrayList<>();
        ArrayList<Integer> supporterCountList = new ArrayList<>();
        ArrayList<Integer> supportingCountList = new ArrayList<>();
        ArrayList<Integer> postCountList = new ArrayList<>();
        ArrayList<String> firstImageList = new ArrayList<>();

        for (User supportingProfile : supportingProfileList) {
            Log.d(TAG,"supporting count = "+supportingProfile.getSupportingCount());

            avatarList.add(supportingProfile.getProfileImgThumbnail());
            nameList.add(supportingProfile.getFullName());
            usernameList.add(supportingProfile.getUsername());
            locationList.add(supportingProfile.getCity()+", "+supportingProfile.getCountry());
            hnidList.add(supportingProfile.getHnid());
            thumbnailList.add(supportingProfile.getProfileImgThumbnail());
            supportingCountList.add(supportingProfile.getSupportingCount());
            supporterCountList.add(supportingProfile.getSupporterCount());
            postCountList.add(supportingProfile.getPostCount());
            firstImageList.add(supportingProfile.getFirstImg());
        }

        Log.d(TAG, "Supporting count list size  = "+supportingCountList.size());

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        profileRecyclerView.setLayoutManager(horizontalLayout);
        avatarLoaderAdapter = new AvatarLoaderAdapter(
                avatarList,
                nameList,
                thumbnailList,
                usernameList,
                hnidList,
                locationList,
                supporterCountList,
                supportingCountList,
                postCountList,
                firstImageList,
                this,
                getContext()
        );
        profileRecyclerView.setAdapter(avatarLoaderAdapter);
    }

    @Override
    public void onCommentClick(int postId, int count) {
        myUtils.onCommentCountClick(postId,count,getContext());
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
        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment(position, postId, recentPostList, postListAdapter, hnid_user, supporting);
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
    }

    @Override
    public void onAvatarLongClick(String hnid,
                                  String name,
                                  String thumbnail,
                                  String username,
                                  String location,
                                  int supporterCount,
                                  int supportingCount,
                                  int postCount,
                                  String firstImage) {

        Fragment fragment = new ViewProfileFragment();

        Log.d(TAG,"supportingCount = "+supportingCount);

        //passing hnid with fragment
        Bundle bundle = new Bundle();

        bundle.putString("hnid", hnid);
        bundle.putString("name", name);
        bundle.putString("username", username);
        bundle.putString("location", location);
        bundle.putString("thumbnail", thumbnail);
        bundle.putString("supporterCount", String.valueOf(supporterCount));
        bundle.putString("supportingCount", String.valueOf(supportingCount));
        bundle.putString("postCount", String.valueOf(postCount));
        bundle.putString("firstImage",firstImage);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }

    //like or un-like post
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

    private void storeRecentPosts(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recentPostList);
        editor.putString("RecentPosts", json);
        editor.apply();
    }

    private ArrayList<Result> getRecentPosts(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("RecentPosts", null);
        Type type = new TypeToken<ArrayList<Result>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void onLoadMore() {

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
    public void viewProfile(String hnid, String name) {
        Fragment fragment = new ProfileSectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("hnid",hnid);
        bundle.putString("name",name);

        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }
}