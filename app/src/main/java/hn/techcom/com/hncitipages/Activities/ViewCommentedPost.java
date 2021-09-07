package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hncitipages.R;

public class ViewCommentedPost extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_commented_post);

        Bundle bundle = getIntent().getExtras();
        String postid = bundle.getString("postid");
        String name = bundle.getString("name");

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_view_commented_post);

        screenTitle.setText("View comment on post");

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }
}