package ped;
import Geometry.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TurningMovement {
    private Link i,j;
    private double capacity; // Q_{ij}

    // [
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    // ]
    private LinkedList<double[]> queue;
    private double network_time;


    public TurningMovement(Link i, Link j) {
        // need connectivity
        assert i.getDestination() == j.getStart();
        this.i = i;
        this.j = j;
        setCapacity();
        queue = new LinkedList<>();
    }

    public boolean intersects(TurningMovement rhs) {
        // TODO: implement
        return i.intersects(rhs.i) || i.intersects(rhs.j) ||
                j.intersects(rhs.i) || j.intersects(rhs.j) ;
    }

    public double getQueueLength() {
        double q_length = 0;
        for (double[] veh_time_pair : queue) {
            q_length += veh_time_pair[0];
        }
        return q_length;
    }

    /** Capacity as defined in ped-AIM by Rongsheng and Jeffery :
     * Section 3 : Network model **/
    public void setCapacity() {
        this.capacity = Math.min(i.getCapacity(), j.getCapacity());
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getCapacity() {
        return this.capacity;
    }

    // get wait time (how long the first entity to enter the queue has been in the queue)
    public double getWaiting_time() throws EmptyQueueException {
        try {
            double[] front = queue.removeFirst();
            return network_time - front[1];
        } catch (NoSuchElementException e) {
            // Case of empty queue, we want the turning movement to be off, so set waiting time to inf.
            // this forces the controller to turn the turning movement off
            throw new EmptyQueueException("empty queue, so there is no waiting time");
        }
    }

    public void updateTime(double newTime) {
        network_time = newTime;
    }

        public String toString() {
        return "Turn : [" + i + ", " + j +
                "]" +
                "" +
                "";
    }
}
