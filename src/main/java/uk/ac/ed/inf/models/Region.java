package uk.ac.ed.inf.models;

import java.util.List;

public class Region {
    private String name;
    private List<LngLat> vertices;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<LngLat> getVertices() { return vertices; }
    public void setVertices(List<LngLat> vertices) { this.vertices = vertices; }
}

