package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import hn.techcom.com.hnapp.Adapters.CommentListAdapter;
import hn.techcom.com.hnapp.Adapters.LikeListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.ResultViewComments;
import hn.techcom.com.hnapp.Models.ResultViewLikes;
import hn.techcom.com.hnapp.Models.ViewCommentResponse;
import hn.techcom.com.hnapp.Models.ViewLikesResponse;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCommentsActivity extends AppCompatActivity implements View.OnClickListener{

    private MaterialTextView commentCountText;
    private RecyclerView recyclerView;
    private MaterialTextView screenTitle;
    private CommentListAdapter commentListAdapter;
    private int postId;

    private ArrayList<ResultViewComments> commentsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);

        //get the post id to view comments
        Intent intent = getIntent();
        postId = intent.getIntExtra("POST_ID",-1);

        //Hooks
        ImageButton backButton         = findViewById(R.id.image_button_back);
        screenTitle                    = findViewById(R.id.text_screen_title_view_comments);
        commentCountText               = findViewById(R.id.text_like_count_view_comments);
        recyclerView                   = findViewById(R.id.recyclerview_posts_comments);

        commentsArrayList = new ArrayList<>();

        viewCommentsOnPost();

        //OnClick Listeners
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
    }

    public void viewCommentsOnPost(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewCommentResponse> call = service.viewComments(postId);
        call.enqueue(new Callback<ViewCommentResponse>() {
            @Override
            public void onResponse(Call<ViewCommentResponse> call, Response<ViewCommentResponse> response) {
                if(response.code() == 200) {
                    ViewCommentResponse list = response.body();

                    if(list.getResults().size() != 0) {
                        commentsArrayList.addAll(list.getResults());
                        commentCountText.setText(String.valueOf(commentsArrayList.size()));
                        if(list.getCount() == 1)
                            screenTitle.setText(R.string.comment);
                        else
                            screenTitle.setText(R.string.comments);
                        setRecyclerView(commentsArrayList);
                    }
                }
            }

            @Override
            public void onFailure(Call<ViewCommentResponse> call, Throwable t) {

            }
        });
    }

    public void setRecyclerView(ArrayList<ResultViewComments> commentList){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentListAdapter = new CommentListAdapter(recyclerView, commentList, this);
        recyclerView.setAdapter(commentListAdapter);
    }
}