package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hnapp.Adapters.PostListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.DeleteResponse;
import hn.techcom.com.hnapp.Models.LikeResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractWithPostBottomSheetFragment extends BottomSheetDialogFragment {

    private NavigationView navigationView;
    private int postId, itemPosition;
    private ArrayList<Result> recentPostList;
    private PostListAdapter postListAdapter;
    private String hnid_user;
    private Utils myUtils;
    private Profile userProfile;
    private boolean supporting;

    private static final String TAG = "PostBottomSheetFragment";

    public InteractWithPostBottomSheetFragment(
            int position,
            int id,
            ArrayList<Result> recentPostList,
            PostListAdapter postListAdapter, String hnid_user, boolean supporting) {
        postId = id;
        itemPosition = position;
        this.recentPostList = recentPostList;
        this.postListAdapter = postListAdapter;
        this.hnid_user = hnid_user;
        this.supporting = supporting;
        myUtils = new Utils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        if(recentPostList.get(itemPosition).getUser().getHnid().equals(userProfile.getHnid()))
            view = inflater.inflate(R.layout.fragment_interact_with_post_bottom_sheet_own, container, false);
        else {
            if(supporting)
                view = inflater.inflate(R.layout.fragment_interact_with_post_bottom_sheet_supporting, container, false);
            else
                view = inflater.inflate(R.layout.fragment_interact_with_post_bottom_sheet, container, false);
        }

        navigationView = view.findViewById(R.id.navigation_interact_with_post);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.support_user_post){
                    Toast.makeText(getActivity(), "Support this user",Toast.LENGTH_LONG).show();
                    supportOrUnsupport();
                }
//                if(item.getItemId() == R.id.report_post){
//                    Toast.makeText(getActivity(), "Reporting this post..",Toast.LENGTH_LONG).show();
//                }
                if(item.getItemId() == R.id.delete_post){
                    Toast.makeText(getActivity(), "Deleting this post..",Toast.LENGTH_LONG).show();
                    deleteThisPost();
                }

                return true;
            }
        });
    }

    public void deleteThisPost(){
        Log.d(TAG,"delete post id = "+postId);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<DeleteResponse> call = service.deletePost(postId);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if(response.code() == 200){
                    DeleteResponse deleteResponse = response.body();
                    Toast.makeText(getActivity(), Objects.requireNonNull(deleteResponse).getSuccess(), Toast.LENGTH_LONG).show();

                    Log.d(TAG,"remove item post id = "+postId);

                    Log.d(TAG,"post list size = "+recentPostList.size());
                    recentPostList.remove(itemPosition);
                    Log.d(TAG,"post list size = "+recentPostList.size());

//                    postListAdapter.notifyItemRemoved(itemPosition);

                    postListAdapter.notifyDataSetChanged();

                    dismiss();
                }
                else
                    Toast.makeText(getActivity(),"Sorry, the post cannot be deleted at this moment. Try again..", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(getActivity(),"Sorry, the delete request has been failed. Try again..", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void supportOrUnsupport(){
        Log.d(TAG,"support or unsupport user with hnid = "+hnid_user);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        RequestBody supporter = RequestBody.create(MediaType.parse("text/plain"), userProfile.getHnid());
        RequestBody supporting = RequestBody.create(MediaType.parse("text/plain"), hnid_user);


        Call<LikeResponse> call = service.supportOrUnsupportUser(supporter,supporting);
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                if(response.code() == 201){
                    LikeResponse supportResponse = response.body();
                    Toast.makeText(getActivity(), Objects.requireNonNull(supportResponse).getMessage(), Toast.LENGTH_LONG).show();
                    postListAdapter.changeSupportingStatus(itemPosition);
                    dismiss();
                }else {
                    Toast.makeText(getActivity(), "Sorry, the user cannot be supported at this moment. Try again..", Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(getActivity(),"Sorry, the support request has been failed. Try again..", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }
}