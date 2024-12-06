package uk.ac.ed.inf.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
    private String name;
    private LngLat location;
    private List<String> openingDays;
    private List<Pizza> menu;

    // Getters and setters

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("location")
    public LngLat getLocation() { return location; }
    public void setLocation(LngLat location) { this.location = location; }

    @JsonProperty("openingDays")
    public List<String> getOpeningDays() { return openingDays; }
    public void setOpeningDays(List<String> openingDays) { this.openingDays = openingDays; }

    @JsonProperty("menu")
    public List<Pizza> getMenu() { return menu; }
    public void setMenu(List<Pizza> menu) { this.menu = menu; }
}
