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
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.LikeListAdapter;
import hn.techcom.com.hncitipages.Adapters.NotificationAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Interfaces.OnNotificationClickListener;
import hn.techcom.com.hncitipages.Models.Notification;
import hn.techcom.com.hncitipages.Models.NotificationsResponse;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.ResultViewLikes;
import hn.techcom.com.hncitipages.Models.ViewLikesResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment
        extends Fragment
        implements
        View.OnClickListener,
        OnNotificationClickListener {

    private String hnid;
    private Utils myUtils;
    private Profile userProfile;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView screenTitle;
    private MaterialTextView notificationCountText;

    private LinearLayout notFoundLayout;

    private ArrayList<Notification> notificationArrayList;
    private NotificationAdapter notificationAdapter;

    private String nextNotificationPageUrl;
    private ShimmerFrameLayout shimmerFrameLayout;


    private static final String TAG ="NotificationsFragment";

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        hnid = userProfile.getHnid();

        //Hooks
        ImageButton backButton         = view.findViewById(R.id.image_button_back);
        screenTitle                    = view.findViewById(R.id.text_screen_title_view_likes);
        notificationCountText          = view.findViewById(R.id.text_notification_count_view_likes);
        recyclerView                   = view.findViewById(R.id.recyclerview_posts_notifications);
        swipeRefreshLayout             = view.findViewById(R.id.swipeRefresh);
        shimmerFrameLayout             = view.findViewById(R.id.shimmerLayout);
        notFoundLayout                 = view.findViewById(R.id.notfound_layout);

        notificationArrayList = new ArrayList<>();

        getNotifications();

        backButton.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && dy>0){
                    //scrolled to bottom
                    Log.d(TAG,"Recycler view scroll position = "+"BOTTOM");
                    if (notificationArrayList.get(notificationArrayList.size()-1) == null) {
                        notificationArrayList.remove(notificationArrayList.size() - 1);
                        getNotificationsFromNextPage(nextNotificationPageUrl);
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationArrayList.clear();
                getNotifications();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void getNotifications(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NotificationsResponse> call = service.getUserNotifications(hnid);

        call.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationsResponse> call, @NonNull Response<NotificationsResponse> response) {
                if (response.code() == 200){
                    NotificationsResponse notificationsResponse = response.body();
                    Log.d(TAG,"my notification number = "+notificationsResponse.getCount());
                    if (notificationsResponse != null && notificationsResponse.getNotification().size() != 0) {
                        Log.d(TAG,"total number of notifications received = "+notificationsResponse.getCount());
                        notificationArrayList.addAll(notificationsResponse.getNotification());
                        notificationCountText.setText(String.valueOf(notificationsResponse.getCount()));
                        if (notificationsResponse.getCount()>0)
                            screenTitle.setText("Notificatios");
                        else
                            screenTitle.setText("Notification");

                        nextNotificationPageUrl = notificationsResponse.getNext();

                        notificationArrayList = myUtils.setNotificationPostRelativeTime(notificationArrayList);

                        if (nextNotificationPageUrl != null) {
                            notificationArrayList.add(null);
                        }

                        setRecyclerView(notificationArrayList);
                    }
                    else {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        notFoundLayout.setVisibility(View.VISIBLE);
                        screenTitle.setText("Notificatios");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    public void getNotificationsFromNextPage(String nextPageUrl){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NotificationsResponse> call = service.getUserNotificationsFromPage(nextPageUrl);
        call.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                if (response.code() == 200){
                    NotificationsResponse notificationsResponse = response.body();
                    if (notificationsResponse != null && notificationsResponse.getNotification().size() != 0){
                        notificationArrayList.addAll(notificationsResponse.getNotification());
                    }
                    nextNotificationPageUrl = notificationsResponse.getNext();
                    if (nextNotificationPageUrl != null) {
                        notificationArrayList.add(null);
                        getNotificationsFromNextPage(nextNotificationPageUrl);
                    }
                    notificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void setRecyclerView(ArrayList<Notification> notificationArrayList){
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(notificationArrayList, getContext(),this);
        recyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public void onNotificationClick(String type, String name, String id, boolean isSupported, int postId) {


        if (type.equals("S")) {
            Fragment fragment = new ProfileSectionFragment();

            Bundle bundle = new Bundle();
            bundle.putString("type",type);
            bundle.putString("name",name);
            bundle.putBoolean("isSupported",isSupported);
            bundle.putString("hnid",id);
            bundle.putString("postId", "0");

            fragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
        }else {
            Fragment fragment = new ProfileSectionFragment();

            Bundle bundle = new Bundle();
            bundle.putString("type",type);
            bundle.putString("name",userProfile.getFullName());
            bundle.putBoolean("isSupported",false);
            bundle.putString("hnid",userProfile.getHnid());
            bundle.putString("postId", String.valueOf(postId));

            fragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
        }
    }
}