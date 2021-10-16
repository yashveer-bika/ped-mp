/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author micha
 * TODO: consider adding a "addIncoming", "getIncoming" functionality
 */




public class Node 
{
    
    
    /* **********
    Exercise 3(b)
    ********** */
    
    private int id;
    
    // private boolean thruNode;
    
    
    public Node(int id)
    {
        this.id = id;
        outgoing = new ArrayList<>();
    }
    
    public int getId()
    {
        return id;
    }
    
    
    
    
    
    // public boolean isThruNode()
    // {
    //     return true;
    // }
    
    
    public int hashCode()
    {
        return getId();
    }
    
    /* **********
    Exercise 3(c)
    ********** */
    public String toString()
    {
        return ""+getId();
    }
    
    
    
    
    /* **********
    Exercise 3(d)
    ********** */
    
    private ArrayList<Link> outgoing;
    
    public ArrayList<Link> getOutgoing()
    {
        return outgoing;
    }

    // TODO: get incoming links

    public void addOutgoingLink(Link l)
    {
        outgoing.add(l);
    }
    
    
    
    
    protected double cost;
    protected Node predecessor;
}
