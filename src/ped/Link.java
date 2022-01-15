/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author micha
 */
public class Link 
{
    // Says whether or not the link is part of the current phase
    // i.e, is traffic allowed to move out of here?
    private boolean activated;

    private boolean entry = false;
    private double cur_queue_length;
    
    // parameters for travel time calculation. max_flow is the free flow time, C is the capacity
    private double C;
    
    // the start and end nodes of this link. Links are directed.
    private Node start, destination;

    private String direction, type;
    // POSSIBLE values for direction:
    // "N", "S", "E", "W", "NS", "EW"


    public Link() {

    }

    // construct this Link with the given parameters
    public Link(Node start, Node destination, double C, boolean entry)
    {
        this.start = start;
        this.destination = destination;
        this.C = C;
        this.entry = entry;
        
        if(start != null)
        {
            start.addOutgoingLink(this);
        }
        if(destination != null)
        {
            destination.addIncomingLink(this);
        }
    }

    // construct this Link with the given parameters
    public Link(Node start, Node destination, double C, String direction)
    {
        this.start = start;
        this.destination = destination;
        this.C = C;
        this.direction = direction;
        // this.type = type;


        if(start != null)
        {
            start.addOutgoingLink(this);
        }
        if(destination != null)
        {
            destination.addIncomingLink(this);
        }
    }

    public Link(Node start, Node destination, double C)
    {
        this.start = start;
        this.destination = destination;
        this.C = C;

        if(start != null)
        {
            start.addOutgoingLink(this);
        }
        if(destination != null)
        {
            destination.addIncomingLink(this);
        }
    }

    public String getDirection() {
        return direction;
    }

    public Node getDestinationNode() {
        return destination;
    }



    public HashMap<String, Integer> getSignals() {
        HashMap<String, Integer> all_signals = destination.getSignals();
        HashMap<String, Integer> desired_signals = new HashMap<String, Integer>();

        // filter out the signals that come from this.start
        for (String key: all_signals.keySet()) {
            // parse key into start and destination
            String[] arrOfStr = key.split("::"); // keys' incoming and outgoing are separated by ::
            if (arrOfStr[0].equals(this.toString())) {
                desired_signals.put(key, all_signals.get(key));
            }
            // System.out.println("Signal's start link: " + arrOfStr[0]);
            // System.out.println("This link:           " + this.toString());
            // for (String a: arrOfStr)
            //     System.out.println(a);
        }

        return desired_signals; // TODO: edit return
    }

    public void setSignal(Link in, Link out, int new_phase) {
        destination.setSignal(in, out, new_phase);
    }

    public void setSignal(String key, int new_phase) {
        destination.setSignal(key, new_phase);
    }

    public ArrayList<Link> getOutgoing() {
        return destination.getOutgoing();
    }

    // move num_cars from this to destination_link
    public void moveCars(Link destination_link, double num_cars) {
        if (num_cars > this.cur_queue_length) {
            return; // TODO: throw an error for moving more cars than I have
        } else {
            this.setQueueLength(this.cur_queue_length - num_cars);
            destination_link.setQueueLength(destination_link.cur_queue_length + num_cars);
        }
    }

    // updates the flow on this link
    public void setQueueLength(double cur_queue_length)
    {
        if (cur_queue_length > C) {
            throw new IllegalArgumentException("Input is too large, must be less than capacity");
        }

        this.cur_queue_length = cur_queue_length;
    }

    
    /*
    public int hashCode()
    {
        return getStart().getId()+getdestination().getId()*10000;
    }

     */
    
    
    
    
    
    
    
    
    /* **********
    Exercise 3(a)
    ********** */
    public Node getStart()
    {
        return start;
    }
    
    public Node getdestination()
    {
        return destination;
    }
    
    
    /* **********
    Exercise 3(c)
    ********** */

    public String toString()
    {
        if (start == null && destination == null) {
            return "("+"null"+", "+"null"+")";
        }
        else if (start == null && destination != null) {
            return "("+"null"+", "+destination.getId()+")";
        }
        else if (start != null && destination == null) {
            return "("+start.getId()+", "+"null"+")";
        }
        else {
            return "("+start.getId()+", "+destination.getId()+")";
        }
    }
}
