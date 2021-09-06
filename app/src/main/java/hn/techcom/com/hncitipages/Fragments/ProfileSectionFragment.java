package hn.techcom.com.hncitipages.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import hn.techcom.com.hncitipages.Activities.UserProfileActivity;
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
        shimmerFrameLayout           = view.findViewById(R.id.shimmerLayout);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            hnid = bundle.getString("hnid");
            name = bundle.getString("name");
        }

        getUserProfile();
        getLatestPostsListBySingleUser();

        if (hnid.equals(userProfile.getHnid())) {
            screenTitle.setText(R.string.my_profile);
        }else {
            screenTitle.setText(myUtils.capitalizeName(name));
        }

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profilePostAdapter = new ProfilePostAdapter(
                initialPostList,
                profile,
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
        getLatestPostsListBySingleUser();
    }


    @Override
    public void onSupporterSupportingCountClick(String show, String count) {
        Fragment fragment = new SupportingSupporterListFragment();
        Bundle args = new Bundle();
        args.putString("Show", show);

        if (show.equals("Supporters")) {
            args.putString("SupporterCount", String.valueOf(count));
            myUtils.storeProfiles(show, supporterProfilesArrayList, requireContext());
        }
        else {
            args.putString("SupportingCount", String.valueOf(count));
            myUtils.storeProfiles(show, supportingProfilesArrayList, requireContext());
        }

        fragment.setArguments(args);

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
}