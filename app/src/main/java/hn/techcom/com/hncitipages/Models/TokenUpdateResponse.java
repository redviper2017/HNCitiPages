package hn.techcom.com.hncitipages.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenUpdateResponse {
    @SerializedName("Success")
    @Expose
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
