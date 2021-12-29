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
 */




public class Node 
{
    
    
    /* **********
    Exercise 3(b)
    ********** */
    static int cur_id = 0;
    protected int rowPosition;
    protected int colPosition;
    protected int id;

    private ArrayList<Link> incoming;
    private ArrayList<Link> outgoing;
    public HashMap<String, Integer> signals; // Integer 1 = green, Integer 0 = red


    // private boolean thruNode;

    public Node() {

    }

    public Node(int id)
    {
        this.id = id;
        outgoing = new ArrayList<>();
        incoming = new ArrayList<>();
        signals = new HashMap<>();
    }

    public Node(int row, int col, int id)
    {
        this.rowPosition = row;
        this.colPosition = col;
        this.id = id;
    }

    public HashMap<String, Integer> getSignals() {
        return signals;
    }

    public void setSignal(Link in, Link out, int new_phase) {
        String key = in + "::" + out;
        signals.put(key, new_phase);
    }

    public void setSignal(String key, int new_phase) {
        signals.put(key, new_phase);
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
