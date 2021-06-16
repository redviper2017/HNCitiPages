package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Interfaces.OnReplyClickListener;
import hn.techcom.com.hnapp.Models.Reply;
import hn.techcom.com.hnapp.Models.ResultViewComments;
import hn.techcom.com.hnapp.Models.ResultViewLikes;
import hn.techcom.com.hnapp.R;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "CommentListAdapter";
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ResultViewComments> allComments = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;
    private OnReplyClickListener onReplyClickListener;

    public CommentListAdapter(RecyclerView recyclerView, ArrayList<ResultViewComments> allComments, Context context, OnReplyClickListener onReplyClickListener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.allComments = allComments;
        this.onReplyClickListener = onReplyClickListener;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ResultViewComments comment = allComments.get(position);
        ((CommentViewHolder) holder).bind(comment);
    }

    @Override
    public int getItemCount() {
        return allComments == null ? 0 : allComments.size();
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location;
        public CircleImageView avatar;
        private MaterialTextView commentPost;
        private RecyclerView repliesRecyclerview;
        private ReplyListAdapter replyListAdapter;
        private ImageButton replyButton, postReplyButton;
        private LinearLayout replyLayout;
        private EditText replyText;

        public CommentViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
            commentPost    = view.findViewById(R.id.comment_post);
            repliesRecyclerview = view.findViewById(R.id.recyclerview_posts_replies);
            replyButton    = view.findViewById(R.id.reply_button_comment);
            replyLayout    = view.findViewById(R.id.reply_layout);
            replyText      = view.findViewById(R.id.reply_editText);
            postReplyButton = view.findViewById(R.id.post_reply_button);

            replyButton.setOnClickListener(this);
            postReplyButton.setOnClickListener(this);
        }

        void bind(ResultViewComments comment){
            String address = comment.getUser().getCity() + ", " + comment.getUser().getCountry();

            //setting up user name and location
            name.setText(comment.getUser().getFullName());
            location.setText(address);

            //setting up user avatar
            String profilePhotoUrl = "http://167.99.13.238:8000" + comment.getUser().getProfileImg();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(avatar);

            Log.d(TAG,"comment = "+comment.getComment());
            commentPost.setText(String.valueOf(comment.getComment()));

            if(comment.getReplies().size() > 0) {
                ArrayList<Reply> replyList = new ArrayList<>();
                replyList.addAll(comment.getReplies());
                setRecyclerView(replyList, repliesRecyclerview);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.reply_button_comment) {
                if(replyButton.getTag().equals("reply")) {
                    replyButton.setTag("cancel");
                    int position = getAbsoluteAdapterPosition();

                    replyLayout.setVisibility(View.VISIBLE);
                    replyButton.setImageResource(R.drawable.cancel_ic);
                }else{
                    replyButton.setTag("reply");
                    replyButton.setImageResource(R.drawable.reply_ic);
                    replyLayout.setVisibility(View.GONE);
                }
            }
            if(view.getId() == R.id.post_reply_button){
                int position = getAbsoluteAdapterPosition();
                int commentId = allComments.get(position).getId();
                if(!TextUtils.isEmpty(replyText.getText().toString()))
                    onReplyClickListener.onReplyClick(commentId,replyText.getText().toString(), position);
                else
                    Toast.makeText(context,"Oops! You've forgot to enter your reply",Toast.LENGTH_LONG).show();
            }
        }

        public void setRecyclerView(ArrayList<Reply> replyList, RecyclerView repliesRecyclerview){
            repliesRecyclerview.setLayoutManager(new LinearLayoutManager(context));
            replyListAdapter = new ReplyListAdapter(context, this.repliesRecyclerview, replyList);
            repliesRecyclerview.setAdapter(replyListAdapter);
        }
    }
}
