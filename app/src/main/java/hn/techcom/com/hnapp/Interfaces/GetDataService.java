package hn.techcom.com.hnapp.Interfaces;

import java.util.List;

import hn.techcom.com.hnapp.Model.SupporterProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GetDataService {

    //Fetch Supporting Profiles List
    @GET("/users/support/list/{user_id}/")
    Call<List<SupporterProfile>> getSupportedProfiles (@Path(value="user_id",encoded = true) String userId);
}
