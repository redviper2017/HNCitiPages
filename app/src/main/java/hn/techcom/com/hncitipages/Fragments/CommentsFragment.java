package hn.techcom.com.hncitipages.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Adapters.CommentListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnCommentDeleteListener;
import hn.techcom.com.hncitipages.Interfaces.OnCommentEditListener;
import hn.techcom.com.hncitipages.Interfaces.OnCommentOptionButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnCommentReplyListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyDeleteListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyEditListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyOptionButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.DeleteResponse;
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
        implements
        View.OnClickListener,
        OnReplyClickListener,
        ViewProfileListener,
        OnCommentOptionButtonClickListener,
        OnCommentDeleteListener,
        OnCommentReplyListener,
        OnCommentEditListener,
        OnReplyOptionButtonClickListener,
        OnReplyDeleteListener,
        OnReplyEditListener {

    private ImageButton postCommentButton;
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
    private String nextPageUrl, hnid;
    private Utils myUtils;

    private static final String TAG = "CommentsFragment";

    private boolean postingComment = false;

    private int commentId = 0;
    private int replyToCommentPosition = 0;
    private int commentEditedAtPosition = 0;
    private int replyEditedAtPosition = 0;
    private String replyText="";
    private String repliedToUsername;


    private LinearLayoutManager linearLayoutManager;
    private InteractionWithCommentBottomSheetFragmentOwn interactWithPostBottomSheetFragment;
    private InteractionWithReplyBottomSheet interactionWithReplyBottomSheet;

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
        postCommentButton         = view.findViewById(R.id.post_comment_button);
        swipeRefreshLayout                    = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout                    = view.findViewById(R.id.shimmerLayout);
        commentsArrayList = new ArrayList<>();

        //Setting up user avatar on comment bottom bar
        Utils myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        hnid = userProfile.getHnid();

        viewCommentsOnPost();
        commentCountText.setText(String.valueOf(count));
        if(count == 1)
            screenTitle.setText(R.string.comment);
        else
            screenTitle.setText(R.string.comments);

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


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && dy>0){
                    if (commentsArrayList.get(commentsArrayList.size()-1) == null){
                        commentsArrayList.remove(commentsArrayList.size()-1);
                        viewCommentsOnPostFromPage();
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back){
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                requireActivity().getSupportFragmentManager().popBackStack();
            else
                requireActivity().onBackPressed();
        }
        if(v.getId() == R.id.post_comment_button) {
            Log.d(TAG,"on post button click flag value = "+postingComment);
            Log.d(TAG,"on post button click tag value = "+postCommentButton.getTag());

            if (!postingComment && postCommentButton.getTag().equals("post comment")) {
                postingComment = true;
                if (!TextUtils.isEmpty(commentEditText.getText()))
                    postComment(userProfile.getHnid(), postId);
                else {
                    Toast.makeText(getContext(), "Oops! you've forgot to enter your comment..", Toast.LENGTH_SHORT).show();
                    postingComment = false;
                }
            }
            if (postCommentButton.getTag().equals("post reply") && commentId != 0 && !postingComment) {

                Log.d(TAG,"replying to user = "+repliedToUsername);
                replyText = commentEditText.getText().toString().replace(repliedToUsername,"");
                Log.d(TAG,"reply to user = "+replyText);

                postingComment = true;

                postCommentButton.setTag("post comment");
                postReply(commentId, replyText);
            }
            if (!postingComment && postCommentButton.getTag().equals("edit comment")){
                postingComment = true;
                if (!TextUtils.isEmpty(commentEditText.getText()))
                    editComment(userProfile.getHnid(), commentId);
                else {
                    Toast.makeText(getContext(), "Oops! you've forgot to enter your comment..", Toast.LENGTH_SHORT).show();
                    postingComment = false;
                }
            }
            if (!postingComment && postCommentButton.getTag().equals("edit reply")){
                postingComment = true;
                if (!TextUtils.isEmpty(commentEditText.getText()))
                    editComment(userProfile.getHnid(), commentId);
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
//        postReply(commentId,reply, position, replyLayout, replyButton);
    }

    public void setRecyclerView(ArrayList<ResultViewComments> commentList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        commentListAdapter = new CommentListAdapter(recyclerView, commentList, getContext(), this, this,this,this);
        recyclerView.setAdapter(commentListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void viewProfile(String hnid, String name, boolean isSupported) {
        Fragment fragment = new ProfileSectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("hnid",hnid);
        bundle.putString("name",name);
        bundle.putBoolean("isSupported",isSupported);

        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }

    public void viewCommentsOnPost(){
        Log.d(TAG,"new comment api response called = "+"YES");
        Log.d(TAG,"new comment api response called with hnid = "+hnid);
        Log.d(TAG,"new comment api response called with postId = "+postId);

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewCommentResponse> call = service.viewComments(postId,hnid);
        call.enqueue(new Callback<ViewCommentResponse>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentResponse> call, @NonNull Response<ViewCommentResponse> response) {
                Log.d(TAG,"new comment api response = "+response.code());
                if(response.code() == 200) {
                    ViewCommentResponse list = response.body();

                    if (list != null) {
                        if(list.getResults().size() != 0) {
                            commentsArrayList.clear();
                            commentsArrayList.addAll(list.getResults());

                            if (list.getNext() != null){
                                nextPageUrl = (String) list.getNext();
                                commentsArrayList.add(null);
                            }

                            setRecyclerView(commentsArrayList);
                        }  else {
                            screenTitle.setText(R.string.comments);
                            setRecyclerView(commentsArrayList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewCommentResponse> call, @NonNull Throwable t) {

            }
        });
    }

    public void viewCommentsOnPostFromPage(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewCommentResponse> call = service.viewCommentsFromPage(nextPageUrl);
        call.enqueue(new Callback<ViewCommentResponse>() {
            @Override
            public void onResponse(@NonNull Call<ViewCommentResponse> call, @NonNull Response<ViewCommentResponse> response) {
                if (response.code() == 200){
                    ViewCommentResponse list = response.body();
                    if (list != null){
                        nextPageUrl = (String) list.getNext();
                        commentsArrayList.addAll(list.getResults());
                        commentListAdapter.notifyDataSetChanged();

                        if (nextPageUrl != null){
                            commentsArrayList.add(null);
                            viewCommentsOnPostFromPage();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ViewCommentResponse> call, @NonNull Throwable t) {

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
                Log.d(TAG,"comment post api response code: "+response.code());

                    ResultViewComments commentResponse = response.body();
                    commentEditText.setText("");
                    if (commentListAdapter != null)
                        if(commentsArrayList.size() != 0) {
                            commentsArrayList.add(0, commentResponse);
                            commentListAdapter.notifyItemInserted(0);
                            count++;
                            commentCountText.setText(String.valueOf(count));
                            recyclerView.scrollToPosition(0);
                        }
                        else {
                            commentsArrayList.add(commentResponse);
                            commentListAdapter.notifyItemInserted(commentsArrayList.size());
                            count++;
                            commentCountText.setText(String.valueOf(count));
                            recyclerView.scrollToPosition(0);
                        }

                postingComment = false;
            }

            @Override
            public void onFailure(@NonNull Call<ResultViewComments> call, @NonNull Throwable t) {
                postingComment = false;
                Toast.makeText(getContext(),"Oops! something is wrong, please try again later..",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editComment(String hnid, int comment_id){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), hnid);

        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), commentEditText.getText().toString());

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResultViewComments> call = service.editComment(comment_id,user,comment);

        call.enqueue(new Callback<ResultViewComments>() {
            @Override
            public void onResponse(@NonNull Call<ResultViewComments> call, @NonNull Response<ResultViewComments> response) {
                ResultViewComments commentResponse = response.body();

                if (commentResponse != null) {
                    commentEditText.setText("");
                    commentEditText.clearFocus();
                    if (postCommentButton.getTag().equals("edit reply"))
                        commentsArrayList.get(commentEditedAtPosition).getReplies().get(replyEditedAtPosition).setComment(commentResponse.getComment());
                    else
                        commentsArrayList.get(commentEditedAtPosition).setComment(commentResponse.getComment());
                    commentListAdapter.notifyItemChanged(commentEditedAtPosition);
                    recyclerView.scrollToPosition(commentEditedAtPosition);
                }
                postingComment = false;
            }

            @Override
            public void onFailure(@NonNull Call<ResultViewComments> call, @NonNull Throwable t) {
                postingComment = false;
                Toast.makeText(getContext(),"Oops! something is wrong, please try again later..",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postReply(int commentId, String reply){
        RequestBody user = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody post = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(postId));
        RequestBody reply_comment = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(commentId));
        RequestBody comment = RequestBody.create(MediaType.parse("text/plain"), reply);

        Log.d(TAG,"replied text to post = "+replyText);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Reply> call = service.replyOnPost(user,post,reply_comment,comment);

        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {

                if(response.code() == 201) {
                    Reply reply = response.body();
//                    commentsArrayList.get(position).getReplies().add(reply);
//                    commentListAdapter.notifyDataSetChanged();
//
//                    replyButton.setImageResource(R.drawable.reply_ic);
//                    replyLayout.setVisibility(View.GONE);

                    commentEditText.clearFocus();
                    commentEditText.setText("");

                    count++;
                    commentCountText.setText(String.valueOf(count));

                    List<Reply> replyList = commentsArrayList.get(replyToCommentPosition).getReplies();
                    replyList.add(reply);

                    commentsArrayList.get(replyToCommentPosition).setReplies(replyList);
                    commentListAdapter.notifyItemChanged(replyToCommentPosition);
                    recyclerView.scrollToPosition(replyToCommentPosition);
                }

                postingComment = false;

            }

            @Override
            public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                postingComment = false;
                Toast.makeText(getContext(),"Oops! something is wrong, please try again later..",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteThisComment(int id, int absoluteAdapterPosition){
        Log.d(TAG,"delete comment with id = "+id);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<DeleteResponse> call = service.deleteComment(id);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteResponse> call, @NonNull Response<DeleteResponse> response) {
                DeleteResponse deleteResponse = response.body();
                Toast.makeText(getActivity(), Objects.requireNonNull(deleteResponse).getSuccess(), Toast.LENGTH_SHORT).show();
                commentsArrayList.remove(absoluteAdapterPosition);
                commentListAdapter.notifyItemRemoved(absoluteAdapterPosition);
                interactWithPostBottomSheetFragment.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<DeleteResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(),"Sorry, unable to delete the comment. Try again..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteThisReply(int commentPosition, int replyPosition, int replyID){
        Log.d(TAG,"delete reply with id = "+replyID);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<DeleteResponse> call = service.deleteComment(replyID);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteResponse> call, @NonNull Response<DeleteResponse> response) {
                DeleteResponse deleteResponse = response.body();
                Toast.makeText(getActivity(), Objects.requireNonNull(deleteResponse).getSuccess(), Toast.LENGTH_SHORT).show();
                commentsArrayList.get(commentPosition).getReplies().remove(replyPosition);
                commentListAdapter.notifyItemChanged(commentPosition);
                interactionWithReplyBottomSheet.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<DeleteResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(),"Sorry, unable to delete the reply. Try again..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCommentOptionButtonClick(int id, String hnid, int absoluteAdapterPosition) {
        interactWithPostBottomSheetFragment = new InteractionWithCommentBottomSheetFragmentOwn(id, hnid,this, this,this,absoluteAdapterPosition);
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
    }

    @Override
    public void onCommentDelete(int id, int absoluteAdapterPosition) {
        deleteThisComment(id,absoluteAdapterPosition);
    }
    @Override
    public void onCommentReply(int id, int absoluteAdapterPosition) {
        String commentedUserName = commentsArrayList.get(absoluteAdapterPosition).getUser().getFullName();

        repliedToUsername = commentedUserName;

        String replyingTo = "<B>" + commentedUserName + " " + "</B>";

        commentEditText.setText(Html.fromHtml(replyingTo));

        commentEditText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        commentEditText.setSelection(commentEditText.length());
        interactWithPostBottomSheetFragment.dismiss();

        commentId = commentsArrayList.get(absoluteAdapterPosition).getId();
        replyToCommentPosition = absoluteAdapterPosition;

        postCommentButton.setTag("post reply");
    }

    @Override
    public void onCommentEdit(int id, int absoluteAdapterPosition) {
        commentEditText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        commentEditText.setText(commentsArrayList.get(absoluteAdapterPosition).getComment());
        commentEditText.setSelection(commentEditText.length());

        interactWithPostBottomSheetFragment.dismiss();

        commentId = commentsArrayList.get(absoluteAdapterPosition).getId();

        postCommentButton.setTag("edit comment");
        commentEditedAtPosition = absoluteAdapterPosition;
    }

    @Override
    public void onReplyOptionButtonClick(int commentPosition, String hnid, int replyId, int absoluteAdapterPosition) {
        interactionWithReplyBottomSheet = new InteractionWithReplyBottomSheet(commentPosition, hnid, replyId, this,this, absoluteAdapterPosition);
        interactionWithReplyBottomSheet.show(getParentFragmentManager(), interactionWithReplyBottomSheet.getTag());
    }

    @Override
    public void onReplyDelete(int commentPosition, int replyPosition, int replyID) {
        deleteThisReply(commentPosition,replyPosition,replyID);
    }

    @Override
    public void onReplyEdit(int commentPosition, int replyPosition, int replyID) {
        commentEditText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        commentEditText.setText(commentsArrayList.get(commentPosition).getReplies().get(replyPosition).getComment());
        commentEditText.setSelection(commentEditText.length());
        interactionWithReplyBottomSheet.dismiss();

        commentId = commentsArrayList.get(commentPosition).getReplies().get(replyPosition).getId();
        postCommentButton.setTag("edit reply");
        commentEditedAtPosition = commentPosition;
        replyEditedAtPosition = replyPosition;
    }
}