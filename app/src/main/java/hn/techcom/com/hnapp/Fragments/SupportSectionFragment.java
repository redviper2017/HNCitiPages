package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import hn.techcom.com.hnapp.Activities.ViewCommentsActivity;
import hn.techcom.com.hnapp.Activities.ViewLikesActivity;
import hn.techcom.com.hnapp.Adapters.AvatarLoaderAdapter;
import hn.techcom.com.hnapp.Adapters.PostListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
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
import hn.techcom.com.hnapp.Models.SupportingProfileList;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
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
        OnLoadMoreListener {

    private Utils myUtils;
    private Profile userProfile;

    private ProgressBar progressBar;
    private RecyclerView recyclerView, profileRecyclerView;
    private EditText searchView;
    private PostListAdapter postListAdapter;
    private AvatarLoaderAdapter avatarLoaderAdapter;

    private ArrayList<Result> recentPostList;

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

        recentPostList = new ArrayList<>();

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        screenTitle.setText(R.string.support_section);

        getSupportingProfiles();
        getSupportingProfilePosts();

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

    private void filter(String text) {
        ArrayList<Result> filterNames = new ArrayList<>();

        for (Result post : recentPostList)
            if (post.getUser().getFullName().toLowerCase().contains(text))
                filterNames.add(post);

        postListAdapter.filterList(filterNames);
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

                    if(supportingProfileList.getCount()>0) {
                        ArrayList<User> profilesArraytList = new ArrayList<>();
                        profilesArraytList.addAll(supportingProfileList.getResults());

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

    //get initial supporting profile posts lit
    public void getSupportingProfilePosts(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestSupportingProfilePosts(userProfile.getHnid());

        call.enqueue(new Callback<PostList>(){
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if(response.code() == 200){
                    PostList postList = response.body();
                    Log.d(TAG, "number of supporting profile posts = "+postList.getCount());

                    ArrayList<Result> postArrayList = new ArrayList<>();
                    postArrayList.addAll(postList.getResults());

                    recentPostList.clear();
                    recentPostList.addAll(postArrayList);

                    setRecyclerView(postArrayList);
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
    }

    public void setProfilesRecyclerView(ArrayList<User> supportingProfileList){
        ArrayList<String> avatarList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();

        for (User supportingProfile : supportingProfileList) {
            avatarList.add(supportingProfile.getProfile_img_thumbnail());
            nameList.add(supportingProfile.getFullName());
        }

        Log.d(TAG, "avatar list item url 1 = "+avatarList.get(0));

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        profileRecyclerView.setLayoutManager(horizontalLayout);
        avatarLoaderAdapter = new AvatarLoaderAdapter(
                avatarList,
                nameList
        );
        profileRecyclerView.setAdapter(avatarLoaderAdapter);
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
        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment(position, postId, recentPostList, postListAdapter, hnid_user, supporting);
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
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
}