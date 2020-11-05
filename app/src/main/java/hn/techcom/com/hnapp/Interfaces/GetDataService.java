package hn.techcom.com.hnapp.Interfaces;

import java.util.List;

import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.QUser;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetDataService {

    //Fetch Supporting Profiles List
    @GET("users/support/list/{user_id}/")
    Call<List<SupporterProfile>> getSupportedProfiles (@Path("user_id") String user_id);

    //Fetch All Posts By Single User With Current User's (QUser = Query User) perspective
    @POST("posts/{posts_by_username}/")
    Call<List<Post>> getAllPostsBy(@Path("posts_by_username") String posts_by_username, @Body QUser queryUser);
}
