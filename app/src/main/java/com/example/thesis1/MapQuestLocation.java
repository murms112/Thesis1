package com.example.thesis1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapQuestLocation {
    @SerializedName("adminArea5")
    @Expose
    private String cityName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @SerializedName("adminArea3")
    @Expose
    private String stateName;

    public String getstateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
