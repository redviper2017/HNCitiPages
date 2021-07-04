package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

public class ViewProfileFragment extends Fragment {

    private Utils myUtils;

    public ViewProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        myUtils = new Utils();

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_viewprofile);

        String hnid = requireArguments().getString("hnid");
        String name = requireArguments().getString("name");

        screenTitle.setText(myUtils.capitalizeName(name));

        // Inflate the layout for this fragment
        return view;
    }
}