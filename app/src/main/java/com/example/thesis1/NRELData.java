package com.example.thesis1;

import java.util.List;

//class necessary for JSON conversion
public class NRELData
{
    private List<Fuel_stations> fuel_stations;

    public void setFuel_stations(List<Fuel_stations> fuel_stations){
        this.fuel_stations = fuel_stations;
    }
    public List<Fuel_stations> getFuel_stations(){
        return this.fuel_stations;
    }
}