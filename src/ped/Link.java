/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;
import java.util.HashMap;

/**
 *
 * @author micha
 */
public class Link 
{
    
    // the flow on this link
    private double cur_queue_length;
    
    // parameters for travel time calculation. max_flow is the free flow time, C is the capacity
    private double max_flow, C, alpha, beta;
    
    // the start and end nodes of this link. Links are directed.
    private Node start, end;
    
    // construct this Link with the given parameters
    public Link(Node start, Node end, double max_flow, double C)
    {
        this.start = start;
        this.end = end;
        this.max_flow = max_flow;
        this.C = C;
        
        if(start != null)
        {
            start.addOutgoingLink(this);
        }
        if(end != null)
        {
            end.addIncomingLink(this);
        }
    }

    public HashMap<String, Integer> getSignals() {
        HashMap<String, Integer> all_signals = end.getSignals();
        HashMap<String, Integer> desired_signals = new HashMap<String, Integer>();

        // filter out the signals that come from this.start
        for (String key: all_signals.keySet()) {
            // parse key into start and end
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

    // updates the flow on this link
    public void setFlow(double cur_queue_length)
    {
        this.cur_queue_length = cur_queue_length;
    }
    
    /* **********
    Exercise 1
    ********** */
    public double getTravelTime()
    {
        // fill this in
        double t_ij = 0;
        
        t_ij = max_flow * (1 + alpha * Math.pow(cur_queue_length/C, beta));
        
        return t_ij;
    }
    
    
    
    
    /* **********
    Exercise 2(a)
    ********** */
    public double getCapacity()
    {
        return C;
    }
    
    public double getFlow()
    {
        return cur_queue_length;
    }
    
    
    
    public int hashCode()
    {
        return getStart().getId()+getEnd().getId()*10000;
    }
    
    
    
    
    
    
    
    
    /* **********
    Exercise 3(a)
    ********** */
    public Node getStart()
    {
        return start;
    }
    
    public Node getEnd()
    {
        return end;
    }
    
    
    /* **********
    Exercise 3(c)
    ********** */
    public String toString()
    {
        return "("+start.getId()+", "+end.getId()+")";
    }
    
    
    /* **********
    Exercise 8(a)
    ********** */
    private double xstar = 0;
    
    public void addXstar(double flow)
    {
        xstar += flow;
    }
    
    /* **********
    Exercise 8(b)
    ********** */
    public void calculateNewX(double stepsize)
    {
        cur_queue_length = (1 - stepsize) * cur_queue_length + stepsize * xstar;
        xstar = 0;
    }
}
