package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
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

public class CommentsFragment
        extends Fragment
        implements View.OnClickListener, OnReplyClickListener {

    private MaterialTextView commentCountText;
    private RecyclerView recyclerView;
    private MaterialTextView screenTitle;
    private CommentListAdapter commentListAdapter;
    private EditText commentEditText;
    private int postId, count;
    private Profile userProfile;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ArrayList<ResultViewComments> commentsArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "CommentsFragment";

    private boolean postingComment = false;

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            postId = bundle.getInt("post_id");
            count  = bundle.getInt("count");
            Log.d(TAG,"post id in CommentsFragment = "+postId);
        }

        //Hooks
        ImageButton backButton                = view.findViewById(R.id.image_button_back);
        screenTitle                           = view.findViewById(R.id.text_screen_title_view_comments);
        commentCountText                      = view.findViewById(R.id.text_like_count_view_comments);
        recyclerView                          = view.findViewById(R.id.recyclerview_posts_comments);
        CircleImageView avatar                = view.findViewById(R.id.avatar_post);
        commentEditText                       = view.findViewById(R.id.comment_editText);
        ImageButton postCommentButton         = view.findViewById(R.id.post_comment_button);
        swipeRefreshLayout                    = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout                    = view.findViewById(R.id.shimmerLayout);
        commentsArrayList = new ArrayList<>();

        viewCommentsOnPost();
        commentCountText.setText(String.valueOf(count));
        if(count == 1)
            screenTitle.setText(R.string.comment);
        else
            screenTitle.setText(R.string.comments);

        //Setting up user avatar on comment bottom bar
        Utils myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        String profilePhotoUrl = userProfile.getProfileImgThumbnail();
        Picasso
                .get()
                .load(profilePhotoUrl)
                .into(avatar);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        postCommentButton.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentsArrayList.clear();
                viewCommentsOnPost();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            requireActivity().getSupportFragmentManager().popBackStack();
        if(v.getId() == R.id.post_comment_button) {
            if (!postingComment) {
                postingComment = true;
                if (!TextUtils.isEmpty(commentEditText.getText()))
                    postComment(userProfile.getHnid(), postId);
                else {
                    Toast.makeText(getContext(), "Oops! you've forgot to enter your comment..", Toast.LENGTH_SHORT).show();
                    postingComment = false;
                }
            }
        }
    }

    @Override
    public void onReplyClick(int commentId, String reply, int position, LinearLayout replyLayout, ImageButton replyButton) {
        Log.d(TAG,"replied text = "+reply);
        postReply(commentId,reply, position, replyLayout, replyButton);
    }

    public void setRecyclerView(ArrayList<ResultViewComments> commentList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentListAdapter = new CommentListAdapter(recyclerView, commentList, getContext(), this);
        recyclerView.setAdapter(commentListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void viewCommentsOnPost(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewCommentResponse> call = service.viewComments(postId);
        call.enqueue(new Callback<ViewCommentResponse>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentResponse> call, @NonNull Response<ViewCommentResponse> response) {
                if(response.code() == 200) {
                    ViewCommentResponse list = response.body();

                    if (list != null) {
                        if(list.getResults().size() != 0) {
                            commentsArrayList.addAll(list.getResults());
                            setRecyclerView(commentsArrayList);
                        }  else {
                            screenTitle.setText(R.string.comments);
                            setRecyclerView(commentsArrayList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ViewCommentResponse> call, Throwable t) {

            }
        });
    }

    public void postComment(String hnid, int postId){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));
        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), commentEditText.getText().toString());

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResultViewComments> call = service.commentOnPost(user,post,comment);

        call.enqueue(new Callback<ResultViewComments>(){
            @Override
            public void onResponse(@NonNull Call<ResultViewComments> call, @NonNull Response<ResultViewComments> response) {
                if(response.code() == 201){
                    ResultViewComments commentResponse = response.body();
                    commentEditText.setText("");
                    if (commentListAdapter != null)
                        if(commentsArrayList.size() != 0) {
                            commentsArrayList.add(0, commentResponse);
                            commentListAdapter.notifyItemInserted(0);
                            count++;
                            commentCountText.setText(String.valueOf(count));
                        }
                        else {
                            commentsArrayList.add(commentResponse);
                            commentListAdapter.notifyDataSetChanged();
                        }

                }else
                    Toast.makeText(getContext(),"Unable to post comment! Try again later..",Toast.LENGTH_LONG).show();
                postingComment = false;
            }

            @Override
            public void onFailure(@NonNull Call<ResultViewComments> call, @NonNull Throwable t) {
                Toast.makeText(getContext(),"Oops! something is wrong, please try again later..",Toast.LENGTH_LONG).show();
            }
        });
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
            public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {

                if(response.code() == 201) {
                    Reply reply = response.body();
                    commentsArrayList.get(position).getReplies().add(reply);
                    commentListAdapter.notifyItemChanged(position);

                    replyButton.setImageResource(R.drawable.reply_ic);
                    replyLayout.setVisibility(View.GONE);

                    count++;
                    commentCountText.setText(String.valueOf(count));
                }

            }

            @Override
            public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {

            }
        });
    }
}