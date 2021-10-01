package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hncitipages.Interfaces.OnCommentDeleteListener;
import hn.techcom.com.hncitipages.Interfaces.OnCommentReplyListener;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class InteractionWithCommentBottomSheetFragmentOwn extends BottomSheetDialogFragment {

    private Utils myUtils;
    private Profile userProfile;
    private String commentedUserHnid;
    private int commentId;
    private int absoluteAdapterPosition;

    private NavigationView navigationView;
    private OnCommentDeleteListener onCommentDeleteListener;
    private OnCommentReplyListener onCommentReplyListener;

    public InteractionWithCommentBottomSheetFragmentOwn(
            int id,
            String hnid,
            OnCommentDeleteListener onCommentDeleteListener,
            OnCommentReplyListener onCommentReplyListener,
            int absoluteAdapterPosition) {
        // Required empty public constructor
        commentId = id;
        commentedUserHnid = hnid;
        this.onCommentDeleteListener = onCommentDeleteListener;
        this.onCommentReplyListener = onCommentReplyListener;
        this.absoluteAdapterPosition = absoluteAdapterPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        if (commentedUserHnid.equals(userProfile.getHnid()))
            view = inflater.inflate(R.layout.fragment_interact_with_comment_bottom_sheet_own, container, false);
        else
            view = inflater.inflate(R.layout.fragment_interact_with_comment_bottom_sheet, container, false);

        navigationView = view.findViewById(R.id.navigation_interact_with_post);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.delete_comment)
                    onCommentDeleteListener.onCommentDelete(commentId, absoluteAdapterPosition);
                if (item.getItemId() == R.id.reply_comment)
                    onCommentReplyListener.onCommentReply(commentId,absoluteAdapterPosition);
                return true;
            }
        });

        return view;
    }
}