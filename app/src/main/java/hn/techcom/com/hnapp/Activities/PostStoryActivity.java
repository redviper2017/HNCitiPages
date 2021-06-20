package hn.techcom.com.hnapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.NewPostResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostStoryActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PostStoryActivity";
    private Spinner postCategorySpinner;
    private ProgressBar progressBar;
    private TextInputEditText storyText;
    private MaterialCardView shareStoryButton, clearStoryButton;
    private Utils myUtils;
    private Profile userProfile;
    private String postCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);

        //Hooks
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_sharestory);
        ImageButton backButton       = findViewById(R.id.image_button_back);
        postCategorySpinner          = findViewById(R.id.spinner_post_type);
        progressBar                  = findViewById(R.id.share_story_progressbar);
        storyText                    = findViewById(R.id.textInputEditText_story);
        shareStoryButton             = findViewById(R.id.share_story_button);
        clearStoryButton             = findViewById(R.id.clear_story_button);

        postCategory = "r";

        screenTitle.setText(R.string.share_story);

        //Setting up post types for spinner
        String[] arrayPostType = new String[]{"Random",
                "Positive Thoughts",
                "Talent",
                "Culture",
                "Lifestyle",
                "Hustle",
                "Comedy",
                "News"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayPostType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        postCategorySpinner.setAdapter(adapter);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        shareStoryButton.setOnClickListener(this);
        clearStoryButton.setOnClickListener(this);

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);

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
        if(view.getId() == R.id.share_story_button)
            shareNewStory();
        if(view.getId() == R.id.clear_story_button){
            storyText.setText("");
            postCategorySpinner.setSelection(0);
        }
    }

    public void shareNewStory(){
        progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG,"post user hnid = "+userProfile.getHnid());
        Log.d(TAG,"post user city = "+userProfile.getCity());
        Log.d(TAG,"post user country = "+userProfile.getCountry());
        Log.d(TAG,"post user posttype = "+"S");
        Log.d(TAG,"post user category = "+postCategory);
        Log.d(TAG,"post user text = "+storyText.getText().toString());

        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCity());
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), userProfile.getCountry());
        RequestBody posttype = RequestBody.create(MediaType.parse("text/plain"),"S");
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"),postCategory);
        RequestBody text = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(storyText.getText()).toString());

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NewPostResponse> call = service.shareStory(user,city,country,posttype,category,text);
        call.enqueue(new Callback<NewPostResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewPostResponse> call, @NonNull Response<NewPostResponse> response) {
                Log.d(TAG,"response code for new story post = "+response.code());
                if(response.code() == 201){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostStoryActivity.this,"Story shared successfully!",Toast.LENGTH_LONG).show();

                    storyText.setText("");
                    postCategorySpinner.setSelection(0);

                    startActivity(new Intent(PostStoryActivity.this,MainActivity.class));
                    finish();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    storyText.setText("");
                    postCategorySpinner.setSelection(0);
                    Toast.makeText(PostStoryActivity.this,"Unable to share story! Try again later..",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewPostResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                storyText.setText("");
                postCategorySpinner.setSelection(0);
//                Toast.makeText(PostStoryActivity.this,"Oops! something is wrong, please try again later..",Toast.LENGTH_LONG).show();

            }
        });
    }
}