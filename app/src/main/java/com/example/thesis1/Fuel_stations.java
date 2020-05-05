package com.example.thesis1;

//class necessary for JSON conversion
public class Fuel_stations
{

    private String station_name;

    private String intersection_directions;

    private String street_address;


    public void setStation_name(String station_name){
        this.station_name = station_name;
    }
    public String getStation_name(){
        return this.station_name;
    }
    public void setIntersection_directions(String intersection_directions){
        this.intersection_directions = intersection_directions;
    }
    public String getIntersection_directions(){
        return this.intersection_directions;
    }
    public void setStreet_address(String street_address){
        this.street_address = street_address;
    }
    public String getStreet_address(){
        return this.street_address;
    }
}

