package hn.techcom.com.hncitipages.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
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

public class ViewProfileFragment
        extends Fragment
        implements
        OnOptionsButtonClickListener,
        OnLikeButtonClickListener,
        OnFavoriteButtonClickListener,
        OnLikeCountButtonListener,
        OnCommentClickListener,
        OnLoadMoreListener,
        OnPlayerPlayedListener {

    private Utils myUtils;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Result> recentPostList;
    private Profile userProfile;
    private static final String TAG = "ViewProfileFragment";
    private String nextUserPostListUrl;
    private PostListAdapter postListAdapter;

    private AlertDialog dialog;

    private AndExoPlayerView playerView;
    private ImageView imageView, playButton;


    public ViewProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        recentPostList = new ArrayList<>();

        //Hooks
        MaterialTextView screenTitle         = view.findViewById(R.id.text_screen_title_viewprofile);
        MaterialTextView profileName         = view.findViewById(R.id.profile_name);
        MaterialTextView locationText        = view.findViewById(R.id.profile_location);
        MaterialTextView usernameText        = view.findViewById(R.id.profile_username);
        MaterialTextView postCountText       = view.findViewById(R.id.post_count_viewprofile);
        MaterialTextView supporterCountText  = view.findViewById(R.id.supporter_count_viewprofile);
        MaterialTextView supportingCountText = view.findViewById(R.id.supporting_count_viewprofile);
        CircleImageView profileThumbnail     = view.findViewById(R.id.circleimageview_profile_view);
        recyclerView                         = view.findViewById(R.id.recyclerview_posts_viewprofile);
        swipeRefreshLayout                   = view.findViewById(R.id.swipeRefresh);
        MaterialTextView viewHnIdButton      = view.findViewById(R.id.hnid_viewprofile);

        String hnid = requireArguments().getString("hnid");
        String name = requireArguments().getString("name");
        String username = "@" + requireArguments().getString("username");
        String location = requireArguments().getString("location");
        String thumbnail = requireArguments().getString("thumbnail");
        String supporterCount = requireArguments().getString("supporterCount");
        String supportingCount = requireArguments().getString("supportingCount");
        String postCount = requireArguments().getString("postCount");
        String firstImage = requireArguments().getString("firstImage");

        Log.d(TAG, "Number of posts in ViewProfile = " + postCount);

        screenTitle.setText(myUtils.capitalizeName(name));

        profileName.setText(name);
        usernameText.setText(username);
        if (location.contains("N/A"))
            locationText.setVisibility(View.GONE);
        else
            locationText.setText(location);
//        Picasso
//                .get()
//                .load(thumbnail)
//                .into(profileThumbnail);

        Glide.with(requireContext()).load(thumbnail).centerCrop().into(profileThumbnail);

        postCountText.setText(postCount);
        supporterCountText.setText(supporterCount);
        supportingCountText.setText(supportingCount);

        getLatestPostsListBySingleUser(hnid);

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
                        getPostsListBySingleUserFromPage(nextUserPostListUrl);
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentPostList.clear();
                getLatestPostsListBySingleUser(hnid);
            }
        });

        viewHnIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhotoDialog(hnid, firstImage);
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
                    if (postList != null){
                        nextUserPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        recentPostList.clear();
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));

                        if (postList.getNext() != null)
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

    //get user posts list from next page
    public void getPostsListBySingleUserFromPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsBySingleUserFromPage(nextPageUrl);
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
//                    Log.d(TAG, "total number of supporting profile posts = "+postList.getCount());
                    if(postList != null){
                        nextUserPostListUrl = postList.getNext();
                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());
                        postArrayList = myUtils.setPostRelativeTime(postArrayList);

                        recentPostList.remove(recentPostList.size() - 1);
                        recentPostList.addAll(myUtils.removeMediaPostsWithoutFilePath(postArrayList));


                        postListAdapter.notifyItemRangeChanged(0,recentPostList.size());
                        if (postList.getNext() != null) {
                            recentPostList.add(null);
                            Log.d(TAG, "total number of user posts fetched = " + recentPostList.size());
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
        postListAdapter = new PostListAdapter(
                postList, getContext(),
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
        myUtils.onCommentCountClick(postId,getContext());
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

    private void updatePhotoDialog(String hnid, String firstImage){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View alertView = getLayoutInflater().inflate(R.layout.view_hnid_alert_layout, null);
        MaterialTextView hnidText  = alertView.findViewById(R.id.textview_hnid_view_profile);
        CircleImageView avatar = alertView.findViewById(R.id.circleimageview_user_profile);

        hnidText.setText(hnid);

        ProgressBar progressBar = alertView.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        Glide
                .with(requireContext())
                .load(firstImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(avatar);

        builder.setView(alertView);
        dialog = builder.create();
        dialog.show();
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