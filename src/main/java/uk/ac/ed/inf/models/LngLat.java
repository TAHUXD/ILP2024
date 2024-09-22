package uk.ac.ed.inf.models;

public class LngLat {
    private double lng;
    private double lat;

    // Default constructor
    public LngLat() {}

    // Parameterized constructor
    public LngLat(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    // Getters and setters
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
}