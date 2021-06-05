package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.ViewLikesResponse;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewLikesActivity extends AppCompatActivity implements View.OnClickListener{

    private MaterialTextView likeCountText;

    private static final String TAG = "ViewLikesActivity";
    private ArrayList<ViewLikesResponse> likesArrayList;

    private int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_likes);

        //get the post id to view likes
        Intent intent = getIntent();
        postId = intent.getIntExtra("POST_ID",-1);
//        viewLikesOnPost();

        //Hooks
        MaterialTextView screenTitle   = findViewById(R.id.text_screen_title_view_likes);
        ImageButton backButton         = findViewById(R.id.image_button_back);
        likeCountText                  = findViewById(R.id.text_like_count_view_likes);

        likesArrayList                 = new ArrayList<>();

        screenTitle.setText(R.string.likes);
        //OnClick Listeners
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
    }

    public void viewLikesOnPost(){
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<ViewLikesResponse>> call = service.viewLikes(post);
        call.enqueue(new Callback<List<ViewLikesResponse>>() {
            @Override
            public void onResponse(Call<List<ViewLikesResponse>> call, Response<List<ViewLikesResponse>> response) {
                if(response.code() == 201) {
                    List<ViewLikesResponse> list = response.body();
                    if(list.size() != 0) {
                        likesArrayList.addAll(list);
                        likeCountText.setText(likesArrayList.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ViewLikesResponse>> call, Throwable t) {

            }
        });
    }
}