/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
    private double queue_length;
    
    // parameters for travel time calculation. max_flow is the free flow time, C is the capacity
    private double capacity;
    
    // the start and end nodes of this link. Links are directed.
    private Node start, destination;

    private String direction;
    // POSSIBLE values for direction:
    // "NS", "EW", "SN", "WE", "entry"


    public Link() {

    }

    // construct this Link with the given parameters
    public Link(Node start, Node destination, double C, boolean entry)
    {
        this.start = start;
        this.destination = destination;
        this.capacity = C;
        this.entry = entry;
        if (entry) {
            this.direction = "entry";
        } else {
            throw new IllegalArgumentException("the entry parameter should only ever be true");
        }
        
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
        this.capacity = C;
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
        this.capacity = C;

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

    public Node getDestination() {
        return this.destination;
    }



//    public HashMap<String, Integer> getSignals() {
//        HashMap<String, Integer> all_signals = destination.getSignals();
//        HashMap<String, Integer> desired_signals = new HashMap<String, Integer>();
//
//        // filter out the signals that come from this.start
//        for (String key: all_signals.keySet()) {
//            // parse key into start and destination
//            String[] arrOfStr = key.split("::"); // keys' incoming and outgoing are separated by ::
//            if (arrOfStr[0].equals(this.toString())) {
//                desired_signals.put(key, all_signals.get(key));
//            }
//            // System.out.println("Signal's start link: " + arrOfStr[0]);
//            // System.out.println("This link:           " + this.toString());
//            // for (String a: arrOfStr)
//            //     System.out.println(a);
//        }
//
//        return desired_signals; // TODO: edit return
//    }
//
//    public void setSignal(Link in, Link out, int new_phase) {
//        destination.setSignal(in, out, new_phase);
//    }
//
//    public void setSignal(String key, int new_phase) {
//        destination.setSignal(key, new_phase);
//    }
//
//    public Set<Link> getOutgoing() {
//        return destination.getOutgoing();
//    }

//    // move num_cars from this to destination_link
//    public void moveCars(Link destination_link, double num_cars) {
//        if (num_cars > this.queue_length) {
//            return; // TODO: throw an error for moving more cars than I have
//        } else {
//            this.setQueueLength(this.queue_length - num_cars);
//            destination_link.setQueueLength(destination_link.queue_length + num_cars);
//        }
//    }

    // updates the flow on this link
    public void setQueueLength(double cur_queue_length)
    {
        if (cur_queue_length > capacity) {
            throw new IllegalArgumentException("New queue length is too large, must be less than capacity");
        }
        this.queue_length = cur_queue_length;
    }

    /*
    public int hashCode()
    {
        return getStart().getId()+getdestination().getId()*10000;
    }

     */

    public Node getStart()
    {
        return start;
    }

    
    /* **********
    Exercise 3(c)
    ********** */

    public String toString()
    {
        if (this.start == null && this.destination == null) {
            return "("+"null"+", "+"null"+" " + this.direction +")";
        }
        else if (this.start == null && this.destination != null) {
            return "("+"null"+", "+this.destination.getId()+" " + this.direction+")";
        }
        else if (this.start != null && this.destination == null) {
            return "("+this.start.getId()+", "+"null"+" " + this.direction+")";
        }
        else {
            return "("+this.start.getId()+", "+this.destination.getId()+" " + this.direction+")";
        }
    }
}
