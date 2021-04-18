package hn.techcom.com.hnapp.Activities;

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
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.BottomSheetFragment;
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