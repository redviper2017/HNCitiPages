package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hncitipages.R;

public class ViewSupportingProfile extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_supporting_profile);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_view_supporting_profile);

        screenTitle.setText(name);

        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }
}