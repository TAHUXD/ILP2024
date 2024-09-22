package uk.ac.ed.inf.models;

public class IsInRegionRequest {
    private LngLat position;
    private Region region;

    // Getters and setters
    public LngLat getPosition() { return position; }
    public void setPosition(LngLat position) { this.position = position; }
    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
}
