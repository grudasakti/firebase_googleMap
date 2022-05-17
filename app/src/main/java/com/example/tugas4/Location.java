package com.example.tugas4;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Location {
    private String latitude;
    private String longtitude;

    public Location(){
    }

    public Location(String latitude, String longtitude){
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongtitude(){
        return longtitude;
    }
}
