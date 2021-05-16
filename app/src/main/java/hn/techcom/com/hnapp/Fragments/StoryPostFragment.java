package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

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

public class StoryPostFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "StoryPostFragment";
    private Spinner postCategorySpinner;
    private TextInputEditText storyText;
    private Utils myUtils;
    private Profile userProfile;
    private String postCategory;
    private ProgressBar progressBar;

    public StoryPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_post, container, false);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        postCategory = "r";

        //Hooks
        MaterialCardView shareButton     = view.findViewById(R.id.share_story_button);
                         postCategorySpinner = view.findViewById(R.id.spinner_post_type);
                         storyText       = view.findViewById(R.id.textInputEditText_story);
                         progressBar = view.findViewById(R.id.share_story_progressbar);

        //Post Categories
        String[] arrayPostType = new String[]{
                "Random",
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
        postCategorySpinner.setAdapter(adapter);

        //OnClick Listeners
        shareButton.setOnClickListener(this);

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

        // Inflate the layout for this fragment
        return view;
    }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.share_story_button)
                if(!TextUtils.isEmpty(storyText.getText()))
                    shareNewStory();
                else
                    Toast.makeText(getContext(),"Oops! You've forgot to write your story..",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(),"Story shared successfully!",Toast.LENGTH_LONG).show();
//                        Objects.requireNonNull(getActivity()).recreate();
                        storyText.setText("");
                        postCategorySpinner.setSelection(0);
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        storyText.setText("");
                        postCategorySpinner.setSelection(0);
                        Toast.makeText(getContext(),"Unable to share story! Try again later..",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<NewPostResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    storyText.setText("");
                    postCategorySpinner.setSelection(0);
                    Toast.makeText(getContext(),"Oops! something is wrong, please try again later..",Toast.LENGTH_LONG).show();

                }
            });
    }
}
