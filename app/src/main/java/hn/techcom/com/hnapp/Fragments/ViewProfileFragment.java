package hn.techcom.com.hnapp.Fragments;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Activities.ViewCommentsActivity;
import hn.techcom.com.hnapp.Activities.ViewLikesActivity;
import hn.techcom.com.hnapp.Adapters.PostListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Interfaces.OnAvatarLongClickListener;
import hn.techcom.com.hnapp.Interfaces.OnCommentClickListener;
import hn.techcom.com.hnapp.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hnapp.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hnapp.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hnapp.Models.FavoriteResponse;
import hn.techcom.com.hnapp.Models.LikeResponse;
import hn.techcom.com.hnapp.Models.PostList;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
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
        OnLoadMoreListener{

    private Utils myUtils;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Result> recentPostList;
    private Profile userProfile;
    private static final String TAG = "ViewProfileFragment";
    private String nextUserPostListUrl;
    private PostListAdapter postListAdapter;


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
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_viewprofile);
        MaterialTextView profileName = view.findViewById(R.id.profile_name);
        MaterialTextView locationText = view.findViewById(R.id.profile_location);
        MaterialTextView usernameText = view.findViewById(R.id.profile_username);
        MaterialTextView postCountText = view.findViewById(R.id.post_count_viewprofile);
        MaterialTextView supporterCountText = view.findViewById(R.id.supporter_count_viewprofile);
        MaterialTextView supportingCountText = view.findViewById(R.id.supporting_count_viewprofile);
        CircleImageView profileThumbnail = view.findViewById(R.id.circleimageview_profile_view);
        recyclerView = view.findViewById(R.id.recyclerview_posts_viewprofile);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        String hnid = requireArguments().getString("hnid");
        String name = requireArguments().getString("name");
        String username = "@" + requireArguments().getString("username");
        String location = requireArguments().getString("location");
        String thumbnail = requireArguments().getString("thumbnail");
        String supporterCount = requireArguments().getString("supporterCount");
        String supportingCount = requireArguments().getString("supportingCount");
        String postCount = requireArguments().getString("postCount");

        getLatestPostsBySingleUser(hnid);

        Log.d(TAG, "Number of posts in ViewProfile = " + postCount);

        screenTitle.setText(myUtils.capitalizeName(name));

        profileName.setText(name);
        usernameText.setText(username);
        locationText.setText(location);
        Picasso
                .get()
                .load(thumbnail)
                .into(profileThumbnail);

        postCountText.setText(postCount);
        supporterCountText.setText(supporterCount);
        supportingCountText.setText(supportingCount);

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
                        getPostsBySingleUserFromPage(nextUserPostListUrl);
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentPostList.clear();
                getLatestPostsBySingleUser(hnid);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //get initial user posts list
    public void getLatestPostsBySingleUser(String target_hnid) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsBySingleUser(target_hnid, userProfile.getHnid());

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.code() == 200) {
                    PostList postList = response.body();
                    if (postList != null) {
                        Log.d(TAG, "number of profile posts = " + postList.getCount());
                        nextUserPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());

                        recentPostList.clear();
                        recentPostList.addAll(postArrayList);
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
    public void getPostsBySingleUserFromPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getPostsBySingleUserFromPage(nextPageUrl);

        call.enqueue(new Callback<PostList>(){
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
                    Log.d(TAG, "number of supporting profile posts = "+postList.getCount());
                    if (postList != null) {
                        nextUserPostListUrl = postList.getNext();

                        ArrayList<Result> postArrayList = new ArrayList<>(postList.getResults());

                        recentPostList.addAll(postArrayList);
                        recentPostList.add(null);

                        postListAdapter.notifyDataSetChanged();
                        if (postList.getNext() != null) {
                            Log.d(TAG,"total number of global posts fetched = "+recentPostList.size());
                            getPostsBySingleUserFromPage(postList.getNext());
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
        Intent intent = new Intent(getContext(), ViewLikesActivity.class);
        intent.putExtra("POST_ID",postId);
        startActivity(intent);
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
}