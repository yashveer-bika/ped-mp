package ped;

public class TurningMovement {
    private Link i,j;
    private double capacity; // Q_{ij}
    private double waiting_time;


    public TurningMovement(Link i, Link j) {
        // need connectivity
        assert i.getDestination() == j.getStart();
        this.i = i;
        this.j = j;
    }

    public double getCapacity() {
        return this.capacity;
    }

    public double getWaiting_time() {
        return waiting_time;
    }

    public String toString() {
        return "Turn : [" + i + ", " + j +
                "]" +
                "" +
                "";
    }
}
