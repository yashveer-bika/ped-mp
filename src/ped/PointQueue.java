/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public PointQueue(int id, Node source, Node dest, double length, double ffspd, int capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);
//        vehs = new ArrayList<>();
//        exiting_vehs = new ArrayList<>();
//        entering_vehs = new ArrayList<>();

        // reset();
    }

//    public List<Vehicle> getVehs() {
//        return vehs;
//    }

    public void reset()
    {
        demand = new LinkedList<Double>();
        for(int i = 0; i < Math.ceil(getFFTime()/Params.dt)-1; i++)
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
//        n = n - exiting + demand.removeFirst();
//        demand.add(entering);
        n = n - exiting + entering;
        entering = 0;
        exiting = 0;
    }

    public double getSendingFlow()
    {
        return Math.min(n, getCapacity() * Params.dt/3600.0);
    }

    public double getReceivingFlow()
    {
        return getCapacity() * Params.dt/3600.0;
    }

    public void addFlow(double y)
    {
        entering += y;
//        n += y;
        // logEnteringFlow(y);
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


}
