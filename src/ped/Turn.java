package ped;

public class Turn {
    private Link i, j;
    private String direction;
    // direction: "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"

    public Turn(Link incoming, Link outgoing) {
        // TODO: enforce that the links are connected

        i = incoming;
        j = outgoing;
        this.direction = "" + incoming.getDirection().charAt(0) + outgoing.getDirection().charAt(1);
    }

    @Override
    public String toString() {
        return "Turn{" +
                "i=" + i +
                ", j=" + j +
                ", direction='" + direction + '\'' +
                '}';
    }
}
