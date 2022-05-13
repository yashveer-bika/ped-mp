/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.LinkedList;

/**
 * This class propagates flow according to the point queue model (no spatial constraints).
 *
 * @author: Michael W. Levin
 */
public class PointQueue extends Link
{
    private LinkedList<Double> demand; // list of vehicles that entered, size is free flow tim/dt - 1

    private double n; // number of vehicles in point queue
    private double exiting; // number of vehicles exiting this time step
    private double entering; // number of vehicles entering this time step

    public PointQueue(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        super(id, source, dest, length, ffspd, capacityPerLane, numLanes);

        reset();
    }

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
        n = n - exiting + demand.removeFirst();
        demand.add(entering);
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
        // logEnteringFlow(y);
    }

    public void removeFlow(double y)
    {
        exiting += y;
    }

    public boolean isEntry() {
        return false;
    }
}
