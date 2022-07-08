package com.example.shopeer;

import java.util.ArrayList;

public class SearchObject {

    private String searchName;
    String location;
    double lat;
    double lon;
    long range;
    long budget;
    ArrayList<String> activity;

    public SearchObject(String searchName, String location, double lat, double lon, long range, long budget, ArrayList<String> activity) {
        this.searchName = searchName;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.budget = budget;
        this.activity = activity;
    }

    public String getSearchName() {
        return searchName;
    }

    @Override
    public String toString() {
        return this.searchName; // What to display in the Spinner list.
    }
}
