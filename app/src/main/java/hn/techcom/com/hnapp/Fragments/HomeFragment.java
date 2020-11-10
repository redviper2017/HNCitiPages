package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.R;

public class HomeFragment extends Fragment {
    private MaterialTextView screenTitle;

    public  ArrayList<Post> globalPosts = new ArrayList<>();

    public HomeFragment(ArrayList<Post> globalPosts) {
        this.globalPosts = globalPosts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);

        screenTitle = view.findViewById(R.id.text_screen_title_supportsection);

        screenTitle.setText(R.string.home);

        // Inflate the layout for this fragment
        return view;
    }
}
