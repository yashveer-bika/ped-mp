/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.util.LinkedList;

/**
 * This class propagates flow according to the point queue model (no spatial constraints).
 *
 * @author: Yashveer Bika, Michael W. Levin
 */
public class ExitLink extends Link
{

    private LinkedList<Vehicle> queue;

    public ExitLink()
    {
        super(0, null, null, 0, 0, 0, 1);

        queue = new LinkedList<Vehicle>();

    }

    public ExitLink(int id, Node n)
    {
        super(id, n, null, 0, 0, 0, 1);

        queue = new LinkedList<Vehicle>();

    }


    public void reset()
    {
        // fill this in
        queue.clear();
    }

    public double getOccupancy()
    {
        // fill this in
        return queue.size();
    }


    public void step()
    {
        // fill this in
    }

    public void update()
    {
        // fill this in
    }

    public double getSendingFlow()
    {
        // fill this in
        return queue.size();
    }

    public double getReceivingFlow()
    {
        // fill this in
        return Integer.MAX_VALUE;
    }

    public void addFlow(double y)
    {
        // fill this in
    }

    public void removeFlow(double y)
    {
        // fill this in
    }

    public boolean isEntry() {
        return true;
    }
}