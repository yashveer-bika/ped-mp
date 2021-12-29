package ped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ml26893
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
}