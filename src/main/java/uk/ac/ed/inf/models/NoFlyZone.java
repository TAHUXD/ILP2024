package uk.ac.ed.inf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NoFlyZone {
    private String name;

    private List<LngLat> vertices;

    // Getters and setters
    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("vertices")
    public List<LngLat> getVertices() { return vertices; }
    public void setVertices(List<LngLat> vertices) { this.vertices = vertices; }
}
