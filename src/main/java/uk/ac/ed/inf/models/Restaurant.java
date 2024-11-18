package uk.ac.ed.inf.models;

import java.util.List;

public class Restaurant {
    private String name;
    private LngLat location;
    private List<String> openingDays;
    private List<Pizza> menu;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LngLat getLocation() { return location; }
    public void setLocation(LngLat location) { this.location = location; }

    public List<String> getOpeningDays() { return openingDays; }
    public void setOpeningDays(List<String> openingDays) { this.openingDays = openingDays; }

    public List<Pizza> getMenu() { return menu; }
    public void setMenu(List<Pizza> menu) { this.menu = menu; }
}
