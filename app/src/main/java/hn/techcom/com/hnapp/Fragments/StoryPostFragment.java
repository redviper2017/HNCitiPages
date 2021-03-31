package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import hn.techcom.com.hnapp.R;

public class StoryPostFragment extends Fragment {

    private Spinner postTypeSpinner;

    public StoryPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_post, container, false);

        postTypeSpinner = view.findViewById(R.id.spinner_post_type);

        //Post Categories
        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "News",
                "Emergency"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }
}