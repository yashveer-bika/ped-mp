package ped;

public class Turn extends Phase {
    // private Link i, j;
    // private String direction;
    // direction:
    //  "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE",
    //  "N", "S", "W", "E",

    public Turn(Link incoming, Link outgoing) {
        // TODO: enforce that the links are connected
        assert incoming.getDestination() == outgoing.getStart();

        i = incoming;
        j = outgoing;
//        System.out.println(incoming.getDirection());
//        System.out.println(outgoing.getDirection());

        constructDirection(incoming.getDirection(), outgoing.getDirection());

        // this.direction = "" + incoming.getDirection().charAt(0) + outgoing.getDirection().charAt(1);
        // System.out.println(this.direction);
        this.setId();
    }

    public void constructDirection(String incomingDirection, String outgoingDirection) {
        assert outgoingDirection != "entry";
        if (incomingDirection == "entry") {
            this.direction = "" + outgoingDirection.charAt(1);
        } else {
            this.direction = "" + incomingDirection.charAt(0) + outgoingDirection.charAt(1);
        }
    }
}
