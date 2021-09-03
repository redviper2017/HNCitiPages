package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.LikeListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.ResultViewLikes;
import hn.techcom.com.hncitipages.Models.ViewLikesResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesFragment extends Fragment implements View.OnClickListener{

    private MaterialTextView likeCountText;
    private RecyclerView recyclerView;
    private LikeListAdapter likesListAdapter;
    private MaterialTextView screenTitle;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "LikesFragment";
    private ArrayList<ResultViewLikes> likesArrayList;
    private int postId;

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            postId = bundle.getInt("post_id");
            Log.d(TAG,"post id in LikesFragment = "+postId);
        }

        //Hooks
        ImageButton backButton         = view.findViewById(R.id.image_button_back);
        screenTitle                    = view.findViewById(R.id.text_screen_title_view_likes);
        likeCountText                  = view.findViewById(R.id.text_like_count_view_likes);
        recyclerView                   = view.findViewById(R.id.recyclerview_posts_likes);
        swipeRefreshLayout             = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout             = view.findViewById(R.id.shimmerLayout);
        likesArrayList                 = new ArrayList<>();

        viewLikesOnPost();
        //OnClick Listeners
        backButton.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                likesArrayList.clear();
                viewLikesOnPost();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void viewLikesOnPost(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ViewLikesResponse> call = service.viewLikes(postId);
        call.enqueue(new Callback<ViewLikesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ViewLikesResponse> call, @NonNull Response<ViewLikesResponse> response) {
                if(response.code() == 200) {
                    ViewLikesResponse list = response.body();

                    if (list != null && list.getResults().size() != 0) {
                        likesArrayList.addAll(list.getResults());
                        likeCountText.setText(String.valueOf(likesArrayList.size()));
                        if (list.getCount() == 1)
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
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        likesListAdapter = new LikeListAdapter(recyclerView, likeList, getContext());
        recyclerView.setAdapter(likesListAdapter);
    }
}