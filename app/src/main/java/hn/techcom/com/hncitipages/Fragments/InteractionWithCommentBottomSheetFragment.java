package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hn.techcom.com.hncitipages.R;

public class InteractionWithCommentBottomSheetFragment extends BottomSheetDialogFragment {

    public InteractionWithCommentBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interaction_with_comment_bottom_sheet, container, false);
    }
}