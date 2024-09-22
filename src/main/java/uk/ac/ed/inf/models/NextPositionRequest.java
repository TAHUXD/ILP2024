package uk.ac.ed.inf.models;

public class NextPositionRequest {
    private LngLat start;
    private double angle;

    // Getters and setters
    public LngLat getStart() { return start; }
    public void setStart(LngLat start) { this.start = start; }
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
}
