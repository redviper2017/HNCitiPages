package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.PostListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
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
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplorePostFragment
        extends Fragment
        implements
        OnOptionsButtonClickListener,
        OnLikeButtonClickListener,
        OnFavoriteButtonClickListener,
        OnLikeCountButtonListener,
        OnCommentClickListener,
        OnLoadMoreListener,
        OnPlayerPlayedListener,
        ViewProfileListener {

    private String postType;
    private Utils myUtils;
    private Profile userProfile;
    private String nextPageUrl;
    private ArrayList<Result> recentPostList;
    private static final String TAG = "ExplorePostFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostListAdapter postListAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;

    public ExplorePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_post, container, false);

        //Getting user profile from local storage
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        recentPostList = new ArrayList<>();

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_explore_post);
        recyclerView                 = view.findViewById(R.id.recyclerview_explore_post);
        swipeRefreshLayout           = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);
        EditText searchView          = view.findViewById(R.id.searchview_explore_post);

        Bundle bundle = this.getArguments();

        if(bundle != null){
            postType = bundle.getString("type");
            screenTitle.setText(postType);
        }

        switch (postType){
            case "Stories & Sayings":
                getLatestPosts("S");
                break;
            case "Images & Moments":
                getLatestPosts("I");
                break;
            case "Audios & Music":
                getLatestPosts("A");
                break;
            case "Videos & Memories":
                getLatestPosts("V");
                break;
        }

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
                    if (recentPostList.get(recentPostList.size()-1) == null) {
                        recentPostList.remove(recentPostList.size() - 1);
                        getPostsFromNextPage(nextPageUrl);
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    recentPostList.clear();
                    switch (screenTitle.getText().toString()) {
                        case "Stories & Sayings":
                            getLatestPosts("S");
                            break;
                        case "Images & Moments":
                            getLatestPosts("I");
                            break;
                        case "Audios & Music":
                            getLatestPosts("A");
                            break;
                        case "Videos & Memories":
                            getLatestPosts("V");
                            break;
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

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        onPlayerPlayed(playerView, imageView, playButton);
    }

    @Override
    public void onCommentClick(int postId,int count) {
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
    public void onLoadMore() {

    }

    @Override
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {
        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment(position, postId, recentPostList, postListAdapter, hnid_user, supporting);
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
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


    //get initial global posts list
    public void getLatestPosts(String type){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        Log.d(TAG,"getLatestPost called = "+"YES");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostByType(userProfile.getHnid(), type);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if (response.code() == 200){
                    PostList latestPostList = response.body();
                    if (latestPostList != null){
                        Log.d(TAG,"total posts of type " + postType+  " = " + latestPostList.getCount());

                        nextPageUrl = latestPostList.getNext();
                        ArrayList<Result> postList = new ArrayList<>(latestPostList.getResults());
                        postList = myUtils.setPostRelativeTime(postList);
                        recentPostList.clear();
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));

                        if(latestPostList.getNext() != null)
                            recentPostList.add(null);

                        setRecyclerView(recentPostList);
                    }
                }else
                    Log.d(TAG,"total posts of type N/A = N/A");
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {
                Log.d(TAG,"total posts of type N/A = N/A");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //get global posts list from next page
    public void getPostsFromNextPage(String pageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsByTypeFromPage(pageUrl);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if(response.code() == 200){
                    PostList latestPostList = response.body();
                    if (latestPostList != null){
                        nextPageUrl = latestPostList.getNext();
                        ArrayList<Result> postList = new ArrayList<>(latestPostList.getResults());
                        postList = myUtils.setPostRelativeTime(postList);
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postList));
                        postListAdapter.notifyDataSetChanged();
                        if (latestPostList.getNext() != null) {
                            recentPostList.add(null);
                            Log.d(TAG,"total number of global posts fetched = "+recentPostList.size());
                            getPostsFromNextPage(nextPageUrl);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {

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
                this,
                this);
        recyclerView.setAdapter(postListAdapter);
        swipeRefreshLayout.setRefreshing(false);
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

    private void filter(String text) {
        ArrayList<Result> filterNames = new ArrayList<>();

        for (Result post : recentPostList)
            if(post != null)
                if (post.getUser().getFullName().toLowerCase().contains(text))
                    filterNames.add(post);

        if(postListAdapter != null)
            postListAdapter.filterList(filterNames);
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