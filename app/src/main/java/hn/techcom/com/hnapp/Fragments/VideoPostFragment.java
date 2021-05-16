package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;

public class VideoPostFragment extends Fragment {

    private Spinner postTypeSpinner;

    private Utils myUtils;
    private Profile userProfile;

    public VideoPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_video, container, false);

        //Hooks
        CircleImageView userAvatar = view.findViewById(R.id.user_avatar_sharevideo);
                        postTypeSpinner = view.findViewById(R.id.spinner_post_type);


        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        String profilePhotoUrl = "http://167.99.13.238:8000" + userProfile.getProfileImg();

        Picasso
                .get()
                .load(profilePhotoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(userAvatar);

        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Commedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }
}