package hn.techcom.com.hncitipages.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import hn.techcom.com.hncitipages.Adapters.ProfileListAdapter;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.SupportingProfileList;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportingSupporterListFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "SSListFragment";
    private String showListOf;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProfileListAdapter adapter;
    private Utils myUtils;
    private Profile userProfile;
    private ArrayList<User> profilesList;
    private String count;

    public SupportingSupporterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supporting_supporter_list, container, false);

        myUtils     = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());
        showListOf  = requireArguments().getString("Show");

        //Hooks
        ImageButton backButton                = view.findViewById(R.id.image_button_back);
        MaterialTextView screenTitle          = view.findViewById(R.id.text_screen_title);
        MaterialTextView countText            = view.findViewById(R.id.count_text);
        swipeRefreshLayout                    = view.findViewById(R.id.swipeRefresh);
        recyclerView                          = view.findViewById(R.id.recyclerview);

        profilesList = getProfiles();

        if (showListOf.equals("Supporters")) {
            count = requireArguments().getString("SupporterCount");
            countText.setText(count);
            if (Integer.parseInt(count) > 1)
                screenTitle.setText(showListOf);
            else
                screenTitle.setText("Supporter");
        }
        else {
            count = requireArguments().getString("SupportingCount");
            countText.setText(count);
            if (Integer.parseInt(count) > 1)
                screenTitle.setText(showListOf);
            else
                screenTitle.setText("Supporting Profile");
        }
        setRecyclerView(profilesList);

        Log.d(TAG,"number of "+showListOf+" = "+profilesList.size());

        //OnClick Listeners
        backButton.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profilesList.clear();
                if (showListOf.equals("Supporters"))
                    getSupporterProfiles();
                else
                    getSupportingProfiles();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        //TODO
        if(v.getId() == R.id.image_button_back)
            getParentFragmentManager().popBackStack();
    }

    private ArrayList<User> getProfiles(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json;
        if (showListOf.equals("Supporters"))
            json = sharedPreferences.getString("Supporters", null);
        else
            json = sharedPreferences.getString("Supporting", null);

        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void setRecyclerView(ArrayList<User> profilesList){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProfileListAdapter(getContext(), profilesList);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    //get initial supporting profile list
    public void getSupportingProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupportingProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(@NonNull Call<SupportingProfileList> call, @NonNull Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supportingProfileList = response.body();
                    Log.d(TAG, "number of supporting profile = "+ Objects.requireNonNull(supportingProfileList).getCount());
                    count = String.valueOf(supportingProfileList.getCount());
                    if(supportingProfileList.getCount()>0) {
                        profilesList.clear();
                        profilesList.addAll(supportingProfileList.getResults());
                        setRecyclerView(profilesList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SupportingProfileList> call, @NonNull Throwable t) {

            }
        });
    }

    //get initial supporter profile list
    public void getSupporterProfiles(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<SupportingProfileList> call = service.getSupporterProfiles(userProfile.getHnid());
        call.enqueue(new Callback<SupportingProfileList>(){
            @Override
            public void onResponse(Call<SupportingProfileList> call, Response<SupportingProfileList> response) {
                if(response.code() == 200){
                    SupportingProfileList supporterProfileList = response.body();
                    Log.d(TAG, "number of supporter profile = "+ supporterProfileList.getCount());
                    count = String.valueOf(supporterProfileList.getCount());
                    if(supporterProfileList.getCount()>0) {
                        profilesList.clear();
                        profilesList.addAll(supporterProfileList.getResults());
                        setRecyclerView(profilesList);
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportingProfileList> call, Throwable t) {

            }
        });
    }
}