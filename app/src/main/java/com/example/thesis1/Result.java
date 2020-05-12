package com.example.thesis1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("locations")
    @Expose
    private List<MapQuestLocation> locations = null;

    public List<MapQuestLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<MapQuestLocation> locations) {
        this.locations = locations;
    }
}
