package uk.ac.ed.inf.controllers;
import uk.ac.ed.inf.models.*;

public class Node implements Comparable<Node> {
    private LngLat position;
    private Node parent;
    private double gCost;
    private double hCost;
    private double fCost;
    private boolean enteredCentralArea;

    public Node(LngLat position, Node parent, double gCost, double hCost, boolean enteredCentralArea) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
        this.enteredCentralArea = enteredCentralArea;
    }

    public LngLat getPosition() { return position; }
    public Node getParent() { return parent; }
    public double getGCost() { return gCost; }
    public double getHCost() { return hCost; }
    public double getFCost() { return fCost; }
    public boolean hasEnteredCentralArea() { return enteredCentralArea; }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.fCost, other.fCost);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.position.closeTo(other.position) && this.enteredCentralArea == other.enteredCentralArea;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return position.hashCode() + (enteredCentralArea ? 1 : 0);
    }
}


