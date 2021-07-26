package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hnapp.R;

public class PostsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title);
        MaterialTextView countText   = findViewById(R.id.count_text);

        String count = getIntent().getStringExtra("PostCount");
        countText.setText(count);
        if (Integer.parseInt(count) > 1)
            screenTitle.setText("Posts");
        else
            screenTitle.setText("Post");

        //OnClick Listeners
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }
}