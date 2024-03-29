package ped;


import ilog.concert.IloLinearNumExpr;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class represents a single conflict region in the conflict region model of reservation-based intersection control.
 * @author Michael
 */
public class ConflictRegion
{

    public double capacity;

    Set<TurningMovement> tms;


    // id for hashing
    private int id;
    private static int id_count = 0;


    /**
     * Constructs this {@link ConflictRegion} with the specified capacity
     */
    public ConflictRegion()
    {
        this.capacity = 0;

        this.id = id_count++;

        tms = new HashSet<>();

    }





//    /**
//     *
//     * @return the remaining flow for this time step
//     */
//    public double getRemainingTimestepFlow()
//    {
//        return R;
//    }

    /**
     * A {@link String} specifying the id
     * @return a {@link String} containing the id
     */
//    public String toString()
//    {
//        return "CR: "+ getTms();
//    }

    public String toString()
    {
        return ""+ getId();
    }

    public int hashCode()
    {
        return id;
    }

    /**
     * Returns the id of this {@link ConflictRegion}. Ids are uniquely generated upon instantiation.
     *
     * @return an int specifying the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Resets this {@link ConflictRegion} for the next time step
     * This updates the capacity, and, if the {@link ConflictRegion} was blocked the last time step, saves some of the previous time step's capacity.
     * This prevents situations in which vehicles are blocked by lack of {@link ConflictRegion} capacity and lack of Link receiving flow on alternative time steps.
     * The blocked check is reset every time step and activated when a vehicle cannot move through this ConflictRegion.
     */
//    public void newTimestep()
//    {
//
//        if(blocked && R>=0)
//        {
//            R += 1;
//            //R += capacity * Network.dt / 3600.0;
//        }
//        else
//        {
//            // this is leftover from when conflict regions stored capacity
//            R -= Math.max(0, (int)R);
//            //R += capacity * Network.dt / 3600.0;
//            R += 1;
//        }
//
//        blocked = false;
//    }

//    /**
//     * Resets this {@link ConflictRegion} to restart the simulation
//     */
//    public void reset()
//    {
//        R = Params.dt;
//        //R = capacity * Network.dt / 3600.0;
//        blocked = false;
//    }

//    /**
//     *
//     * @return the remaining time step flow
//     */
//    public double getRemainingFlow()
//    {
//        return R;
//    }

//    /**
//     * This checks whether there is sufficient remaining flow to move a vehicle which requires the specified flow. If not, this {@link ConflictRegion} is marked as blocked.
//     *
//     * @param inc the incoming {@link Link}
//     * @param out the outgoing {@link Link}
//     * @param flow equivalent flow of the vehicle. This may be reduced for autonomous vehicles due to lower following headways.
//     * @return whether a vehicle with the specified equivalent flow can move from the incoming link to the outgoing link
//     */
//    public boolean canMove(Link inc, Link out, double flow)
//    {
//        boolean output = R >= flow*adjustFlow(inc, out);
//        blocked = blocked || !output;
//        return output;
//    }

//    /**
//     * Moves a vehicle requiring the specified flow
//     * @param inc the incoming {@link Link}. This is used for adjusting the flow required.
//     * @param out the outgoing {@link Link}
//     * @param flow equivalent flow of the vehicle. This may be reduced for autonomous vehicles due to lower following headways.
//     */
//    public void update(Link inc, Link out, double flow)
//    {
//        R -= flow*adjustFlow(inc, out);
//    }

    /**
     * Adjusts the required flow for the specified turning movement
     *
     * @param inc the incoming {@link Link}
     * @param out the outgoing {@link Link}
     * @return adjust the required flow for vehicles turning from the specified incoming and outgoing links.
     */
    public double adjustFlow(Link inc, Link out)
    {
        return 1 / (inc.getCapacityPerLane() * inc.getDsLanes() * Params.dt/3600.0);
    }

    /**
     * Returns the capacity of this {@link ConflictRegion}
     * @return the capacity of this {@link ConflictRegion}
     */
    public double getCapacity()
    {
        double cap = Double.NEGATIVE_INFINITY;
        if (tms.size() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        for (TurningMovement tm : tms) {
            double c = tm.getCapacity();
            if (c > cap) {
                cap = c;
            }
        }
        return cap;
    }

//    public void setCapacity(double capacity) {
//        this.capacity = capacity;
//    }



    /**
     * Marked this {@link ConflictRegion} as blocked for the current time step
     */

    public void addTM(TurningMovement tm) {
        tms.add(tm);
    }

    public Set<TurningMovement> getTms() {
        return tms;
    }

    /**
     * Sets the maximum flow scale to the specified {@link Link}
     * @param i the {@link Link} with the maximum flow scaling
     */
    public void setMaxScale(Link i){}
}