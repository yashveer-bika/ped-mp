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




public class Node extends Location
{
    // static int cur_id = 0;
    protected int rowPosition;
    protected int colPosition;
    // protected Location location;
    protected int id;
    protected String type;

    private Set<Link> allLinks;
    private Link exitLink;
    private Link entryLink;

    private double curDemand = 0;
//    private List<Vehicle> vehicles;
    // public HashMap<String, Integer> signals; // Integer 1 = green, Integer 0 = red

    // private boolean thruNode;

    //for calculating shortest path
    private Node pi = null;
    private double cost = Double.POSITIVE_INFINITY;

    //for calculating shortest paths
    private ArrayList<Node> parents = new ArrayList<>();
    private boolean explored = false;

    public Node(double x, double y) {
        super(x,y);
        this.allLinks = new HashSet<>();
//        this.vehicles = new ArrayList<>();
        entryLink = new EntryLink();
        exitLink = null;

//        signals = new HashMap<>();
    }

    public Node(int id, String type, double x, double y) {
        this(id,x,y);
        this.type = type;

//        signals = new HashMap<>();
    }

    public Node(int id, double x, double y)
    {
        this(x,y);
        this.id = id;

//        signals = new HashMap<>();
    }

    public Node(int id, int row, int col, double x, double y)
    {
        this(id, x, y);
        this.rowPosition = row;
        this.colPosition = col;

    }

    public String getType() {
        return type;
    }

    public Link getOutgoingLink(Node rhs) {
//        System.out.println("n1 id: " + getId());
//        System.out.println("n2 id: " + rhs.getId());
        for (Link l : getOutgoingLinks()) {
            Node r = l.getDest();
            if (r.equals(rhs)) {
//                String n1id = String.valueOf(getId());
//                String n2id = String.valueOf(rhs.getId());
//                int linkid = Integer.parseInt(n1id + n2id);
//                System.out.println(n1id + n2id);
//                System.out.println(linkid);
                // assert linkid == rhs.getId();
                return l;
            }
        }
        return null;
    }

//    public List<Vehicle> getVehicles() {
//        return vehicles;
//    }

//    public void setVehicles(List<Vehicle> vehicles) {
//        this.vehicles = vehicles;
//    }

//    public void addVehicle(Vehicle v) {
//        vehicles.add(v);
//    }



    public Point asPoint() {
        return new Point(this.getX(), this.getY());
    }

    public Set<Node> getNeighbors() {
        Set<Node> neighs = new HashSet<>();
        for (Link l : this.getIncomingLinks()) {
            neighs.add( l.getSource() );
        }
        for (Link l : this.getOutgoingLinks()) {
            neighs.add( l.getDest() );
        }
        return neighs;
    }

//    public void setLocation(Location location) {
//        this.location = location;
//    }

    public Location getLocation() {
        return (Location) this;
    }

    public Set<Link> getOutgoingLinks()
    {
        Set<Link> outgoingLinks = new HashSet<>();
        for (Link l : allLinks) {
            if (this.equals(l.getSource()) && l.getDest() != null && !(l instanceof EntryLink) && !(l instanceof ExitLink)) {
                outgoingLinks.add(l);
            }
        }
        return outgoingLinks;
    }

    public Set<Link> getIncomingLinks()
    {
        Set<Link> incomingLinks = new HashSet<>();
        for (Link l : allLinks) {
            if (this.equals(l.getDest()) && l.getSource() != null && !(l instanceof EntryLink) && !(l instanceof ExitLink)) {
                incomingLinks.add(l);
            }
        }
        return incomingLinks;
    }

    public Set<Link> getAllLinks() {
        return this.allLinks;
    }

    public void removeLink(Link l) {
        allLinks.remove(l);
    }

    public void setEntryLink(Link l) {
        removeLink(getEntryLink());
        addLink(l);
        entryLink = l;
    }

    public void setExitLink(Link l) {
        removeLink(getExitLink());
        addLink(l);
        exitLink = l;
    }

    public Link getEntryLink() {
        return entryLink;
    }

    public Link getExitLink() {
        return exitLink;
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

    public void addDemand(double d) {
        this.curDemand += d;
    }

    public double getCurDemand() {
        return curDemand;
    }

    public Node getPi() {
        return pi;
    }

    public void setPi(Node pi) {
        this.pi = pi;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public ArrayList<Node> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Node> parents) {
        this.parents = parents;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

//    public void clearVehicles() {
//        vehicles = new ArrayList<>();
//    }

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


//    public boolean equals(Object o) {
//        Node rhs = (Node) o;
//        return location.equals(rhs.getLocation());
//    }

    public boolean equals(Object o) {
        Node rhs = (Node) o;
        if (rhs == null || this == null) {
            return false;
        }
//        return (rhs.getId() == this.getId()) || (super.equals(o));
        return (rhs.getId() == this.getId());
    }

     public String toString()
     {
         return "Node{id=" + id + "}";
     }

    public void addLink(Link link) {
        allLinks.add(link);
    }
    
    
    
    
    /* **********
    Exercise 3(d)
    ********** */




}
