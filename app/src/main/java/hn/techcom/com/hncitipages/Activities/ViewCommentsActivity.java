package hn.techcom.com.hncitipages.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Adapters.CommentListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnReplyClickListener;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.Models.ResultViewComments;
import hn.techcom.com.hncitipages.Models.ViewCommentResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCommentsActivity extends AppCompatActivity implements View.OnClickListener, OnReplyClickListener {

    private MaterialTextView commentCountText;
    private RecyclerView recyclerView;
    private MaterialTextView screenTitle;
    private CommentListAdapter commentListAdapter;
    private CircleImageView avatar;
    private EditText commentEditText;
    private ImageButton postCommentButton;

    private int postId;
    private Utils myUtils;
    private Profile userProfile;

    private ArrayList<ResultViewComments> commentsArrayList;
    private static final String TAG = "ViewCommentsActivity";

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
        avatar                         = findViewById(R.id.avatar_post);
        commentEditText                = findViewById(R.id.comment_editText);
        postCommentButton              = findViewById(R.id.post_comment_button);

        commentsArrayList = new ArrayList<>();

        viewCommentsOnPost();

        //Setting up user avatar on comment bottom bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(this);
        String profilePhotoUrl = userProfile.getProfileImgThumbnail();
        Picasso
                .get()
                .load(profilePhotoUrl)
                .into(avatar);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        postCommentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_button_back)
            super.onBackPressed();
        if(view.getId() == R.id.post_comment_button)
            if(!TextUtils.isEmpty(commentEditText.getText()))
                postComment(userProfile.getHnid(),postId);
            else
                Toast.makeText(this,"Oops! you've forgot to enter your comment..",Toast.LENGTH_LONG).show();

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
                    }  else {
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
        commentListAdapter = new CommentListAdapter(recyclerView, commentList, this, this);
        recyclerView.setAdapter(commentListAdapter);
    }

    public void postComment(String hnid, int postId){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));
        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), commentEditText.getText().toString());

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResultViewComments> call = service.commentOnPost(user,post,comment);

        call.enqueue(new Callback<ResultViewComments>(){
            @Override
            public void onResponse(Call<ResultViewComments> call, Response<ResultViewComments> response) {
                if(response.code() == 201){
                    ResultViewComments commentResponse = response.body();
                    Toast.makeText(ViewCommentsActivity.this, "Your comment has been posted successfully!", Toast.LENGTH_LONG).show();
                    commentEditText.setText("");
                    if (commentListAdapter != null)
                        if(commentsArrayList.size() != 0) {
                            commentsArrayList.add(0, commentResponse);
                            commentListAdapter.notifyDataSetChanged();
                        }
                    else {
                        commentsArrayList.add(commentResponse);
                        commentListAdapter.notifyDataSetChanged();
                    }

                }else
                    Toast.makeText(ViewCommentsActivity.this,"Unable to post comment! Try again later..",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResultViewComments> call, Throwable t) {
                Toast.makeText(ViewCommentsActivity.this,"Oops! something is wrong, please try again later..",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onReplyClick(int commentId, String reply, int position, LinearLayout replyLayout, ImageButton replyButton) {
        Log.d(TAG,"replied text = "+reply);
        postReply(commentId,reply, position, replyLayout, replyButton);
    }

    public void postReply(int commentId, String reply, int position, LinearLayout replyLayout, ImageButton replyButton){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));
        RequestBody reply_comment = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(commentId));
        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), reply);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Reply> call = service.replyOnPost(user,post,reply_comment,comment);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {

                if(response.code() == 201) {
                    Reply reply = response.body();
                    commentsArrayList.get(position).getReplies().add(reply);
                    commentListAdapter.notifyDataSetChanged();

                    replyButton.setImageResource(R.drawable.reply_ic);
                    replyLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {

            }
        });
    }
}