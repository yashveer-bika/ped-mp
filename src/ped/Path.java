/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.ArrayList;

/**
 *
 * @author micha
 */
public class Path extends ArrayList<Link>
{
    public double getTravelTime()
    {
        double output = 0;
        
        for(Link l : this)
        {
            output += l.getTravelTime();
        }
        
        return output;
    }
    
    public boolean isConnected()
    {
        for(int i = 0; i < size()-1; i++)
        {
            if(get(i).getEnd() != get(i+1).getStart())
            {
                return false;
            }
        }
        
        return true;
    }
    
    /* **********
    Exercise 6(a)
    ********** */
    public Node getSource()
    {
        return get(0).getStart();
    }
    
    public Node getDest()
    {
        return get(size()-1).getEnd();
    }
    
    /* **********
    Exercise 8(a)
    ********** */
    public void addHstar(double h)
    {
        for(Link l : this)
        {
            l.addXstar(h);
        }
    }
}
