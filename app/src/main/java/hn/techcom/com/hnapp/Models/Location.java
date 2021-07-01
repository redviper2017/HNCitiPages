package hn.techcom.com.hnapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {
    @SerializedName("cities")
    @Expose
    private List<String> cities = null;
    @SerializedName("countries")
    @Expose
    private List<String> countries = null;

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }
}
