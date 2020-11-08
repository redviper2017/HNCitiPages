package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hn.techcom.com.hnapp.Adapters.PostLoaderAdapter;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.R;

public class SupportedSectionFragment extends Fragment {

    private static final String TAG = "SupportedProfileSection";

    private RecyclerView supportedProfileAvatars, supportedProfilePostsList;
    private EditText searchView;

    static ArrayList<SupporterProfile> userSupportedProfiles;
    static ArrayList<Post> userSupportedProfilePosts = new ArrayList<>();

    PostLoaderAdapter adapter;


    public SupportedSectionFragment(ArrayList<SupporterProfile> userSupportedProfiles, ArrayList<Post> userSupportedProfilePosts) {
        // Required empty public constructor
        this.userSupportedProfiles = userSupportedProfiles;
        this.userSupportedProfilePosts = userSupportedProfilePosts;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);

        supportedProfileAvatars = view.findViewById(R.id.recyclerview_supported_avatars_supportsection);
        supportedProfilePostsList = view.findViewById(R.id.recyclerview_posts_supportsection);
        searchView = view.findViewById(R.id.searchview_supportedsection);

        //function calls
        setSupportedProfilePosts();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString().toLowerCase());
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void filter(String text) {
        ArrayList<Post> filterNames = new ArrayList<>();

        for (Post post : userSupportedProfilePosts)
            if (post.getUser().getFullName().toLowerCase().contains(text))
                filterNames.add(post);

        adapter.filterList(filterNames);
    }


    // this function sets all posts by users supported by the logged in user
    public void setSupportedProfilePosts() {
        Log.d(TAG, "supported posts size = " + userSupportedProfilePosts.size());
        Collections.sort(userSupportedProfilePosts, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getCreatedOn().compareTo(post2.getCreatedOn());
            }
        });
        Collections.reverse(userSupportedProfilePosts);

        adapter = new PostLoaderAdapter(userSupportedProfilePosts, userSupportedProfiles, getContext());

        supportedProfilePostsList.setHasFixedSize(true);
        supportedProfilePostsList.setLayoutManager(new LinearLayoutManager(getContext()));
        supportedProfilePostsList.setAdapter(adapter);

    }

}