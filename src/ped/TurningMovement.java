package ped;
import Geometry.*;
import util.Angle;
import util.DoubleE;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TurningMovement {
    private Network engine;
    private Link i,j;
    private int capacity; // Q_{ij}
    private double turning_proportion;
    private Set<ConflictRegion> conflictRegions;
    private double queueLength;

    // [
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    //  [number of vehicles that entered at time t, entrance time t]
    // ]
    // private LinkedList<double[]> queue;
    private double network_time;

    // private List<Vehicle> vehicleQueue;

//    public TurningMovement() {
//
//    }

    public TurningMovement(Link i, Link j)  {
        // need connectivity
//        System.out.println("link i: " + i);
//        System.out.println("link j: " + j);
        assert i.getDest() == j.getSource();
        this.i = i;
        this.j = j;
        // setCapacity();
        conflictRegions = new HashSet<>();
        queueLength = 0;
        // queue = new LinkedList<>();
        // vehicleQueue = new ArrayList<>();
        // turning_proportion = 0;
    }

    public TurningMovement(Link i, Link j, Network engine)  {
        this(i,j);
        this.engine = engine;
    }

    public Set<ConflictRegion> getConflictRegions() {
        return conflictRegions;
    }

//    public List<Vehicle> getVehicles() {
//        List<Vehicle> vehs = new ArrayList<>();
//        for (Vehicle v : i.getVehs()) {
//            Node d3_ = j.getDest();
//            Node d2_ = j.getSource();
//            Node d2 = v.getNextNode(1);
//            Node d3 = v.getNextNode(2);
//            if (d2_.equals(d2) && d3_.equals(d3)) {
//                vehs.add(v);
//            }
//        }
//        return vehs;
//    }

    public Link getIncomingLink(){
        return i;
    }

    public Link getOutgoingLink(){
        return j;
    }

    // NOTE: THIS ONLY (supposedly) WORKS FOR VEHICLES RN
    public double getWeight() {
        return i.getPressure(j, getTurningProportion());
    }


    public void moveVehicles() {

    }

    protected Set<Link> getLinks() {
        Set<Link> thislinks = new HashSet<>();
        thislinks.add(i);
        thislinks.add(j);
        return thislinks;
    }

    public boolean shareLinks(TurningMovement rhs) {
        Set<Link> intersection = new HashSet<>(getLinks());
        intersection.retainAll(rhs.getLinks());
        if (intersection.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public double startXshift(double epsilon) {
        double theta_i = Angle.bound(this.getIncomingLink().getAngle()) ;
        return Math.cos(theta_i + Math.PI) + epsilon * Math.cos(theta_i - Math.PI/2);
    }

    public double startYshift(double epsilon) {
        double theta_i = Angle.bound(this.getIncomingLink().getAngle()) ;
        return Math.sin(theta_i + Math.PI) + epsilon * Math.sin(theta_i - Math.PI/2);
    }

    public double endXshift(double epsilon) {
        double theta_j = Angle.bound(this.getOutgoingLink().getAngle()) ;
        return Math.cos(theta_j) + epsilon * Math.cos(theta_j - Math.PI/2);
    }

    public double endYshift(double epsilon) {
        double theta_j = Angle.bound(this.getOutgoingLink().getAngle()) ;
        return Math.sin(theta_j) + epsilon * Math.sin(theta_j - Math.PI/2);
    }

//    public boolean intersects(TurningMovement rhs) {
//                // no two flows can go to the same node (no merge)
//        if (this.getOutgoingLink().equals(rhs.getOutgoingLink())) {
//            return true;
//        }
//
//        // diverging is allowed
//        if (this.getIncomingLink().equals(rhs.getIncomingLink())) {
//            return false;
//        }
//
//        // complementary left and right turns
//        if (this.getOutgoingLink().getDest() == rhs.getIncomingLink().getSource() && rhs.getOutgoingLink().getDest() == this.getIncomingLink().getSource()) {
//            return false;
//        }
//
//        double TM1_incoming_angle = Angle.bound(Math.PI*1 + this.getIncomingLink().getAngle()) ;
//        double TM1_outgoing_angle = Angle.bound(Math.PI*0 + this.getOutgoingLink().getAngle()) ;
//        double TM2_incoming_angle = Angle.bound(Math.PI*1 + rhs.getIncomingLink().getAngle()) ;
//        double TM2_outgoing_angle = Angle.bound(Math.PI*0 + rhs.getOutgoingLink().getAngle()) ;
//
//        double epsilon = 0.1; // magnitude of shift to see if turns intersect
//        System.out.println("Positions");
//        System.out.println("\t" + this);
////        System.out.println("\t" + rhs);
//        System.out.println( "\t X: " + startXshift(epsilon) + ", Y: " + startYshift(epsilon));
////        System.out.println("\t" + this.getIncomingLink().getSource().getX() + ", " + this.getIncomingLink().getSource().getY());
////        System.out.println("\t" + this.getIncomingLink().getDest().getX() + ", " + this.getIncomingLink().getDest().getY());
//
//        System.out.println("\t" + (this.getIncomingLink().getDest().getX() + startXshift(epsilon)) + ", " + (this.getIncomingLink().getDest().getY() + startYshift(epsilon)) );
//        System.out.println("\t" + (this.getIncomingLink().getDest().getX() + endXshift(epsilon))  + ", " + (this.getIncomingLink().getDest().getY() + endYshift(epsilon)) );
//
//
////        System.out.println("\t" + this.getOutgoingLink().getDest().getX() + ", " + this.getOutgoingLink().getDest().getY());
////        System.out.println("\t" + rhs.getIncomingLink().getSource().getX() + ", " + rhs.getIncomingLink().getSource().getY());
////        System.out.println("\t" + rhs.getIncomingLink().getDest().getX() + ", " + rhs.getIncomingLink().getDest().getY());
////        System.out.println("\t" + rhs.getOutgoingLink().getDest().getX() + ", " + rhs.getOutgoingLink().getDest().getY());
//
//
//
//
//        // create the offset line segments
////        System.out.println("Angles");
////        System.out.println("\t" + this);
////        System.out.println("\t" + rhs);
////        System.out.println("\tTM1 incoming angle: " + Angle.bound(Math.PI*1 + this.getIncomingLink().getAngle()) );
////        System.out.println("\tTM1 outgoing angle: " + Angle.bound(Math.PI*0 + this.getOutgoingLink().getAngle()) );
////        System.out.println("\tTM2 incoming angle: " + Angle.bound(Math.PI*1 + rhs.getIncomingLink().getAngle()) );
////        System.out.println("\tTM2 outgoing angle: " + Angle.bound(Math.PI*0 + rhs.getOutgoingLink().getAngle()) );
//
//
//        return false;
//
//
//    }


//    // YASH's IDEAS
    public boolean intersects(TurningMovement rhs) {
        if (
                this.getIncomingLink() instanceof EntryLink ||
                this.getOutgoingLink() instanceof ExitLink ||
                rhs.getIncomingLink() instanceof EntryLink ||
                rhs.getOutgoingLink() instanceof ExitLink
        ) {
            return false;
        }

//        // if the links have physical intersect
//        if (
//                this.getIncomingLink().intersects(rhs.getIncomingLink()) ||
//                this.getIncomingLink().intersects(rhs.getOutgoingLink()) ||
//                this.getOutgoingLink().intersects(rhs.getIncomingLink()) ||
//                this.getOutgoingLink().intersects(rhs.getOutgoingLink())
//        ) {
//            return true;
//        }

        // TODO: this is a simplified model
        // if i = rhs.i , there is no conflict
        // if j = rhs.j , there is conflict
        // we ignore tip touching
        // TODO: verify this satisfies SF
        // TODO: (maybe) add a better intersection/conflict definition


//        // TODO: remove hardcode for SiouxFalls
//        if (
//                (this.toString().equals("Turn:19,20,18") && rhs.toString().equals("Turn:22,20,19")) ||
//                (rhs.toString().equals("Turn:19,20,18") && this.toString().equals("Turn:22,20,19")) ||
//                (this.toString().equals("Turn:21,20,22") && rhs.toString().equals("Turn:22,20,19")) ||
//                (rhs.toString().equals("Turn:21,20,22") && this.toString().equals("Turn:22,20,19")) ||
//
//                (this.toString().equals("Turn:19,20,21") && rhs.toString().equals("Turn:22,20,19")) ||
//                (rhs.toString().equals("Turn:19,20,21") && this.toString().equals("Turn:22,20,19")) ||
//
//                (this.toString().equals("Turn:19,20,21") && rhs.toString().equals("Turn:22,20,19")) ||
//                (rhs.toString().equals("Turn:19,20,21") && this.toString().equals("Turn:22,20,19"))
//        ) {
//            // System.out.println("OONGA BOONGA");
//            return true;
//        }

        // no two flows can go to the same node (no merge)
        if (this.getOutgoingLink().equals(rhs.getOutgoingLink())) {
            return true;
        }

        // diverging is allowed
        if (this.getIncomingLink().equals(rhs.getIncomingLink())) {
            return false;
        }

        // complementary left and right turns
//        System.out.println(this);
//        System.out.println(rhs);
//        System.out.println("TM1: " + this + ", TM2: " + rhs);
//
//        System.out.println("\t" + this.getOutgoingLink().getDest().getId());
//        System.out.println("\t" + rhs.getIncomingLink().getSource().getId());
//        System.out.println("\t" + rhs.getOutgoingLink().getDest().getId());
//        System.out.println("\t" + this.getIncomingLink().getSource().getId());

        if (this.getOutgoingLink().getDest() == rhs.getIncomingLink().getSource() && rhs.getOutgoingLink().getDest() == this.getIncomingLink().getSource()) {
            return false;
        }

//        System.out.println("TM1: " + this + ", TM2: " + rhs);

//        System.out.println("\tTM1 incoming angle: " + Angle.bound(Math.PI*1 + this.getIncomingLink().getAngle()) );
//        System.out.println("\tTM1 outgoing angle: " + Angle.bound(Math.PI*0 + this.getOutgoingLink().getAngle()) );
//        System.out.println("\tTM2 incoming angle: " + Angle.bound(Math.PI*1 + rhs.getIncomingLink().getAngle()) );
//        System.out.println("\tTM2 outgoing angle: " + Angle.bound(Math.PI*0 + rhs.getOutgoingLink().getAngle()) );


        double in_out_shift = 0.01; // the idea of the in_out shift is to remove the problem of overlapping links that might intersect
        // NOTE: THE IDEA ABOVE IS NOT RIGOROUS

        double TM1_incoming_angle = Angle.bound(Math.PI*1 + this.getIncomingLink().getAngle() + in_out_shift) ;
        double TM1_outgoing_angle = Angle.bound(Math.PI*0 + this.getOutgoingLink().getAngle() - in_out_shift) ;
        double TM2_incoming_angle = Angle.bound(Math.PI*1 + rhs.getIncomingLink().getAngle() + in_out_shift) ;
        double TM2_outgoing_angle = Angle.bound(Math.PI*0 + rhs.getOutgoingLink().getAngle() - in_out_shift) ;


//        if (
//                DoubleE.equals(TM2_incoming_angle, TM1_outgoing_angle) &&
//                DoubleE.geq(TM2_incoming_angle, TM1_outgoing_angle) &&
//                DoubleE.geq(TM1_incoming_angle, TM1_outgoing_angle)
//        ) {
//            return true;
//        }


        double TM1_min = Math.min(TM1_incoming_angle, TM1_outgoing_angle);
        double TM1_max = Math.max(TM1_incoming_angle, TM1_outgoing_angle);
        double TM2_min = Math.min(TM2_incoming_angle, TM2_outgoing_angle);
        double TM2_max = Math.max(TM2_incoming_angle, TM2_outgoing_angle);
//        System.out.println("\tTM1_min: " + TM1_min);
//        System.out.println("\tTM1_max: " + TM1_max);
//        System.out.println("\tTM2_min: " + TM2_min);
//        System.out.println("\tTM2_max: " + TM2_max);


//        if (
//                (DoubleE.geq(TM2_min, TM1_min) && DoubleE.leq(TM2_min,TM1_max) && DoubleE.geq(TM2_max,TM1_max)) ||
//                (DoubleE.geq(TM1_min,TM2_min) && DoubleE.leq(TM1_min, TM2_max) && DoubleE.geq(TM1_max, TM2_max))
//        ) {
//            return true;
//        }

//        if (
//                (DoubleE.inRange(TM2_min, TM1_min, TM1_max) && DoubleE.outRange(TM2_max, TM1_min, TM1_max)) ||
//                (DoubleE.inRange(TM2_max, TM1_min, TM1_max) && DoubleE.outRange(TM2_min, TM1_min, TM1_max))
//        )
//        {
//            return true;
//        }

//        // we have no conflict if one turning movements angles are contained within the range of the other's,
//        // or don't overlap
//        // (TM1) is contained in (TM2)
        if (
                DoubleE.geq(TM1_min, TM2_min) &&
                DoubleE.leq(TM1_min, TM2_max) &&
                DoubleE.geq(TM1_max, TM2_min) &&
                DoubleE.leq(TM1_max, TM2_max)
        ) {
            return false;
        }
//        // (TM1) is outside of (TM2)
        // 1) set (TM1)
        if (
                DoubleE.leq(TM1_min, TM2_min) &&
                DoubleE.leq(TM1_max, TM2_min)
        ) {
//            System.out.println("WE OUTSIDE");
//            System.out.println(this);
//            System.out.println(rhs);
            return false;
        }
        // (TM2) is contained in (TM1)
        if (
                DoubleE.geq(TM2_min, TM1_min) &&
                DoubleE.leq(TM2_min, TM1_max) &&
                DoubleE.geq(TM2_max, TM1_min) &&
                DoubleE.leq(TM2_max, TM1_max)
        ) {
//            System.out.println(rhs + " is contained in " + this);
            return false;
        }
//        // (TM2) is outside of (TM1)
        if (
                DoubleE.leq(TM2_min, TM1_min) &&
                DoubleE.leq(TM2_max, TM1_min))
        {
            return false;
        }

        return true;
    }

//    public double getQueueLength() {
//        double q_length = 0;
//        for (double[] veh_time_pair : queue) {
//            q_length += veh_time_pair[0];
//        }
//        return q_length;
//    }


    public void setQueueLength(double queueLength) {
        this.queueLength = queueLength;
    }

    public double getQueueLength() {
        return i.getSendingFlow() * getRandomTurningProportion();
    }

//    public void addToQueue(Vehicle v) {
//        vehicleQueue.add(v);
//
//    }

    /** Capacity as defined in ped-AIM by Rongsheng and Jeffery :
     * Section 3 : Network model **/
    public double getCapacity() {
        return Math.min(i.getCapacity(), j.getCapacity());
    }

//    // get wait time (how long the first entity to enter the queue has been in the queue)
//    public double getWaiting_time() throws EmptyQueueException {
//        try {
//            double[] front = queue.removeFirst();
//            return network_time - front[1];
//        } catch (NoSuchElementException e) {
//            // Case of empty queue, we want the turning movement to be off, so set waiting time to inf.
//            // this forces the controller to turn the turning movement off
//            throw new EmptyQueueException("empty queue, so there is no waiting time");
//        }
//    }

    public void updateTime(double newTime) {
        network_time = newTime;
    }

//    public String toString() {
//        return "Turn : [" + i + ", " + j +
//                "]" +
//                "" +
//                "";
//    }

    public String toString() {
        return "Turn:" + i.getSource().getId() +","+ i.getDest().getId() +","+ j.getDest().getId();
    }

//    public void updateQueue(double entrance_time, double num_veh) {
//        double new_val[] = {num_veh, entrance_time};
//        queue.add(new_val);
//    }

//    public void addVehicles(List<Vehicle> vs) {
//        vehicleQueue.addAll(vs);
//    }

    public double getRandomTurningProportion() {
        // TODO: ask Michael if we add some sophistication here
        double tp = readTurningProps(this.i.getId(), this.j.getId());
        return tp;
    }

    public double getTurningProportion() {
        // TODO: note the data setup of the file
        // make the file loading more modular
        // allow for time-dependent data loading

        double tp = readTurningProps(this.i.getId(), this.j.getId());
//        System.out.println(this);
//        System.out.println("\t: turningProportion: " + tp);

        return tp;
    }

    private double readTurningProps(int upstreamId, int downstreamId) {

//        File turn_props_file = new File("data/PQ3/turning_proportions.txt");
        File turn_props_file = engine.getTurnPropsFile();

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

                // at header: src	dest	turning proportions
                if (data.charAt(0) == 's') {
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
                    int downstreamId_ = Integer.parseInt(turn_prop_data[1]);
                    if (upstreamId == upstreamId_ && downstreamId == downstreamId_) {
                        double proportion = Double.parseDouble(turn_prop_data[2]);
                        myReader.close();
                        return proportion;
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find turning proportions file");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
