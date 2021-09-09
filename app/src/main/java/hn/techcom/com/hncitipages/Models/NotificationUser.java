package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationUser {

    @SerializedName("hnid")
    @Expose
    private String hnid;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("profile_img_thumbnail")
    @Expose
    private String profileImgThumbnail;

    public String getHnid() {
        return hnid;
    }

    public void setHnid(String hnid) {
        this.hnid = hnid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImgThumbnail() {
        return profileImgThumbnail;
    }

    public void setProfileImgThumbnail(String profileImgThumbnail) {
        this.profileImgThumbnail = profileImgThumbnail;
    }

}
