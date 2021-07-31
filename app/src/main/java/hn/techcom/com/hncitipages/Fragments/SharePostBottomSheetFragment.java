package hn.techcom.com.hncitipages.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hncitipages.Activities.PostAudioActivity;
import hn.techcom.com.hncitipages.Activities.PostImageActivity;
import hn.techcom.com.hncitipages.Activities.PostStoryActivity;
import hn.techcom.com.hncitipages.Activities.PostVideoActivity;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class SharePostBottomSheetFragment extends BottomSheetDialogFragment {
    NavigationView navigationView;
    private Utils myUtils;
    private Profile userProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_share_post,container,false);
        navigationView = view.findViewById(R.id.navigation_view_share);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.navigation_share_text){
                    startActivity(new Intent(getActivity(), PostStoryActivity.class));
                    dismiss();
                }
                if(item.getItemId() == R.id.navigation_share_image){
                    startActivity(new Intent(getActivity(), PostImageActivity.class));
                    dismiss();
                }
                if(item.getItemId() == R.id.navigation_share_video){
                    startActivity(new Intent(getActivity(), PostVideoActivity.class));
                    dismiss();
                }
                if(item.getItemId() == R.id.navigation_share_audio){
                    startActivity(new Intent(getActivity(), PostAudioActivity.class));
                    dismiss();
                }

                return true;
            }
        });
    }
}