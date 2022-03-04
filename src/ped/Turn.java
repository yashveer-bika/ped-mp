package ped;

public class Turn extends Phase {
    // private Link i, j;
    // private String direction;
    // direction:
    //  "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE",
    //  "N", "S", "W", "E",

    // EACH TURNING MOVEMENT HAS A CAPACITY OF MOVEMENT OVER A TIME STEP
    private double capacity;

    public Turn() {
        System.out.println("Turn made : empty constructor");
    }

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

    public double getPedWaitTime() {
        return 69.0;
    }

    public double getCapacity() {
        return this.capacity;
    }
}
