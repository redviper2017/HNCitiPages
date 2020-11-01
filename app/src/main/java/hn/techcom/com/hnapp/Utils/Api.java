package hn.techcom.com.hnapp.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Model.SupporterProfile;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class Api {

    private static final String TAG = "Api";

    private Api() {
    }

    public void fetchSupportedProfiles(int userId) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<SupporterProfile>> call = service.getSupportedProfiles(String.valueOf(userId));
        call.enqueue(new Callback<List<SupporterProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<SupporterProfile>> call,@NonNull Response<List<SupporterProfile>> response) {
                ArrayList<SupporterProfile> userSupportedProfiles = new ArrayList<>(Objects.requireNonNull(response.body()));
                Log.d(TAG,"this user is supported by = "+userSupportedProfiles.get(0).getFullName());
            }

            @Override
            public void onFailure(@NonNull Call<List<SupporterProfile>> call,@NonNull Throwable t) {

            }
        });
    }
}
