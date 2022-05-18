package ped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import util.Angle;

/**
 *
 * @author ml26893, bikax003
 * use normal-coordinate
 * up on the y-axis is positive
 * right on the x-axis is positive
 */
public class Location
{

    private double x, y;

    public Location(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Location(Location rhs)
    {
        this(rhs.x, rhs.y);
    }

    public String toString()
    {
        return "("+x+", "+y+")";
    }
    public int hashCode()
    {
        return (int)(1000*x+10*y);
    }

    public double euclideanDist(Location rhs)
    {
        double xdiff = x - rhs.x;
        double ydiff = y - rhs.y;

        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public boolean equals(Object o)
    {
        Location rhs = (Location)o;
        return x == rhs.x && y == rhs.y;
    }

//    public static double angle(Location src, Location dest) {
//
//        double dy = dest.getY() - src.getY();
//        double dx = dest.getX() - src.getX();
//
//        double ang = Math.atan( dy / dx );
//
//        // figure out which quadrant I'm pointing to, since that effects the value
//        // 1 : dx (+) dy (+) (nothing)
//        // 2 : dx (-) dy (+) (+ pi)
//        // 3 : dx (-) dy (-) (+ pi)
//        // 4 : dx (+) dy (-) (+ 2pi)
//
//        if (dx >= 0 && dy >= 0) { // quadrant 1
//
//        } else if (dx < 0 && dy >= 0) { // quadrant 2
//            ang = ang + Math.PI;
//        } else if (dx < 0 && dy < 0) { // quadrant 3
//            ang = ang + Math.PI;
//        } else { // quadrant 4
//            ang = ang + 2 * Math.PI;
//        }
//        return ang;
//    }

    /**
     * Calculates the angle between this {@link Location} and another
     * @param rhs the other {@link Location}
     * @return the angle to {@link Location} {@code rhs} in radians
     */
    public double angleTo(Location rhs)
    {
        double output = Math.atan2(rhs.getY() - getY(), rhs.getX() - getX());

        if(output < 0)
        {
            output += 2*Math.PI;
        }

        return output;
    }

    // use trigonometry to calculate the coordinates of the new location
    public Location spawnNewLocation(double dist, double ang) {
        // move angles to range of [0,2*pi)
        ang = Angle.bound(ang);
        double dx = dist * Math.cos(ang);
        double dy = dist * Math.sin(ang);
        Location loc = new Location(this.getX() + dx, this.getY() + dy);
        return loc;
    }


    public Location spawnPedIntLoc(double ang1, double ang2, double dist) {
        double bisect_angle = util.Angle.bisectAngle(ang1, ang2);
        double dist_from_point = util.Angle.equidistantLength(dist, ang1, ang2);
        Location locTmp = this.spawnNewLocation(dist_from_point, bisect_angle);
        return locTmp;
    }
}