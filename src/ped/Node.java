/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import Geometry.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author micha
 */




public class Node 
{
    // static int cur_id = 0;
    protected int rowPosition;
    protected int colPosition;
    protected Location location;
    protected int id;

    private Set<Link> incomingLinks;
    private Set<Link> outgoingLinks;
    private Set<Link> allLinks;
    // public HashMap<String, Integer> signals; // Integer 1 = green, Integer 0 = red


    // private boolean thruNode;

    public Node() {
        this.outgoingLinks = new HashSet<>();
        this.incomingLinks = new HashSet<>();
        this.allLinks = new HashSet<>();
//        signals = new HashMap<>();
    }

    public Node(int id)
    {
        this.id = id;
        this.outgoingLinks = new HashSet<>();
        this.incomingLinks = new HashSet<>();
        this.allLinks = new HashSet<>();

//        signals = new HashMap<>();
    }

    public Node(int id, int row, int col)
    {
        this.rowPosition = row;
        this.colPosition = col;
        this.id = id;
        this.outgoingLinks = new HashSet<>();
        this.incomingLinks = new HashSet<>();
        this.allLinks = new HashSet<>();

    }

    public Point asPoint() {
        return new Point(location.getX(), location.getY());
    }

    public Set<Node> getNeighbors() {
        Set<Node> neighs = new HashSet<>();
        for (Link l : this.getIncomingLinks()) {
            neighs.add( l.getStart() );
        }
        for (Link l : this.getOutgoingLinks()) {
            neighs.add( l.getDestination() );
        }
        return neighs;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Link> getOutgoingLinks()
    {
        return this.outgoingLinks;
    }

    public Set<Link> getIncomingLinks()
    {
        return this.incomingLinks;
    }

    public Set<Link> getAllLinks() {
        return this.allLinks;
    }

    public void addOutgoingLink(Link l)
    {
        this.outgoingLinks.add(l);
        this.allLinks.add(l);
    }

    public void addIncomingLink(Link l)
    {
        this.incomingLinks.add(l);
        this.allLinks.add(l);
    }

    public void addEntryLink(Link l) {
        this.allLinks.add(l);
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public void setColPosition(int colPosition) {
        this.colPosition = colPosition;
    }

    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    public int getColPosition() {
        return colPosition;
    }

    public int getId()
    {
        return id;
    }



    /*
    public void setIncomingLinks(Set<Link> incoming) {
        this.incoming = incoming;
    }

    public Set<Link> getIncomingLinks() {
        return incoming;
    }

    public void setOutgoingLinks(Set<Link> outgoingLinks) {
        this.outgoing = outgoing;
    }

    public Set<Link> getOutgoingLinks() {
        return outgoing;
    }

     */




//    public HashMap<String, Integer> getSignals() {
//        return signals;
//    }
//
//    public void setSignal(Link in, Link out, int new_phase) {
//        String key = in + "::" + out;
//        signals.put(key, new_phase);
//    }
//
//    public void setSignal(String key, int new_phase) {
//        signals.put(key, new_phase);
//    }




    
    
    
    
    
    // public boolean isThruNode()
    // {
    //     return true;
    // }
    

    
    /* **********
    Exercise 3(c)
    ********** */
     public String toString()
     {
         return ""+this.getId();
     }
    
    
    
    
    /* **********
    Exercise 3(d)
    ********** */




}
