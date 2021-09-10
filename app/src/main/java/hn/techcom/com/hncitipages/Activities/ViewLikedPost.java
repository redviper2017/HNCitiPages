package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import hn.techcom.com.hncitipages.Fragments.HomeFragment;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
import hn.techcom.com.hncitipages.R;

public class ViewLikedPost extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_liked_post);

        Bundle bundle = getIntent().getExtras();
        String hnid = bundle.getString("hnid");
        String name = bundle.getString("name");
        boolean isSupported = bundle.getBoolean("isSupported");
        String type = bundle.getString("type");
        int postId = bundle.getInt("postId");

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_view_liked_post);

        screenTitle.setText("View like on post");

        backButton.setOnClickListener(this);

        Fragment fragment = new ProfileSectionFragment();

        Bundle bundle1 = new Bundle();
        bundle.putString("type",type);
        bundle.putString("name",name);
        bundle.putBoolean("isSupported",isSupported);
        bundle.putString("hnid",hnid);
        bundle.putInt("postId", postId);

        fragment.setArguments(bundle1);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }
}