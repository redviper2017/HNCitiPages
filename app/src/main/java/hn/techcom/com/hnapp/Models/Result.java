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
    private Object city;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("multiple_media")
    @Expose
    private Boolean multipleMedia;
    @SerializedName("posttype")
    @Expose
    private String posttype;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("isLiked")
    @Expose
    private Boolean isLiked;
    @SerializedName("isSupported")
    @Expose
    private Boolean isSupported;
    @SerializedName("files")
    @Expose
    private List<File> files = null;
    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;

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

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
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

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
