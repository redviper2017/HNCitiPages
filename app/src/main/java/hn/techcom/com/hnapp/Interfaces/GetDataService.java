package hn.techcom.com.hnapp.Interfaces;

import java.util.List;

import hn.techcom.com.hnapp.Models.NewUser;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.QUser;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.Models.Validate;
import hn.techcom.com.hnapp.Models.ValidationResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GetDataService {

    //Fetch Supporting Profiles List
    @GET("users/support/list/{user_id}/")
    Call<List<SupporterProfile>> getSupportedProfiles (@Path("user_id") String user_id);

    //Fetch All Posts By Single User With Current User's (QUser = Query User) perspective
    @POST("posts/{posts_by_username}/")
    Call<List<Post>> getAllPostsBy(@Path("posts_by_username") String posts_by_username, @Body QUser queryUser);


    // APIs for New Server


    // Email validation
    @GET()
    Call<ValidationResponse> validateEmail(@Url String url);


    // Register New User
    @Multipart
    @POST("users/registration/")
    Call<NewUser> registerNewUser(
            @Part("email") RequestBody email,
            @Part("mobile_number") RequestBody mobile_number,
            @Part("full_name") RequestBody full_name,
            @Part("date_of_birth") RequestBody date_of_birth,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part first_img
    );
}
