/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

/**
 *
 * @author micha
 */
public class Link 
{
    
    // the flow on this link
    private double x;
    
    // parameters for travel time calculation. t_ff is the free flow time, C is the capacity, alpha and beta are the calibration parameters in the BPR function
    private double t_ff, C, alpha, beta;
    
    // the start and end nodes of this link. Links are directed.
    private Node start, end;
    
    // construct this Link with the given parameters
    public Link(Node start, Node end, double t_ff, double C, double alpha, double beta)
    {
        this.start = start;
        this.end = end;
        this.t_ff = t_ff;
        this.C = C;
        this.alpha = alpha;
        this.beta = beta;
        
        if(start != null)
        {
            start.addOutgoingLink(this);
        }
    }
    
    // updates the flow on this link
    public void setFlow(double x)
    {
        this.x = x;
    }
    
    /* **********
    Exercise 1
    ********** */
    public double getTravelTime()
    {
        // fill this in
        double t_ij = 0;
        
        t_ij = t_ff * (1 + alpha * Math.pow(x/C, beta)); 
        
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
        return x;
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
        x = (1 - stepsize) * x + stepsize * xstar;
        xstar = 0;
    }
}
