/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;


import Geometry.Geometry;
import Geometry.LineSegment;
import Geometry.Point;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a Link in the network.
 * This is an abstract class: key methods are not implemented, and must be implemented in subclasses. 
 * These depend on the specific flow model.
 *
 * @author Michael Levin
 */
public abstract class Link implements Comparable<Link>
{
//    private List<Vehicle> vehs; // TODO: see if we need this

    // id used to reference the Link
    private int id;

    // stores the upstream and downstream nodes
    private Node source, dest;

    //
    private String type;

    // capacity per lane in veh/hr
    private double capacityPerLane;

    // number of lanes
    private int numLanes;

    // Link length in miles
    private double length;

    // free flow speed in mi/hr
    private double ffspd;

    // private double free_flow_time;

    // travel time information
    private double avgTT;
    private double totalFlow;

    // during each DNL, update total entering flow and total time spent occupying the Link.
    private double total_entering;
    private double total_time_occ;

    // YASHVEER's ADDITIONS
    // suppose we plotted the source and dest. angle is the angle from source to dest
    private double angle;
    private boolean sidewalk = false;
    private boolean crosswalk = false;

    // TODO: make simTime reachable here


    /**
     * Constructs a new {@Link Link} with the given parameters.
     * Generally {@Link Link}s will be constructed in {@Link dnl.ReadNetwork}.
     *
     * Jam density is global and found in {@Link Params} TODO: add my own Params file type setup
     * Backwards wave speed may or may not be calculated based on these parameters - depends on the flow model.
     * @param id id of this {@Link Link}
     * @param source start {@Link Node} of this {@Link Link}
     * @param dest end {@Link Node} of this {@Link Link}
     * @param length length in ft
     * @param ffspd free flow speed in mi/hr
     * @param capacityPerLane capacity (per lane) in veh/timestep (veh/Params.dt)
     * @param numLanes number of lanes
     */
    public Link(int id, String type, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
    {
        // store Link parameters
        this.id = id;
        this.type = type;
        this.source = source;
        this.dest = dest;
        this.capacityPerLane = capacityPerLane;
        this.ffspd = ffspd;
        this.length = length;
        this.numLanes = numLanes;
        // this.free_flow_time = ffspd / length;
//        this.vehs = new ArrayList<>();

        // update incoming/outgoing sets of Links in the Node class
        if(source != null)
        {
            source.addLink(this);
//            source.addOutgoingLink(this);
        }
        if(dest != null)
        {
            dest.addLink(this);
//            dest.addIncomingLink(this);
        }

        total_time_occ = 0.0;
        total_entering = 0.0;
    }

    /**
     * Resets this {@Link Link} to start a new dynamic network loading
     */
    public abstract void reset();

    /**
     * @return the free flow speed in mi/hr
     */
    public double getFFSpeed()
    {
        return ffspd;
    }

    /**
     * @return the free flow travel time in s.
     */
    public double getFFTime()
    {
        return getLength() / 5280.0 / getFFSpeed() * 3600.0;
    }

    /**
     * @return the travel time when entering at the given time (in s).
     */
    public double getAvgTT()
    {
        return Math.max(avgTT, getFFTime());
    }

    /**
     * @return the total time spent traveling on this Link (in s)
     */
    public double getTotalTT()
    {
        return getAvgTT() * totalFlow;
    }


//    /**
//     * @return the average grade (change in elevation) in percent vertical change in ft per horizontal distance
//     */
//    public double getAvgGrade()
//    {
//        return (dest.getElevation() - source.getElevation()) / (getLength());
//    }

    /**
     * @return the length in ft
     */
    public double getLength()
    {
//        System.out.println("Link: " + this.id + " ---> " + length);
        return length;
    }

    /**
     * @return the capacity per lane in veh/timestep
     */
    public double getCapacityPerLane()
    {
        return capacityPerLane;
    }

    /**
     * @return the total capacity in veh/timestep
     */
    public double getCapacity()
    {
        return capacityPerLane * numLanes;
    }

    /**
     * @return the number of lanes
     */
    public double getNumLanes()
    {
        return numLanes;
    }

    // TODO: verify this is what we want
    public double getDsLanes() {
        return getNumLanes();
    }


    /**
     * @return the upstream {@Link Node} for this {@Link Link}
     */
    public Node getSource()
    {
        return source;
    }

    /**
     * @return the downstream {@Link Node} for this {@Link Link}
     */
    public Node getDest()
    {
        return dest;
    }


    /**
     * @return the id for this {@Link Link}
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return the number of vehicles currently on this {@Link Link}.
     */
    public abstract double getOccupancy();

    /**
     * This is called every time step. 
     * For the {@Link Link} class, {@Link #step()} should propagate flow along the Link.
     */
    public abstract void step();

    /**
     * This is called every time step, after {@Link #step()} has been called for all {@Link Node}s and {@Link Link}s.
     * It can be used to finish any updating work that could not occur during {@Link #step()}.
     */
    public abstract void update();

    /**
     * Adds the given flow (in veh, for a single time step) to the upstream end of the Link.
     * This method is called when vehicles enter the Link!
     * This is usually called by the {@Link Node#step()} method.
     * Subclasses of Link need to call {@Link #logEnteringFlow(double)} to update the average travel times.
     */
    public abstract void addFlow(double y);


    public abstract double getPressure(Link downstreamLink, double turningProportion);

    /**
     * This is used to track the total number of vehicles entering this Link.
     * The number of vehicles is used to calculate the average travel time.
     * This method is usually called by {@Link #addFlow(double)}.
     * Subclasses of {@Link Link} should call this method as part of the {@Link #addFlow(double)} method.
     */
    public void logEnteringFlow(double y)
    {
        total_entering += y;
    }


    /**
     * This method is used to calculate the average travel time.
     * It should be called once per time step by the {@Link dnl.Network} class.
     */
    public void logOccupancyTime()
    {
        total_time_occ += Params.dt * getOccupancy();
    }

    public double getAvgDelay() {
        return getAvgTT() - getFFTime();
    }

    /**
     * This method is used to calculate the total travel time at the end of the dynamic network loading.
     * The result can be accessed by {@Link #getAvgTT()}. 
     * This method is called in {@Link dnl.Network#simulate()}.
     */
    public void calculateTravelTime()
    {
        if(total_entering > 0)
        {
            avgTT = total_time_occ / total_entering;
        }
        else
        {
            avgTT = getFFTime();
        }

        totalFlow = total_entering;

        // now reset total_time_occ and total_entering for the next dynamic network loading.
        total_time_occ = 0.0;
        total_entering = 0.0;
    }


    /**
     * Removes the given flow (in veh, for a single time step) from the downstream end of the Link.
     * This method is called when vehicles exit the Link! 
     * This is usually called by the {@Link Node#step()} method.
     * @param y the flow to be removed
     */
    public abstract void removeFlow(double y);

    /**
     * @return the maximum flow that could exit the Link in the next time step (in veh)
     */
    public abstract double getSendingFlow();


    /**
     * @return the maximum flow that could enter the Link in the next time step (in veh)
     */
    public abstract double getReceivingFlow();


    /**
     * Used to sort {@Link Link} by id.
     */
    public int compareTo(Link rhs)
    {
        return id - rhs.id;
    }


    /**
     * Used for hashing. You can ignore it.
     */
    public int hashCode()
    {
        return id;
    }

    /**
     * @return the id of this {@Link Link}
     */
    public String toString()
    {
//        return ""+id;
        String src_id;
        try {
            src_id = source.getId() + "";
        } catch (NullPointerException e) {
            src_id = "null";
        }

        String dest_id;
        try {
            dest_id = dest.getId() + "";
        } catch (NullPointerException e) {
            dest_id = "null";
        }

        return "(" + src_id + ", " + dest_id + ")";
    }


    /** YASHVEER's ADDITIONS ***/
    // TODO: javadoc
//    public abstract boolean isEntry();

    // TODO: make my new methods follow good design practice
    public double getAngle() {
        return source.angleTo(dest);
    }

    public double getDirection() {
        return getAngle();
    }

    public boolean isSidewalk() {
        return sidewalk;
    }

//    public List<Vehicle> getVehs() {
//        return vehs;
//    }

//    public void addVehicle(Vehicle v) {}
//
//    public void removeVehicle(Vehicle v) {}

    public double getPositionalLength() {
        double x_diff = source.getX() - dest.getX();
        double y_diff = source.getY() - dest.getY();
        return Math.sqrt(x_diff * x_diff + y_diff * y_diff);
    }

    public boolean intersects(Link rhs) {
//        LineSegment l1 = new LineSegment(
//                new Point(getSource().getX(), getSource().getY()),
//                new Point(getDest().getX(), getDest().getY())
//        );
//
//        LineSegment l2 = new LineSegment(
//                new Point(rhs.getSource().getX(), rhs.getSource().getY()),
//                new Point(rhs.getDest().getX(), rhs.getDest().getY())
//        );

        Point2D.Double p1 = new Point2D.Double(getSource().getX(), getSource().getY());
        Point2D.Double p2 = new Point2D.Double(getDest().getX(), getDest().getY());
        Point2D.Double p3 = new Point2D.Double(rhs.getSource().getX(), rhs.getSource().getY());
        Point2D.Double p4 = new Point2D.Double(rhs.getDest().getX(), rhs.getDest().getY());

        // this intersect includes tips
        double x1 = this.getSource().getX();
        double y1 = this.getSource().getY();

        double x2 = this.getDest().getX();
        double y2 = this.getDest().getY();

        double x3 = rhs.getSource().getX();
        double y3 = rhs.getSource().getY();

        double x4 = rhs.getDest().getX();
        double y4 = rhs.getDest().getY();

        return Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4) && !shareAnyPoint(p1, p2, p3, p4);
//        return Geometry.doLinesIntersect()
    }

    // https://stackoverflow.com/questions/23192573/java-proper-line-intersection-check
    // shareAnyPoint(StartPointA, EndPointA, StartPointB, EndPointB) function that checks if start/end points from either lines lies on the other line.
    public static boolean shareAnyPoint(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D) {
        if (isPointOnTheLine(A, B, C)) return true;
        else if (isPointOnTheLine(A, B, D)) return true;
        else if (isPointOnTheLine(C, D, A)) return true;
        else if (isPointOnTheLine(C, D, B)) return true;
        else return false;
    }

    public static boolean isPointOnTheLine(Point2D.Double A, Point2D.Double B, Point2D.Double P) {
        double m = (B.y - A.y) / (B.x - A.x);

        //handle special case where the line is vertical
        if (Double.isInfinite(m)) {
            if(A.x == P.x) return true;
            else return false;
        }

        if ((P.y - A.y) == m * (P.x - A.x)) return true;
        else return false;
    }


    public void setSidewalk(boolean sidewalk) {
        this.sidewalk = sidewalk;
    }

    public void setCrosswalk(boolean crosswalk) {
        this.crosswalk = crosswalk;
    }

    public boolean isCrosswalk() {
        return crosswalk;
    }

    public String getType() {
        return type;
    }
}