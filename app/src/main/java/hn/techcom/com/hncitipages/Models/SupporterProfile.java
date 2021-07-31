package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupporterProfile {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hnid")
    @Expose
    private String hnid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profile_img_url")
    @Expose
    private String profileImgUrl;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("post_count")
    @Expose
    private Integer postCount;
    @SerializedName("follower_count")
    @Expose
    private Integer followerCount;
    @SerializedName("following_count")
    @Expose
    private Integer followingCount;

    public Integer getId() {
        return id;
    }

    public String getHnid() {
        return hnid;
    }

    public void setHnid(String hnid) {
        this.hnid = hnid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

}
