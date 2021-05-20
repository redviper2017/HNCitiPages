package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import hn.techcom.com.hnapp.R;

public class PostAudioActivity extends AppCompatActivity implements View.OnClickListener{
    //Constants
    private static final int REQUEST_AUDIO_CAPTURE = 1;
    private static final int REQUEST_AUDIO_PICK = 2;
    private MaterialCardView captureAudioButton, selectAudioButton;
    private Spinner postCategorySpinner;

    private String postCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_audio);

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_shareaudio);
        captureAudioButton           = findViewById(R.id.capture_audio_button);
        selectAudioButton            = findViewById(R.id.select_audio_button);
        postCategorySpinner          = findViewById(R.id.spinner_post_type);

        screenTitle.setText(R.string.share_audio);

        //Setting up post types for spinner
        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Commedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postCategorySpinner.setAdapter(adapter);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        captureAudioButton.setOnClickListener(this);
        selectAudioButton.setOnClickListener(this);

        postCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(parent.getItemAtPosition(position).toString()){
                    case "Random":
                        postCategory = "r";
                        break;
                    case "Positive Thoughts":
                        postCategory = "p";
                        break;
                    case "Talent":
                        postCategory = "t";
                        break;
                    case "Lifestyle":
                        postCategory = "l";
                        break;
                    case "Culture":
                        postCategory = "c";
                        break;
                    case "Hustle":
                        postCategory = "h";
                        break;
                    case "Commedy":
                        postCategory = "o";
                        break;
                    case "News":
                        postCategory = "n";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
        if(view.getId() == R.id.capture_audio_button)
            startAudioCaptureIntent();
        if(view.getId() == R.id.select_audio_button)
            startAudioPick();
    }

    //Custom methods
    private void startAudioCaptureIntent(){
//        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//        startActivityForResult(intent, REQUEST_AUDIO_CAPTURE);
    }

    private void startAudioPick(){
//        final Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("audio/*");
//        startActivityForResult(intent, REQUEST_AUDIO_PICK);
    }
}