package hn.techcom.com.hnapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("multiple_media")
    @Expose
    private Boolean multipleMedia;
    @SerializedName("isLiked")
    @Expose
    private Boolean isLiked;
    @SerializedName("isSupported")
    @Expose
    private Boolean isSupported;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("posttype")
    @Expose
    private String posttype;
    @SerializedName("files")
    @Expose
    private List<File> files = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getMultipleMedia() {
        return multipleMedia;
    }

    public void setMultipleMedia(Boolean multipleMedia) {
        this.multipleMedia = multipleMedia;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsSupported() {
        return isSupported;
    }

    public void setIsSupported(Boolean isSupported) {
        this.isSupported = isSupported;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
