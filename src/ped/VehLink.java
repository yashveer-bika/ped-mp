package ped;

import java.util.HashSet;
import java.util.Set;

public class VehLink extends Link {
    // String direction;
    String type;
    private Set<String> turningDirections;


    public VehLink(Node start, Node end, double C, boolean entry) {
        super(start, end, C, entry);
        turningDirections = new HashSet<String>();
    }

    public VehLink(Node start, Node end, double C) {
        super(start, end, C);
        turningDirections = new HashSet<String>();

    }

    public VehLink(Node start, Node end, double C, String direction) {
        super(start, end, C, direction);
//        this.direction = direction;
        // this.type = type;
        turningDirections = new HashSet<String>();

    }

    public String getLaneType() {
        return this.type;
    }

//    public String getDirection() {
//        return this.direction;
//    }

    public Set<String> getTurningDirections() {
        return this.turningDirections;
    }
}
