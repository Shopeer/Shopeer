package com.example.shopeer;

import java.util.ArrayList;

public class SearchObject {

    String location;
    double lat;
    double lon;
    long range;
    long budget;
    ArrayList<String> activity;

    public SearchObject(String location, double lat, double lon, long range, long budget, ArrayList<String> activity) {
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.budget = budget;
        this.activity = activity;
    }


}
