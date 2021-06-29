package hn.techcom.com.hnapp.Models;

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
    @SerializedName("profile_img_thumbnail")
    @Expose
    private String profile_img_thumbnail;
    @SerializedName("hnid")
    @Expose
    private String hnid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("isSupported")
    @Expose
    private Boolean isSupported;

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

    public String getProfile_img_thumbnail() {
        return profile_img_thumbnail;
    }

    public void setProfile_img_thumbnail(String profile_img_thumbnail) {
        this.profile_img_thumbnail = profile_img_thumbnail;
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
}
