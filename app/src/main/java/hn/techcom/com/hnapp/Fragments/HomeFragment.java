package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Adapters.PostListAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hnapp.Models.PostList;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnOptionsButtonClickListener {
    //Constants
    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerView;

    private Utils myUtils;
    private Profile userProfile;
    public  ArrayList<PostList> globalPostList;
    private PostListAdapter postListAdapter;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);

        //Hooks
        MaterialTextView screenTitle = view.findViewById(R.id.text_screen_title_supportsection);

        recyclerView = view.findViewById(R.id.recyclerview_posts_supportsection);

        screenTitle.setText(R.string.home);
        globalPostList = new ArrayList<>();

        //Setting up user avatar on top bar
        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        getLatestGlobalPostList();
        // Inflate the layout for this fragment
        return view;
    }

    public void getLatestGlobalPostList(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getLatestGlobalPosts(userProfile.getHnid());

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if(response.code() == 200){
                    PostList latestGlobalPostList = response.body();
                    if (latestGlobalPostList != null) {
                        Log.d(TAG,"next global post list url = "+latestGlobalPostList.getNext());

                        getGlobalPostsFromNextPage(latestGlobalPostList.getNext());

                        ArrayList<Result> postList = new ArrayList<>();
                        postList.addAll(latestGlobalPostList.getResults());

                        Log.d(TAG,"number of posts to show = "+postList.size());

                        setRecyclerView(postList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {

            }
        });
    }

    public void getGlobalPostsFromNextPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<PostList> call = service.getGlobalPostsFromPage(nextPageUrl);

        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NonNull Call<PostList> call, @NonNull Response<PostList> response) {
                if(response.code() == 200){
                    PostList globalPostList = response.body();
                    if (globalPostList != null) {
                        Log.d(TAG,"next global post list url = "+globalPostList.getNext());
                        Log.d(TAG,"previous global post list url = "+globalPostList.getPrevious());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostList> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onOptionsButtonClick(int position) {
        InteractWithPostBottomSheetFragment interactWithPostBottomSheetFragment = new InteractWithPostBottomSheetFragment();
        interactWithPostBottomSheetFragment.show(getParentFragmentManager(), interactWithPostBottomSheetFragment.getTag());
    }

    public void setRecyclerView(ArrayList<Result> postList){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postListAdapter = new PostListAdapter(recyclerView, postList, getContext(),this);
        recyclerView.setAdapter(postListAdapter);
    }
}
