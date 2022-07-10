package com.example.shopeer;

import java.util.ArrayList;

public class SearchObject implements Comparable{

    private String searchName;
    private String location;
    private double lat;
    private double lon;
    private int range;
    private int budget;
    private ArrayList<String> activities;

    public SearchObject(String searchName, String location, double lat, double lon, int range, int budget, ArrayList<String> activities) {
        this.searchName = searchName;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.range = range;
        this.budget = budget;
        this.activities = activities;
    }

    public String getSearchName() {
        return searchName;
    }

    public String getLocation() {
        return location;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getBudget() {
        return budget;
    }

    public int getRange() {
        return range;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    @Override
    public String toString() {
        return this.searchName; // What to display in the Spinner list.
    }

    @Override
    public int compareTo(Object o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }
}
