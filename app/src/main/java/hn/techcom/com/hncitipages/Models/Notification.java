package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("to_user")
    @Expose
    private String toUser;
    @SerializedName("from_user")
    @Expose
    private NotificationUser notificationUser;
    @SerializedName("notification_type")
    @Expose
    private String notificationType;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("post")
    @Expose
    private Integer post;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public NotificationUser getNotificationUser() {
        return notificationUser;
    }

    public void setNotificationUser(NotificationUser notificationUser) {
        this.notificationUser = notificationUser;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }
}
