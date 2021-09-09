package hn.techcom.com.hncitipages.Interfaces;

import java.util.List;

import hn.techcom.com.hncitipages.Models.DeleteResponse;
import hn.techcom.com.hncitipages.Models.FavoriteResponse;
import hn.techcom.com.hncitipages.Models.LikeResponse;
import hn.techcom.com.hncitipages.Models.Location;
import hn.techcom.com.hncitipages.Models.NetworkLocation;
import hn.techcom.com.hncitipages.Models.NewPostResponse;
import hn.techcom.com.hncitipages.Models.PostList;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Models.ResultViewComments;
import hn.techcom.com.hncitipages.Models.SingleUserInfoResponse;
import hn.techcom.com.hncitipages.Models.SupportingProfileList;
import hn.techcom.com.hncitipages.Models.SupporterProfile;
import hn.techcom.com.hncitipages.Models.ValidationResponse;
import hn.techcom.com.hncitipages.Models.ViewCommentResponse;
import hn.techcom.com.hncitipages.Models.ViewLikesResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
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


    // APIs for New Server


    // Email validation
    @GET("users/emailvalidate/{email}/")
    Call<ValidationResponse> validateEmail(@Path("email") String email);


    // Register New User
    @Multipart
    @POST("users/registration/")
    Call<Profile> registerNewUser(
            @Part("email") RequestBody email,
            @Part("mobile_number") RequestBody mobile_number,
            @Part("full_name") RequestBody full_name,
            @Part("date_of_birth") RequestBody date_of_birth,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("gender") RequestBody gender,
            @Part MultipartBody.Part first_img
    );

    //Share Story Post
    @Multipart
    @POST("posts/create/")
    Call<NewPostResponse> shareStory(
            @Part("user") RequestBody user,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("posttype") RequestBody posttype,
            @Part("category") RequestBody category,
            @Part("text") RequestBody text
    );

    //Share Video Post
    @Multipart
    @POST("posts/create/")
    Call<NewPostResponse> shareVideo(
            @Part("user") RequestBody user,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("posttype") RequestBody posttype,
            @Part("category") RequestBody category,
            @Part("text") RequestBody text,
            @Part("aspect") RequestBody aspect,
            @Part MultipartBody.Part file
    );

    //Share Image Post
    @Multipart
    @POST("posts/create/")
    Call<NewPostResponse> shareImage(
            @Part("user") RequestBody user,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("posttype") RequestBody posttype,
            @Part("category") RequestBody category,
            @Part("text") RequestBody text,
            @Part("aspect") RequestBody aspect,
            @Part MultipartBody.Part file
    );

    //Share Audio Post
    @Multipart
    @POST("posts/create/")
    Call<NewPostResponse> shareAudio(
            @Part("user") RequestBody user,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @Part("posttype") RequestBody posttype,
            @Part("category") RequestBody category,
            @Part("text") RequestBody text,
            @Part MultipartBody.Part file
    );

    //Get all latest global posts
    @GET("posts/get/{hnid}/all/anything/")
    Call<PostList> getLatestGlobalPosts(@Path("hnid") String hnid);

    //Get global posts from a specified page
    @GET()
    Call<PostList> getGlobalPostsFromPage(@Url String url);

//    //Get Likes on post
//    @GET("posts/get_like_post/{post_id}/")
//    Call<ViewLikesResponse> viewLikes(@Path("post_id") int post_id);

    //Get Likes on post (from user's perspective) NEW API
    @GET("posts/get_like_post_update/{post_id}/{hnid}/")
    Call<ViewLikesResponse> viewLikes(@Path("post_id") int post_id, @Path("hnid") String hnid);

//    //Get latest comments on post
//    @GET("posts/get_post_comment/{post_id}/")
//    Call<ViewCommentResponse> viewComments(@Path("post_id") int post_id);

    //Get latest comments on post (from user's perspective) NEW API
    @GET("posts/get_post_comment_update/{post_id}/{hnid}/")
    Call<ViewCommentResponse> viewComments(@Path("post_id") int post_id, @Path("hnid") String hnid);

    //Get comments from page
    @GET()
    Call<ViewCommentResponse> viewCommentsFromPage(@Url String url);

    //Like/Un-Like Post
    @Multipart
    @POST("posts/like_post/")
    Call<LikeResponse> likeOrUnlikePost(
         @Part("user") RequestBody user,
         @Part("post") RequestBody post
    );

    //Delete/change upload status post
    @POST("posts/delete_post/{post_id}/")
    Call<DeleteResponse> deletePost(@Path("post_id") int post_id);

    //Favorite/Un-Favorite post
    @Multipart
    @POST("posts/post_bookmark/")
    Call<FavoriteResponse> favoriteOrUnfavoritePost(
            @Part("user") RequestBody user,
            @Part("post") RequestBody post
    );

    //Support/Un-Support User
    @Multipart
    @POST("users/support_users/")
    Call<LikeResponse> supportOrUnsupportUser(
            @Part("supporter") RequestBody supporter,
            @Part("supporting") RequestBody supporting
    );

    //Comment on post
    @Multipart
    @POST("posts/post_comment/")
    Call<ResultViewComments> commentOnPost(
            @Part("user") RequestBody user,
            @Part("post") RequestBody post,
            @Part("comment") RequestBody comment
    );

    //Reply on post
    @Multipart
    @POST("posts/post_comment/")
    Call<Reply> replyOnPost(
            @Part("user") RequestBody user,
            @Part("post") RequestBody post,
            @Part("reply_comment") RequestBody reply_comment,
            @Part("comment") RequestBody comment
    );

    //Get Supporting Profiles
    @GET("users/supporting/{hnid}/")
    Call<SupportingProfileList> getSupportingProfiles(@Path("hnid") String hnid);

    //Get Supporting Profiles from page
    @GET()
    Call<SupportingProfileList> getSupportingProfilesFromPage(@Url String url);


    //Get Supporters Profile
    @GET("users/supporters/{hnid}/")
    Call<SupportingProfileList> getSupporterProfiles(@Path("hnid") String hnid);

    //Get Supported Profile Posts List
    @GET("posts/get_supporting_posts/{hnid}/")
    Call<PostList> getLatestSupportingProfilePosts(@Path("hnid") String hnid);

    //Get supported profile posts from a specified page
    @GET()
    Call<PostList> getSupportingProfilePostsFromPage(@Url String url);

    //Get all latest favorite posts
    @GET("posts/get_post_bookmark/{hnid}/")
    Call<PostList> getLatestFavoritePosts(@Path("hnid") String hnid);

    //Get all favorite posts from a specified page
    @GET()
    Call<PostList> getFavoritePostsFromPage(@Url String url);

    //Get all cities & countries
    @GET("get_country_city/")
    Call<List<Location>> getLocations();

    //Get latest Posts by city
    @GET("posts/get/{hnid}/city/{city}/")
    Call<PostList> getLatestPostsFromCity(
            @Path("hnid") String hnid,
            @Path("city") String city
    );

    //Get all posts by city from a specified page
    @GET()
    Call<PostList> getPostsFromCityFromPage(@Url String url);

    //Get latest Posts by country
    @GET("posts/get/{hnid}/country/{country}/")
    Call<PostList> getLatestPostsFromCountry(
            @Path("hnid") String hnid,
            @Path("country") String country
    );

    //Get all posts by country from a specified page
    @GET()
    Call<PostList> getPostsFromCountryFromPage(@Url String url);

    //Get all posts by single user
    @GET("posts/get_single_user_posts/{target_hnid}/{hnid}/")
    Call<PostList> getLatestPostsBySingleUser( @Path("target_hnid") String target_hnid, @Path("hnid") String hnid);

    //Get posts by a single user from a specified page
    @GET()
    Call<PostList> getPostsBySingleUserFromPage(@Url String url);

    //Update Profile Image
    @Multipart
    @POST("users/updateuser/")
    Call<LikeResponse> updateProfileImage(
            @Part("user") RequestBody user,
            @Part MultipartBody.Part profile_img
    );

    //Get single user info
    @GET("users/single_userinfo/{hnid}/")
    Call<SingleUserInfoResponse> getUserInfo(@Path("hnid") String hnid);

    //Get Network location
    @GET("http://ip-api.com/json/")
    Call<NetworkLocation> getNetworkLocation();

    //Get Latest Post by Type (i.e. S - Story, V - Videos, A - Audios, I - Images)
    @GET("posts/get/{hnid}/pbt/{type}")
    Call<PostList> getLatestPostByType(@Path("hnid") String hnid, @Path("type") String type);

    //Get Latest Post by Type from page (i.e. S - Story, V - Videos, A - Audios, I - Images)
    @GET()
    Call<PostList> getPostsByTypeFromPage(@Url String url);
}
