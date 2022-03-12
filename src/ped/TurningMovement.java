package ped;
import Geometry.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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

    public Link getIncomingLink(){
        return i;
    }

    public Link getOutgoingLink(){
        return j;
    }

    public double getWeight() {
        double weight = this.getQueueLength();
        VehIntersection neigh = (VehIntersection) this.getOutgoingLink().getDestination();
        Set<TurningMovement> downstream_turns = neigh.getVehicleTurns();
        for (TurningMovement downstream_turn : downstream_turns) {
            weight -= downstream_turn.getTurningProportion() * downstream_turn.getQueueLength() ;
        }

        return weight;
    }

    //
    public boolean intersects(TurningMovement rhs) {
        // TODO: this is a simplified model
        // if i = rhs.i , there is no conflict
        // if j = rhs.j , there is conflict
        // we ignore tip touching
        // TODO: verify this satisfies SF
        // TODO: (maybe) add a better intersection/conflict definition

        return  (i.intersects(rhs.i) && !i.equals(rhs.i)) ||
                (i.intersects(rhs.j) && !i.getDestination().equals(rhs.j.getStart())) ||
                (j.intersects(rhs.i) && !rhs.i.getDestination().equals(j.getStart())) ||
                (j.intersects(rhs.j) && !j.getStart().equals(rhs.j.getStart())) ;

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

    public double getTurningProportion() {
        // TODO: note the data setup of the file
        // make the file loading more modular
        // allow for time-dependent data loading
        File pq3_nodes_f = new File("data/PQ3/turning_proportions.txt");

        readTurningProps(pq3_nodes_f, this.i.getStart().getId(), this.i.getDestination().getId(), this.j.getDestination().getId());
        return 0.5;
    }

    private double readTurningProps(File turn_props_file, int upstreamId, int curId, int downstreamId) {
        try {
            String[] header = {};
            Scanner myReader = new Scanner(turn_props_file);
            ArrayList<String[]> turn_prop_rows = new ArrayList();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
//                System.out.println("SINGLE LINE");
//                System.out.println(data);
//                for (String s : data.split("\t")) {
//                    System.out.println("\t" + s);
//                }

                // at header
                if (data.charAt(0) == 'u') {
//                    System.out.println("HEADER");
                    header = data.split("\t");
                }
                // normal node
                else {
                    String[] turn_prop_data = data.split("\t");
//                    for (String s : turn_prop_data) {
//                        System.out.println("\t" + s);
//                    }
                    int upstreamId_ = Integer.parseInt(turn_prop_data[0]);
                    int curId_ = Integer.parseInt(turn_prop_data[1]);
                    int downstreamId_ = Integer.parseInt(turn_prop_data[2]);
                    if (upstreamId == upstreamId_ && curId == curId_ && downstreamId == downstreamId_) {
                        double proportion = Double.parseDouble(turn_prop_data[2]);
                        return proportion;
                    }

                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find turning proportions file");
            e.printStackTrace();
        }
        return -1;
    }

}
