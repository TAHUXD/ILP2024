package uk.ac.ed.inf.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pizza {
    private String name;
    private int priceInPence;

    // Constructors
    public Pizza() {}

    public Pizza(String name, int priceInPence) {
        this.name = name;
        this.priceInPence = priceInPence;
    }

    // Getters and setters

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("priceInPence")
    public int getPriceInPence() { return priceInPence; }
    public void setPriceInPence(int priceInPence) { this.priceInPence = priceInPence; }
}
