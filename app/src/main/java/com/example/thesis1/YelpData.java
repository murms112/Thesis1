package com.example.thesis1;

import java.util.List;

public class YelpData {
    private List<Businesses> businesses;

    public void setBusinesses(List<Businesses> businesses){
        this.businesses = businesses;
    }
    public List<Businesses> getBusinesses(){
        return this.businesses;
    }
}
