package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hnapp.Models.NewUser;
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

        Toast.makeText(getActivity(), "Welcome "+getNewUserFromSharedPreference().getFullName() + " !",Toast.LENGTH_LONG).show();

        screenTitle.setText(R.string.home);

        // Inflate the layout for this fragment
        return view;
    }

    private NewUser getNewUserFromSharedPreference() {
        SharedPreferences pref = Objects.requireNonNull(getContext()).getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("NewUser","");
        NewUser user = gson.fromJson(json, NewUser.class);

        return user;
    }
}
