package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hnapp.Fragments.HomeFragment;
import hn.techcom.com.hnapp.Fragments.SharePostBottomSheetFragment;
import hn.techcom.com.hnapp.Fragments.SupportedSectionFragment;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.BottomSheetFragment;
import hn.techcom.com.hnapp.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    static ArrayList<SupporterProfile> userSupportedProfiles;
    static ArrayList<Post> globalPosts = new ArrayList<>();
    static ArrayList<Post> userSupportedProfilePosts = new ArrayList<>();

    //currently its hard coded but later on it will taken from local db based on currently logged in user's username
    private String currentUserUsername = "redviper";

    private static final String TAG = "MainActivity";

    private Utils myUtils;

    @Override
    protected void onStart() {
        super.onStart();

        myUtils = new Utils();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Profile localUser = myUtils.getNewUserFromSharedPreference(this);

        //Check if user is logged in and profile is locally stored
        if(user == null || localUser.getProfileImg() == null){
            startActivity(new Intent(this, SignInActivity.class));
        }
        else {
            Fragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
        }
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
                fragmentSelected = new HomeFragment();
                break;
            case android.R.id.home:
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
        }
        // Begin the transaction
        if(fragmentSelected != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).commit();
        return true;
    }
}