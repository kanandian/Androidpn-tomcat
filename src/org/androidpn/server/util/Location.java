package org.androidpn.server.util;

public class Location {

    private double longitude;
    private double latitude;

    public Location () {

    }

    public Location (String location) {

    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "30,30";
    }
}
