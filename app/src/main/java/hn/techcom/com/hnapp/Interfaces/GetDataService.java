package hn.techcom.com.hnapp.Interfaces;

import java.util.List;

import hn.techcom.com.hnapp.Model.SupporterProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetDataService {

    //Fetch Supporting Profiles List
    @GET()
    Call<List<SupporterProfile>> getSupportedProfiles (@Url String url);
}
