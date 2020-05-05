package com.example.thesis1;

public class AppSettings {
    private static AppSettings app_settings = null;
    private String city;
    private double latitude;
    private double longitude;
    private String state;

    AppSettings(String city, double latitude, double longitude, String state){
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
    }
    public static AppSettings getInstance(String city, double latitude, double longitude, String state)
    {
        if (app_settings == null)
            app_settings = new AppSettings(city, latitude, longitude, state);

        return app_settings;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
