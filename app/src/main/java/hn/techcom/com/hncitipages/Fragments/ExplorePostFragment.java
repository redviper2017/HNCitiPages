package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;

import hn.techcom.com.hncitipages.Adapters.PostListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
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
        OnLoadMoreListener, OnPlayerPlayedListener {

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
        // Inflate the layout for this fragment
        return view;
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
                        recentPostList.remove(recentPostList.size() - 1);
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

    @Override
    public void onPlayerPlayed(AndExoPlayerView playerView, ImageView imageview, ImageView playButton) {

    }
}