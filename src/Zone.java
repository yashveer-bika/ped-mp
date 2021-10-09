/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sta;

import java.util.HashMap;

/**
 *
 * @author micha
 */
public class Zone extends Node
{
    private HashMap<Node, Double> demand;
    
    /* **********
    Exercise 4(a)
    ********** */
    public Zone(int id)
    {
        // remove this
        super(id);
        demand = new HashMap<>();
        thruNode = true;
    }
    
    
    
    /* **********
    Exercise 4(b)
    ********** */
    public void addDemand(Node s, double d)
    {
        if(demand.containsKey(s))
        {
            demand.put(s, demand.get(s) + d);
        }
        else
        {
            demand.put(s, d);
        }
    }
    
    public double getDemand(Node s)
    {
        if(demand.containsKey(s))
        {
            return demand.get(s);
        }
        else
        {
            return 0;
        }
    }
    
    
    /* **********
    Exercise 4(c)
    ********** */
    public double getProductions()
    {
        double total = 0;
        
        for(Node s : demand.keySet())
        {
            total += demand.get(s);
        }
        
        return total;
    }
    
    
    
    /* **********
    Exercise 4(d)
    ********** */
    
    private boolean thruNode;
    
    public boolean isThruNode()
    {
        return thruNode;
    }
    
    public void setThruNode(boolean thru)
    {
        thruNode = thru;
    }
}
