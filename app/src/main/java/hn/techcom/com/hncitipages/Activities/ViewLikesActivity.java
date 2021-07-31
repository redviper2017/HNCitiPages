package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import hn.techcom.com.hncitipages.Adapters.LikeListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.ResultViewLikes;
import hn.techcom.com.hncitipages.Models.ViewLikesResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewLikesActivity extends AppCompatActivity implements View.OnClickListener{

    private MaterialTextView likeCountText;
    private RecyclerView recyclerView;
    private LikeListAdapter likesListAdapter;
    private MaterialTextView screenTitle;

    private static final String TAG = "ViewLikesActivity";
    private ArrayList<ResultViewLikes> likesArrayList;

    private int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_likes);

        //get the post id to view likes
        Intent intent = getIntent();
        postId = intent.getIntExtra("POST_ID",-1);

        //Hooks
        ImageButton backButton         = findViewById(R.id.image_button_back);
        screenTitle                    = findViewById(R.id.text_screen_title_view_likes);
        likeCountText                  = findViewById(R.id.text_like_count_view_likes);
        recyclerView                   = findViewById(R.id.recyclerview_posts_likes);

        likesArrayList                 = new ArrayList<>();

        viewLikesOnPost();

        //OnClick Listeners
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
    }

    public void viewLikesOnPost(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewLikesResponse> call = service.viewLikes(postId);
        call.enqueue(new Callback<ViewLikesResponse>() {
            @Override
            public void onResponse(Call<ViewLikesResponse> call, Response<ViewLikesResponse> response) {
                if(response.code() == 200) {
                    ViewLikesResponse list = response.body();

                    if(list.getResults().size() != 0) {
                        likesArrayList.addAll(list.getResults());
                        likeCountText.setText(String.valueOf(likesArrayList.size()));
                        if(list.getCount() == 1)
                            screenTitle.setText(R.string.like);
                        else
                            screenTitle.setText(R.string.likes);
                        setRecyclerView(likesArrayList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ViewLikesResponse> call, Throwable t) {

            }
        });
    }

    public void setRecyclerView(ArrayList<ResultViewLikes> likeList){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        likesListAdapter = new LikeListAdapter(recyclerView, likeList, this);
        recyclerView.setAdapter(likesListAdapter);
    }
}