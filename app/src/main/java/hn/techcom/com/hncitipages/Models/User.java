package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;
    @SerializedName("first_img")
    @Expose
    private String firstImg;
    @SerializedName("hnid")
    @Expose
    private String hnid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("isSupported")
    @Expose
    private Boolean isSupported;
    @SerializedName("profile_img_thumbnail")
    @Expose
    private String profileImgThumbnail;
    @SerializedName("supporterCount")
    @Expose
    private Integer supporterCount;
    @SerializedName("supportingCount")
    @Expose
    private Integer supportingCount;
    @SerializedName("postCount")
    @Expose
    private Integer postCount;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(String firstImg) {
        this.firstImg = firstImg;
    }

    public String getHnid() {
        return hnid;
    }

    public void setHnid(String hnid) {
        this.hnid = hnid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsSupported() {
        return isSupported;
    }

    public void setIsSupported(Boolean isSupported) {
        this.isSupported = isSupported;
    }

    public String getProfileImgThumbnail() {
        return profileImgThumbnail;
    }

    public void setProfileImgThumbnail(String profileImgThumbnail) {
        this.profileImgThumbnail = profileImgThumbnail;
    }

    public Integer getSupporterCount() {
        return supporterCount;
    }

    public void setSupporterCount(Integer supporterCount) {
        this.supporterCount = supporterCount;
    }

    public Integer getSupportingCount() {
        return supportingCount;
    }

    public void setSupportingCount(Integer supportingCount) {
        this.supportingCount = supportingCount;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }
}