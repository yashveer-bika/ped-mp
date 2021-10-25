/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<Link> incoming;
    private ArrayList<Link> outgoing;
    protected double cost;
    protected Node predecessor;
    public HashMap<String, Integer> signals; // Integer 1 = green, Integer 0 = red


    // private boolean thruNode;
    
    
    public Node(int id)
    {
        this.id = id;
        outgoing = new ArrayList<>();
        incoming = new ArrayList<>();
        signals = new HashMap<>();
    }

    public HashMap<String, Integer> getSignals() {
        return signals;
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
    

    public ArrayList<Link> getOutgoing()
    {
        return outgoing;
    }

    public ArrayList<Link> getIncoming()
    {
        return incoming;
    }

    public void addOutgoingLink(Link l)
    {
        outgoing.add(l);
    }

    public void addIncomingLink(Link l)
    {
        incoming.add(l);
    }




}
