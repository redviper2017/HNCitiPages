package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("hnid")
    @Expose
    private String hnid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_img")
    @Expose
    private String profileImg;
    @SerializedName("first_img")
    @Expose
    private String firstImg;

    @SerializedName("profile_img_thumbnail")
    @Expose
    private String profileImgThumbnail;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProfileImgThumbnail() {
        return profileImgThumbnail;
    }

    public void setProfileImgThumbnail(String profileImgThumbnail) {
        this.profileImgThumbnail = profileImgThumbnail;
    }

    public void setFirstImg(String firstImg) {
        this.firstImg = firstImg;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "hnid='" + hnid + '\'' +
                ", username='" + username + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", firstImg='" + firstImg + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}
