package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

public class ViewProfileFragment extends Fragment {

    private Utils myUtils;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Result> recentPostList;
    private Profile userProfile;
    private static final String TAG = "ViewProfileFragment";


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
        MaterialTextView usernameText            = view.findViewById(R.id.profile_username);
        MaterialTextView postCountText       = view.findViewById(R.id.post_count_viewprofile);
        MaterialTextView supporterCountText  = view.findViewById(R.id.supporter_count_viewprofile);
        MaterialTextView supportingCountText = view.findViewById(R.id.supporting_count_viewprofile);
        CircleImageView profileThumbnail     = view.findViewById(R.id.circleimageview_profile_view);
        recyclerView                         = view.findViewById(R.id.recyclerview_posts_viewprofile);
        swipeRefreshLayout                   = view.findViewById(R.id.swipeRefresh);

        String hnid = requireArguments().getString("hnid");
        String name = requireArguments().getString("name");
        String username = "@"+requireArguments().getString("username");
        String location = requireArguments().getString("location");
        String thumbnail = requireArguments().getString("thumbnail");
        String supporterCount = requireArguments().getString("supporterCount");
        String supportingCount = requireArguments().getString("supportingCount");
        String postCount = requireArguments().getString("postCount");

        Log.d(TAG,"Number of posts in ViewProfile = "+postCount);

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


        // Inflate the layout for this fragment
        return view;
    }
}