package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.NotificationsResponse;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.ViewLikesResponse;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private String hnid;
    private Utils myUtils;
    private Profile userProfile;
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

        getNotifications();

        // Inflate the layout for this fragment
        return view;
    }

    public void getNotifications(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<NotificationsResponse> call = service.getUserNotifications(hnid);

        call.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationsResponse> call, @NonNull Response<NotificationsResponse> response) {
                if (response.code() == 200){
                    NotificationsResponse notificationsResponse = response.body();
                    if (notificationsResponse != null) {
                        Log.d(TAG,"total number of notifications received = "+notificationsResponse.getCount());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationsResponse> call, @NonNull Throwable t) {

            }
        });
    }
}