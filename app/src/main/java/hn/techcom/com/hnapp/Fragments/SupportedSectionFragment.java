package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import hn.techcom.com.hnapp.Adapters.AvatarLoaderAdapter;
import hn.techcom.com.hnapp.Adapters.PostLoaderAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.QUser;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportedSectionFragment extends Fragment {

    private static final String TAG = "SupportedProfileSection";

    private RecyclerView supportedProfileAvatars, supportedProfilePostsList;

    static ArrayList<SupporterProfile> userSupportedProfiles;
    static ArrayList<Post> userSupportedProfilePosts = new ArrayList<>();

    //currently its hard coded but later on it will taken from local db based on currently logged in user's username
    private String currentUserUsername = "redviper";

    public SupportedSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);

        supportedProfileAvatars = view.findViewById(R.id.recyclerview_supported_avatars_supportsection);
        supportedProfilePostsList = view.findViewById(R.id.recyclerview_posts_supportsection);


        //function calls
        getSupportedProfiles();


        // Inflate the layout for this fragment
        return view;
    }

    // this function retrieves the list of supported profiles by the current user
    public void getSupportedProfiles() {
        //here the user id is 1 which will come from local db
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<SupporterProfile>> call = service.getSupportedProfiles("1");
        call.enqueue(new Callback<List<SupporterProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<SupporterProfile>> call, @NonNull Response<List<SupporterProfile>> response) {
                userSupportedProfiles = new ArrayList<>(Objects.requireNonNull(response.body()));
                Log.d(TAG, "this user is supported by = " + userSupportedProfiles.get(0).getFullName());

                if (getSupportedProfilePosts()){
                    setSupportedProfilePosts();
                }
//                setSupportedProfileAvatars(userSupportedProfiles);
            }

            @Override
            public void onFailure(@NonNull Call<List<SupporterProfile>> call, @NonNull Throwable t) {
                Log.d(TAG, "request failed = " + "True: " + t.getMessage());
            }
        });
    }

    // this function sets supported users avatars to the recyclerview
    public void setSupportedProfileAvatars(ArrayList<SupporterProfile> userSupportedProfiles) {
        ArrayList<String> avatarList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        for (SupporterProfile supportingProfile : userSupportedProfiles) {
            avatarList.add(supportingProfile.getProfileImgUrl());
            nameList.add(supportingProfile.getFullName());
        }
        Log.d(TAG, "avatar list size = " + avatarList.size());
        AvatarLoaderAdapter adapter = new AvatarLoaderAdapter(avatarList, nameList);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        supportedProfileAvatars.setHasFixedSize(true);
        supportedProfileAvatars.setLayoutManager(horizontalLayout);
        supportedProfileAvatars.setAdapter(adapter);
    }

    // this function retrieves the list of posts by a single user
    public void getPostsByUser(String username) {
        Log.d(TAG,"getting posts of = "+username);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        QUser currentUser = new QUser();
        currentUser.setUser(currentUserUsername);

        Call<List<Post>> call = service.getAllPostsBy(username, currentUser);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call,@NonNull Response<List<Post>> response) {
                if (response.body() != null) {
                    Log.d(TAG, "first post from "+ username + " = "+response.body().get(0).getText());
                    userSupportedProfilePosts.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call,@NonNull Throwable t) {

            }
        });
    }

    // this function retrieves the list of supported user's posts
    public boolean getSupportedProfilePosts() {
        Log.d(TAG, "this user is supporting  =  " + userSupportedProfiles.get(0).getUsername());
        for(SupporterProfile supportingProfile : userSupportedProfiles){
            getPostsByUser(supportingProfile.getUsername());
        }
        return true;
//        setSupportedProfilePosts();
    }

    // this function sets all posts by users supported by the logged in user
    public void setSupportedProfilePosts(){
        Log.d(TAG,"supported posts size = "+userSupportedProfilePosts.size());
        Collections.sort(userSupportedProfilePosts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getCreatedOn().compareTo(post2.getCreatedOn());
            }
        });
        Collections.reverse(userSupportedProfilePosts);

        PostLoaderAdapter adapter = new PostLoaderAdapter(userSupportedProfilePosts,userSupportedProfiles,getContext());

        supportedProfilePostsList.setHasFixedSize(true);
        supportedProfilePostsList.setLayoutManager(new LinearLayoutManager(getContext()));
        supportedProfilePostsList.setAdapter(adapter);

    }

}