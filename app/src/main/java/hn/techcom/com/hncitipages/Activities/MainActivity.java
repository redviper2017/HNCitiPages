package hn.techcom.com.hncitipages.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Fragments.ExploreFragment;
import hn.techcom.com.hncitipages.Fragments.HomeFragment;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
import hn.techcom.com.hncitipages.Fragments.SharePostBottomSheetFragment;
import hn.techcom.com.hncitipages.Fragments.SupportSectionFragment;
import hn.techcom.com.hncitipages.Fragments.VisitSectionFragment;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.Post;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.SupporterProfile;
import hn.techcom.com.hncitipages.Models.TokenUpdateResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.BottomSheetFragment;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "101";
    private static final int PERMISSION_REQUEST_CODE = 200;

    //currently its hard coded but later on it will taken from local db based on currently logged in user's username

    private static final String TAG = "MainActivity";

    private Utils myUtils;
    private Profile userProfile;

    private final int UPDATE_REQUEST_CODE = 1612;

    @Override
    protected void onStart() {
        super.onStart();

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(MainActivity.this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Profile localUser = myUtils.getNewUserFromSharedPreference(this);

        //Check if user is logged in and profile is locally stored
        if(user == null || localUser.getProfileImg() == null){
            startActivity(new Intent(this, SignInActivity.class));
        }
        else {
            if (checkPermission()) {
                getToken();
                getLatestPostsListBySingleUser();
                Fragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
            }else{
                requestPermission();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callInAppUpdate();
        createNotificationChannel();

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
    protected void onResume() {
        super.onResume();
        callInAppUpdate();
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
                fragmentSelected = new SupportSectionFragment();
                break;
            case R.id.navigation_home:
                fragmentSelected = new HomeFragment();
                break;
            case R.id.navigation_visitsection:
                fragmentSelected = new VisitSectionFragment();
                break;
//            case R.id.navigation_profile:
//                if (userPostCount != 0) {
//                    fragmentSelected = new ProfileSectionFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("hnid",userProfile.getHnid());
//                    bundle.putString("name",userProfile.getFullName());
//
//                    fragmentSelected.setArguments(bundle);
//                }
//                else
//                    Toast.makeText(MainActivity.this,"You have to make your first post to view this section",Toast.LENGTH_SHORT).show();
//                break;
            case R.id.navigation_explore:
                fragmentSelected = new ExploreFragment();
                break;
            case android.R.id.home:
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                break;
        }
        // Begin the transaction
        if(fragmentSelected != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).addToBackStack(null).commit();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted && recordAccepted){
                        Fragment fragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
                    }
                    else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                        }
                    }
                }else
                    finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE) {
            Toast.makeText(this, "Downloading start", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d(TAG,"onActivityResult: Update flow failed! Result code: " + resultCode);
            }
        }
    }

    //Custom methods
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("You need to allow access to all of these permission in order to continue with this app.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        onDestroy();
                    }
                })
                .create()
                .show();
    }

    private void callInAppUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, UPDATE_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        // Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                // This example applies an immediate update. To apply a flexible update
//                // instead, pass in AppUpdateType.FLEXIBLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                Log.d(TAG,"available version = "+appUpdateInfo.availableVersionCode());
//                Log.d(TAG,"update req = "+appUpdateInfo.updateAvailability());
//
//                // Request the update.
//
//                try {
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,
//                            MainActivity.this,
//                            UPDATE_REQUEST_CODE);
//                }
//                catch (IntentSender.SendIntentException exception){
//                    Log.d(TAG,"callInAppUpdate: "+exception.getMessage());
//                }
//            }
//            Log.d(TAG,"available version = "+appUpdateInfo.availableVersionCode());
//            Log.d(TAG,"update req = "+appUpdateInfo.updateAvailability());
//        });
    }

    //get initial user posts list
    public void getLatestPostsListBySingleUser() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestPostsBySingleUser(userProfile.getHnid(),userProfile.getHnid());
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                if (response.code() == 200){
                    Log.d(TAG,"total number of post by this user = "+response.body().getResults().size());
                    PostList postList = response.body();
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful())
                    Log.d(TAG,"onComplete: Failed to get FCM Token");

                //If successful get token
                String token = task.getResult();
                Log.d(TAG,"onComplete: Successful to get FCM Token = "+token);

                updateFCMTokenForUser(token);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FirebaseNotificationChannel";
            String description = "Received FIrebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateFCMTokenForUser(String token){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody fcm_token = RequestBody.create(MediaType.parse("text/plain"), token);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<TokenUpdateResponse> call = service.updateFCMToken(user,fcm_token);
        call.enqueue(new Callback<TokenUpdateResponse>() {
            @Override
            public void onResponse(Call<TokenUpdateResponse> call, Response<TokenUpdateResponse> response) {
                if(response.code() == 201){
                    TokenUpdateResponse tokenUpdateResponse = response.body();
                    if (tokenUpdateResponse != null) {
                        Log.d(TAG,"updated token of user = "+tokenUpdateResponse.getSuccess());
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenUpdateResponse> call, Throwable t) {

            }
        });
    }
}