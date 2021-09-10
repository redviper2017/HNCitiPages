package hn.techcom.com.hncitipages.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.ProfilePostAdapter;
import hn.techcom.com.hncitipages.Fragments.HomeFragment;
import hn.techcom.com.hncitipages.Fragments.NotificationsFragment;
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
        extends AppCompatActivity {

    private static final String TAG = "ProfileSectionFragment";
    private Utils myUtils;

    private String hnid,name;

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
        Log.d(TAG,"user hnid = "+hnid);
        isSupported = bundle.getBoolean("isSupported");

        //getting user profile from local storage
        myUtils                     = new Utils();
        userProfile                 = myUtils.getNewUserFromSharedPreference(this);

        Fragment fragment = new NotificationsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).commit();
    }
}