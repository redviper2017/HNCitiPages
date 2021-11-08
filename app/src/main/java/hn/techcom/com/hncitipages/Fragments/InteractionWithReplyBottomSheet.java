package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hncitipages.Interfaces.OnCommentDeleteListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyDeleteListener;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class InteractionWithReplyBottomSheet extends BottomSheetDialogFragment {

    private Utils myUtils;
    private Profile userProfile;
    private NavigationView navigationView;
    private OnCommentDeleteListener onCommentDeleteListener;
    private int commentPosition,replyID,replyPosition;
    private OnReplyDeleteListener onReplyDeleteListener;
    String hnid;

    public InteractionWithReplyBottomSheet(
            int commentPosition,
            String hnid,
            int replyID,
            OnReplyDeleteListener onReplyDeleteListener,
            int replyPosition) {
        // Required empty public constructor
        this.commentPosition = commentPosition;
        this.hnid = hnid;
        this.replyID = replyID;
        this.onReplyDeleteListener = onReplyDeleteListener;
        this.replyPosition = replyPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interaction_with_reply_bottom_sheet, container, false);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        navigationView = view.findViewById(R.id.navigation_interact_with_reply);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.delete_reply)
                    onReplyDeleteListener.onReplyDelete(commentPosition, replyPosition, replyID);
                if (item.getItemId() == R.id.edit_reply){}
                return true;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}