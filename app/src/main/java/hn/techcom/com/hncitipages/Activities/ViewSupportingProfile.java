package hn.techcom.com.hncitipages.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;

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
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Models.SingleUserInfoResponse;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSupportingProfile
        extends AppCompatActivity
        implements
        View.OnClickListener,
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

    private String nextPageUrl,hnid,name;
    private ProfilePostAdapter profilePostAdapter;

    private MaterialTextView postCountText, supportingCountText, supporterCountText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;
    private ShimmerFrameLayout shimmerFrameLayout;

    private SingleUserInfoResponse profile;
    private Profile userProfile;
    private boolean isSupported;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_supporting_profile);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        hnid = bundle.getString("hnid");
        isSupported = bundle.getBoolean("isSupported");

        //getting user profile from local storage
        myUtils                     = new Utils();
        userProfile                 = myUtils.getNewUserFromSharedPreference(this);
        supporterProfilesArrayList  = new ArrayList<>();
        supportingProfilesArrayList = new ArrayList<>();
        initialPostList             = new ArrayList<>();

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_view_supporting_profile);
        recyclerView                 = findViewById(R.id.recyclerview_view_supporting_profile);
        swipeRefreshLayout           = findViewById(R.id.swipeRefresh);
        shimmerFrameLayout           = findViewById(R.id.shimmerLayout);

        screenTitle.setText(name);

        getUserProfile();
        getLatestPostsListBySingleUser();

        backButton.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initialPostList.clear();
                getLatestPostsListBySingleUser();
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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }

    @Override
    public void onCommentClick(int postId, int count) {

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
    public void onOptionsButtonClick(int position, int postId, String hnid_user, boolean supporting) {

    }

    @Override
    public void onPlayerPlayed(AndExoPlayerView playerView, ImageView imageview, ImageView playButton) {

    }

    @Override
    public void onPostCountClick() {

    }

    @Override
    public void onSupporterSupportingCountClick(String show, String count) {

    }

    @Override
    public void onUpdateProfileClick() {

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
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleUserInfoResponse> call, @NonNull Throwable t) {

            }
        });
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
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.code() == 200){
                    Log.d(TAG,"total number of post by this user = "+response.body().getResults().size());
                    PostList latestPostListByUser = response.body();

                    if (latestPostListByUser.getCount() > 0) {
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

    public void setRecyclerView(ArrayList<Result> postList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        profilePostAdapter = new ProfilePostAdapter(
                initialPostList,
                profile,
                isSupported,
                this,
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
}