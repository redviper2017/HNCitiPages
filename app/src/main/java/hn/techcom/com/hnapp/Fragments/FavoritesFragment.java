package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hnapp.R;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_favorites);

        screenTitle.setText(R.string.favorites);
        // Inflate the layout for this fragment
        return view;
    }
}