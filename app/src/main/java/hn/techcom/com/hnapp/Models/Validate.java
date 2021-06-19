package hn.techcom.com.hnapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Validate {
    @SerializedName("existing")
    @Expose
    private Boolean existing;

    public Boolean getExisting() {
        return existing;
    }

    public void setExisting(Boolean existing) {
        this.existing = existing;
    }
}
