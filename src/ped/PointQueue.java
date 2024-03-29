/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class propagates flow according to the point queue model (no spatial constraints).
 *
 * @author: Michael W. Levin
 */
public class PointQueue extends Link
{
    private LinkedList<Double> demand; // list of vehicles that entered, size is free flow tim/dt - 1

    private double n; // number of vehicles in point queue
//    private List<Vehicle> vehs;
    private double exiting; // number of vehicles exiting this time step
//    private List<Vehicle> exiting_vehs;
    private double entering; // number of vehicles entering this time step
//    private List<Vehicle> entering_vehs;

    public PointQueue(int id, String type, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, type, source, dest, length, ffspd, capacityPerLane, numLanes);
//        vehs = new ArrayList<>();
//        exiting_vehs = new ArrayList<>();
//        entering_vehs = new ArrayList<>();

        // reset();

        demand = new LinkedList<Double>();
        demand.add(0.0);
        for(int i = 0; i < Math.ceil(getFFTime()/Params.dt)-2; i++)
        {
            demand.add(0.0);
        }
    }

    public PointQueue(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, "point-queue", source, dest, length, ffspd, capacityPerLane, numLanes);
//        vehs = new ArrayList<>();
//        exiting_vehs = new ArrayList<>();
//        entering_vehs = new ArrayList<>();

        // reset();
//
//        System.out.println("link: " + this);
//        System.out.println("\tlink free flow time: " + getFFTime());
//        System.out.println("\ttime step (dt): " + Params.dt);

        demand = new LinkedList<>();
        demand.add(0.0);
        for(int i = 0; i < Math.ceil(getFFTime()/Params.dt)-2; i++)
        {
            demand.add(0.0);
        }

    }

//    public List<Vehicle> getVehs() {
//        return vehs;
//    }

    public void reset()
    {
        demand = new LinkedList<Double>();
        demand.add(0.0);
        for(int i = 0; i < Math.ceil(getFFTime()/Params.dt)-2; i++)
        {
            demand.add(0.0);
        }

        n = 0;
        exiting = 0;
        entering = 0;

    }

    public double getOccupancy()
    {
        double output = 0.0;

        for(double x : demand)
        {
            output += x;
        }

        output += n;
        return output;
    }


    public void step()
    {
        // nothing to do here
    }

    public void update()
    {
//        System.out.println("link: " + this);
//        System.out.println("demand: " + demand);
        n = n - exiting + demand.removeFirst();
        demand.add(entering);
//        n = n - exiting + entering;
        entering = 0;
        exiting = 0;
    }

    public double getSendingFlow()
    {
        return Math.min(n, getCapacity() );
    }

    public double getReceivingFlow()
    {
        return getCapacity();
    }

    public void addFlow(double y)
    {
        entering += y;
//        n += y;
         logEnteringFlow(y);
    }

    public void removeFlow(double y)
    {
        exiting += y;
//        n -= y;
    }

//    public boolean isEntry() {
//        return false;
//    }

//    public void addVehicle(Vehicle v) {
////        System.out.println("Previous size: " + vehs.size());
////        System.out.println("v.getCurrentNode(): " + v.getCurrentNode().getId());
////        System.out.println("this.getSource(): " + this.getSource().getId());
//
//        assert v.getCurrentNode() == this.getSource();
////        v.moveVehicle();
//        int v_size = vehs.size();
//        vehs.add(v);
//        assert v_size + 1 == this.vehs.size();
//        assert vehs.contains(v);
////        System.out.println("Current size: " + vehs.size());
//
//    }

//    public void removeVehicle(Vehicle v) {
//        vehs.remove(v);
//    }

    public double getN() {
        return n;
    }

    public double getQueueLength(double turningProportion) {
        return n * turningProportion;
    }

    @Override
    public double getPressure(Link downstreamLink, double turningProportion) {
        //        System.out.println("\t Getting weight");
        double weight = getQueueLength(turningProportion);
//        System.out.println("dest: " + getOutgoingLink().getDest());
        if (!(downstreamLink.getDest() instanceof VehIntersection)) {
            return weight;
        }
        VehIntersection neigh = (VehIntersection) downstreamLink.getDest();
        Set<TurningMovement> nextTurns = neigh.getVehicleTurns();
//        System.out.println("\t\t downstream turns: " + nextTurns);
        for (TurningMovement nextTurn : nextTurns) {
            if (nextTurn.getIncomingLink().equals(downstreamLink)) {
                TurningMovement downstream_turn = nextTurn;
                weight -= downstream_turn.getTurningProportion() * downstream_turn.getQueueLength() ;
            }
//            System.out.println("\tweight: " + weight);
//            System.out.println("\tdownstream_turn.getTurningProportion(): " + downstream_turn.getTurningProportion());
//            System.out.println("\tdownstream_turn.getQueueLength(): " + downstream_turn.getQueueLength());
        }
//        System.out.println("\tweight: " + weight);
        return weight;
    }
}
