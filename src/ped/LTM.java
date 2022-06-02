/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.LinkedList;

/**
 * This class propagates flow according to the link transmission model.
 *
 * @author
 */
public class LTM extends Link
{
    private LinkedList<Double> N_up;
    private LinkedList<Double> N_down;

    private double yin, yout;

    public LTM(int id, Node source, Node dest, double length, double ffspd, int capacityPerLane, int numLanes)
    {
        super(id, "LTM", source, dest, length, ffspd, capacityPerLane, numLanes);

        reset();
    }

//    public boolean isEntry() {
//        return false;
//    }

    public void reset()
    {
        N_up = new LinkedList<>();
        N_down = new LinkedList<>();

        for(int i = 0; i < Math.round(getLength() / ( getFFSpeed() / 3600.0) / Params.dt); i++)
        {
            N_up.add(0.0);
        }

        double w = getCapacityPerLane() / Params.JAM_DENSITY;

        for(int i = 0; i < Math.round(getLength() / (w / 3600.0) / Params.dt); i++)
        {
            N_down.add(0.0);
        }

        yin = 0;
        yout = 0;
    }

    public double getOccupancy()
    {
        return N_up.getLast() - N_down.getLast();
    }

    public void step()
    {
        // nothing to do here
    }

    public void update()
    {
        double N_last = N_up.getLast();
        N_up.add(N_last + yin);

        N_last = N_down.getLast();
        N_down.add(N_last + yout);

        yin = 0;
        yout = 0;

        N_up.removeFirst();
        N_down.removeFirst();
    }

    public double getSendingFlow()
    {
        return Math.min(getCapacity() * Params.dt / 3600.0, N_up.getFirst() - N_down.getLast());
    }

    public double getReceivingFlow()
    {
        return Math.min(getCapacity() * Params.dt/3600.0, Params.JAM_DENSITY * this.getNumLanes() * getLength() + N_down.getLast() - N_up.getFirst());
    }

    public void addFlow(double y)
    {
        yin += y;
        logEnteringFlow(y);
    }

    public void removeFlow(double y)
    {
        yout += y;
    }
}
