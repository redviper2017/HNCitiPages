package hn.techcom.com.hnapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hn.techcom.com.hnapp.Adapters.AvatarLoaderAdapter;
import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Model.SupporterProfile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import hn.techcom.com.hnapp.Utils.Api;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportedSectionFragment extends Fragment {

    private static final String TAG = "SupportedProfileSection";

    private RecyclerView supportedProfileAvatars;

    static ArrayList<SupporterProfile> userSupportedProfiles;

    public SupportedSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_profile_section, container, false);

        supportedProfileAvatars = view.findViewById(R.id.recyclerview_supported_avatars_supportsection);


        //function calls
        getSupportedProfiles();


        // Inflate the layout for this fragment
        return view;
    }

    // this function retrieves the list of supported profiles by the current user
    public void getSupportedProfiles() {
        //here the user id is 1 which will come from local db
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<SupporterProfile>> call = service.getSupportedProfiles("1");
        call.enqueue(new Callback<List<SupporterProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<SupporterProfile>> call,@NonNull Response<List<SupporterProfile>> response) {
                userSupportedProfiles = new ArrayList<>(Objects.requireNonNull(response.body()));
                Log.d(TAG,"this user is supported by = "+userSupportedProfiles.get(0).getFullName());

                setSupportedProfileAvatars(userSupportedProfiles);
            }

            @Override
            public void onFailure(@NonNull Call<List<SupporterProfile>> call,@NonNull Throwable t) {
                Log.d(TAG,"request failed = "+"True: "+t.getMessage());
            }
        });
    }

    // this function sets data to the recyclerview
    public void setSupportedProfileAvatars(ArrayList<SupporterProfile> userSupportedProfiles){
        ArrayList<String> avatarList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        for (SupporterProfile supporterProfile : userSupportedProfiles){
            avatarList.add(supporterProfile.getProfileImgUrl());
            nameList.add(supporterProfile.getFullName());
        }
        Log.d(TAG,"avatar list size = "+avatarList.size());
        AvatarLoaderAdapter adapter = new AvatarLoaderAdapter(avatarList,nameList);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        supportedProfileAvatars.setHasFixedSize(true);
        supportedProfileAvatars.setLayoutManager(horizontalLayout);
        supportedProfileAvatars.setAdapter(adapter);
    }

}