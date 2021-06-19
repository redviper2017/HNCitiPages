package hn.techcom.com.hnapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewPostResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("posttype")
    @Expose
    private String posttype;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("edited_on")
    @Expose
    private String editedOn;
    @SerializedName("published_status")
    @Expose
    private String publishedStatus;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("deleted_on")
    @Expose
    private Object deletedOn;
    @SerializedName("featured")
    @Expose
    private Boolean featured;
    @SerializedName("media_post")
    @Expose
    private Boolean mediaPost;
    @SerializedName("multiple_media")
    @Expose
    private Boolean multipleMedia;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("user")
    @Expose
    private String user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(String editedOn) {
        this.editedOn = editedOn;
    }

    public String getPublishedStatus() {
        return publishedStatus;
    }

    public void setPublishedStatus(String publishedStatus) {
        this.publishedStatus = publishedStatus;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Object getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(Object deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getMediaPost() {
        return mediaPost;
    }

    public void setMediaPost(Boolean mediaPost) {
        this.mediaPost = mediaPost;
    }

    public Boolean getMultipleMedia() {
        return multipleMedia;
    }

    public void setMultipleMedia(Boolean multipleMedia) {
        this.multipleMedia = multipleMedia;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
