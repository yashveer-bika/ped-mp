package ped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ml26893
 */
public class ConflictPoint extends Location implements Comparable<ConflictPoint>
{
    private int id;
    private static int curr_id = 1;


    public double label;
    private String name;

    public ConflictPoint(String name, Location loc)
    {
        super(loc);
        this.name = name;
        this.id = curr_id++;
    }

    public int compareTo(ConflictPoint rhs)
    {
        if(label < rhs.label)
        {
            return -1;
        }
        else if(label > rhs.label)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int getId()
    {
        return id;
    }

    public String toString()
    {
        return name;
    }

    public boolean equals(Object o)
    {
        ConflictPoint rhs = (ConflictPoint)o;
        return id == rhs.id;
    }

    public int hashCode()
    {
        return id;
    }
}