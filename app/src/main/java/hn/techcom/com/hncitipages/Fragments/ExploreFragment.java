package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import hn.techcom.com.hncitipages.R;

public class ExploreFragment extends Fragment implements View.OnClickListener{

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_profile);
        CardView storyCardButton     = view.findViewById(R.id.story_card);
        CardView imageCardButton     = view.findViewById(R.id.image_card);
        CardView audioCardButton     = view.findViewById(R.id.audio_card);
        CardView videoCardButton     = view.findViewById(R.id.video_card);

        //On click listeners
        storyCardButton.setOnClickListener(this);
        imageCardButton.setOnClickListener(this);
        audioCardButton.setOnClickListener(this);
        videoCardButton.setOnClickListener(this);

        screenTitle.setText(R.string.explore);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragmentSelected = null;
        Bundle bundle = new Bundle();

        if (v.getId() == R.id.story_card){
            bundle.putString("type","Stories & Sayings");
            fragmentSelected = new ExplorePostFragment();
            fragmentSelected.setArguments(bundle);
        }else if (v.getId() == R.id.image_card){
            bundle.putString("type","Images & Moments");
            fragmentSelected = new ExplorePostFragment();
            fragmentSelected.setArguments(bundle);
        }else if (v.getId() == R.id.audio_card){
            bundle.putString("type","Audios & Music");
            fragmentSelected = new ExplorePostFragment();
            fragmentSelected.setArguments(bundle);
        }else if (v.getId() == R.id.video_card){
            bundle.putString("type","Videos & Memories");
            fragmentSelected = new ExplorePostFragment();
            fragmentSelected.setArguments(bundle);
        }
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).addToBackStack(null).commit();
    }
}