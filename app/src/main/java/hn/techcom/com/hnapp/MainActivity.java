package hn.techcom.com.hnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hn.techcom.com.hnapp.Fragments.HomeFragment;
import hn.techcom.com.hnapp.Fragments.SharePostBottomSheetFragment;
import hn.techcom.com.hnapp.Fragments.SupportedSectionFragment;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.QUser;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    static ArrayList<SupporterProfile> userSupportedProfiles;
    static ArrayList<Post> globalPosts = new ArrayList<>();
    static ArrayList<Post> userSupportedProfilePosts = new ArrayList<>();

    //currently its hard coded but later on it will taken from local db based on currently logged in user's username
    private String currentUserUsername = "redviper";

    private static final String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();
        Fragment fragment = new HomeFragment(globalPosts);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hooks
        BottomAppBar bottomAppBar = findViewById(R.id.bottomappbar_home);
        FloatingActionButton newPostFab = findViewById(R.id.fab_post);

        //set bottomAppBar
        setSupportActionBar(bottomAppBar);

        getSupportedProfiles();

        newPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePostBottomSheetFragment shareSheetFragment = new SharePostBottomSheetFragment();
                shareSheetFragment.show(getSupportFragmentManager(), shareSheetFragment.getTag());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragmentSelected = null;
        switch(item.getItemId()){
            case R.id.navigation_supportedsection:
                fragmentSelected = new SupportedSectionFragment(userSupportedProfiles,userSupportedProfilePosts);
                break;
            case R.id.navigation_home:
                fragmentSelected = new HomeFragment(globalPosts);
                break;
        }
        // Begin the transaction
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        //if the current fragment loaded is the home fragment then follow default behavior
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.framelayout_main);
        if(f instanceof HomeFragment)
            super.onBackPressed();
        else {
            Fragment fragment = new HomeFragment(globalPosts);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
        }

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
                getSupportedProfilePosts();
            }

            @Override
            public void onFailure(@NonNull Call<List<SupporterProfile>> call, @NonNull Throwable t) {
                Log.d(TAG, "request failed = " + "True: " + t.getMessage());
            }
        });
    }

    // this function retrieves the list of posts by a single user
    public void getPostsByUser(String username) {
        Log.d(TAG, "getting posts of = " + username);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        QUser currentUser = new QUser();
        currentUser.setUser(currentUserUsername);

        Call<List<Post>> call = service.getAllPostsBy(username, currentUser);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.body() != null) {
                    Log.d(TAG, "first post from " + username + " = " + response.body().get(0).getText());
                    userSupportedProfilePosts.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {

            }
        });
    }

    // this function retrieves the list of supported user's posts
    public void getSupportedProfilePosts() {
        Log.d(TAG, "this user is supporting  =  " + userSupportedProfiles.get(0).getUsername());

        for(int i=0;i<userSupportedProfiles.size();i++){
            getPostsByUser(userSupportedProfiles.get(i).getUsername());
        }
    }

}