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
import android.widget.LinearLayout;
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
import hn.techcom.com.hncitipages.Adapters.SupportingPostAdapter;
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
        OnAvatarLongClickListener,
        OnPlayerPlayedListener,
        ViewProfileListener {

    private Utils myUtils;
    private Profile userProfile;

    private RecyclerView recyclerView;
    private EditText searchView;
    private SupportingPostAdapter supportingPostAdapter;
    private String nextSupportingPostListUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;

    private ArrayList<Result> recentPostList;
    private SupportingProfileList allProfiles;

    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout notFoundLayout;

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
        recyclerView                 = view.findViewById(R.id.recyclerview_posts_supportsection);
        searchView                   = view.findViewById(R.id.searchview_supportedsection);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);
        notFoundLayout               = view.findViewById(R.id.notfound_layout);

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
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
//                {
//                    searchView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                if (dy > 0 || dy<0 && searchView.getVisibility() == View.VISIBLE)
//                    searchView.setVisibility(View.GONE);

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

        supportingPostAdapter.filterList(filterNames);
    }

    //get initial supporting profile list
    public void getSupportingProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupportingProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(@NonNull Call<SupportingProfileList> call, @NonNull Response<SupportingProfileList> response) {
                if(response.code() == 200) allProfiles = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<SupportingProfileList> call, @NonNull Throwable t) {

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
                    if (postList != null && postList.getCount() > 0) {
                        Log.d(TAG, "number of supporting profile posts = " + postList.getCount());
                        nextSupportingPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        recentPostList.clear();
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));
                        if (postList.getNext() != null)
                            recentPostList.add(null);
                        recentPostList.add(0, recentPostList.get(0));
                        setRecyclerView(recentPostList);
                    }else {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        notFoundLayout.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
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
                    nextSupportingPostListUrl = postList.getNext();

                    ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                    postArrayList = myUtils.setPostRelativeTime(postArrayList);

                    recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));

                    supportingPostAdapter.notifyDataSetChanged();
                    if (postList.getNext() != null) {
                        recentPostList.add(null);
                        Log.d(TAG,"total number of global posts fetched = "+recentPostList.size());
                        getSupportingProfilePostsFromNextPage(postList.getNext());
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

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        supportingPostAdapter = new SupportingPostAdapter(
                getContext(),
                postList,
                allProfiles,
                this,
                this,
                this,
                this,
                this,
                this,
                this);
        recyclerView.setAdapter(supportingPostAdapter);
        swipeRefreshLayout.setRefreshing(false);
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
        InterectionWithBottomSheetFragment1 interectionWithBottomSheetFragment1 = new InterectionWithBottomSheetFragment1(position, postId, recentPostList, supportingPostAdapter, hnid_user, supporting);
        interectionWithBottomSheetFragment1.show(getParentFragmentManager(), interectionWithBottomSheetFragment1.getTag());
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

                    LikeResponse likeResponse = response.body();
//                    Toast.makeText(getContext(), likeResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Toggling like button image
                    recentPostList.get(position).setLiked(!recentPostList.get(position).getLiked());

                    //Toggling like count on post
                    if(recentPostList.get(position).getLiked())
                        recentPostList.get(position).setLikeCount(recentPostList.get(position).getLikeCount() + 1);
                    else
                        recentPostList.get(position).setLikeCount(recentPostList.get(position).getLikeCount() - 1);

                    supportingPostAdapter.notifyDataSetChanged();
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
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                if(response.code() == 201){
                    FavoriteResponse favoriteResponse = response.body();

                    //Toggling favorite button image
                    recentPostList.get(position).setFavourite(!recentPostList.get(position).getFavourite());

                    supportingPostAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), "Sorry unable to favorite the post at this moment, try again later.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
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

    @Override
    public void viewProfile(String hnid, String name, boolean isSupported) {
        Fragment fragment = new ProfileSectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("hnid",hnid);
        bundle.putString("name",name);
        bundle.putBoolean("isSupported",isSupported);

        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }
}