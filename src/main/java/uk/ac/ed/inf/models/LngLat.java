package uk.ac.ed.inf.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LngLat {
    @JsonProperty("lng")
    private double lng;

    @JsonProperty("lat")
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

    // Utility method to calculate the distance to another LngLat
    public double distanceTo(LngLat other) {
        double dx = this.lng - other.lng;
        double dy = this.lat - other.lat;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Method to check if this position is close to another position
    public boolean closeTo(LngLat other) {
        return this.distanceTo(other) < 0.00015;
    }

    // Method to get the next position given an angle
    public LngLat nextPosition(double angle) {
        if (angle == 999) {
            return this; // Hover
        }
        double distance = 0.00015;
        double rad = Math.toRadians(angle);
        double newLng = this.lng + distance * Math.cos(rad);
        double newLat = this.lat + distance * Math.sin(rad);
        return new LngLat(newLng, newLat);
    }
}
