/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

import java.util.ArrayList;
import java.util.HashMap; // import the HashMap class
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author micha
 */
public class Main 
{
    public static void main(String[] args)
    {
        System.out.println("Hello world");

        // TODO: create a single intersection representation
        // Have 8 links, 1 in 1 out from each side of the intersection

        Node middle = new Node(1);
        Node top = new Node(2);
        Node bottom = new Node(3);
        Node left = new Node(4);
        Node right = new Node(5);

        // entry link
        Link B = new Link(top, middle, 20, 100);
        // Link B = new Link(bottom, middle, 20, 40);
        Link A = new Link(left, middle, 40, 200);
        Link[] entry_links = {A, B};

        // exit links
        // Link D = new Link(middle, top, 20, 100);
        Link C = new Link(middle, right, 40, 200);
        Link D = new Link(middle, bottom, 20, 40);

        Node[] nodes = {middle, top, bottom, left, right};
        Link[] links = {A, B, C, D};
        Network University_10th_Ave = new Network(nodes, links);

        /*
        for (Link link : University_10th_Ave.getLinks()) {
            System.out.println(link);
            System.out.println(link.getFlow());
        }
        */

        // iterate over 6 time steps, we have demand for each link
        // define demand from each entry link for each time step

        int T = 6; // NUMBER OF TIME STEPS

        ArrayList<ArrayList> demands = new ArrayList<ArrayList>();
        for (int i=0; i < T; i++) { // 6 time steps
            ArrayList<Double> demand_at_time = new ArrayList<Double>();

            for (int j=0; j < entry_links.length; j++) {
                demand_at_time.add(10.0);
            }
            demands.add(demand_at_time);
        }


        // System.out.println("Demands");
        // System.out.println(demands);
        // System.out.println(demands.get(0).get(0));
        // System.out.println(demands.get(0).get(0).getClass().getSimpleName());

        // Each entry link goes into Node middle (1 in the drawing)
        // So, we get the outgoing links from middle
        ArrayList<Link> outgoing = middle.getOutgoing();

        // MAKE TURNING RATIOS
        HashMap<ArrayList<Link>, Double> turning_ratios = new HashMap<ArrayList<Link>, Double>();

        for (int i = 0; i < entry_links.length; i++) {
            for (int j = 0; j < outgoing.size(); j++) {
                Link entry = entry_links[i];
                Link out_link = outgoing.get(j);
                ArrayList<Link> pair = new ArrayList<Link>();
                pair.add(entry);
                pair.add(out_link);
                turning_ratios.put(pair, 1.0 / outgoing.size());
            }
        }
        // System.out.println("Turning ratios");
        // System.out.println(turning_ratios);


        // make a hash table of turn ratios
        System.out.println("Initial flow");
        System.out.println(University_10th_Ave.showFlow());

        for (int i=0; i < T; i++) { // Iterates over 6 time steps
            System.out.println();

            // Adds demand flow into the entry links
            for (int j = 0; j < entry_links.length; j++) {
                Link link = entry_links[j];
                double demand = (double) demands.get(i).get(j);
                double current_flow = link.getFlow();
                link.setFlow(current_flow + demand);
            }
            System.out.println("Network at time " + i + ": just adding demand");
            System.out.println(University_10th_Ave.showFlow());


            // Moves all the flow according to turn ratio

            for (Link entry_link : entry_links) {
                // iterate over the links leaving Node 1
                double init_entry_flow = entry_link.getFlow();
                ArrayList<Link> out_links = middle.getOutgoing();
                for (int j = 0; j < out_links.size(); j++) {
                    // edit flow, set tr_{ij} = 1 / (no. of possible j's)
                    double tr = (1.0 / out_links.size()); // HARDCODED TURNING RATIO
                    double flow_out = tr * init_entry_flow;
                    entry_link.setFlow(entry_link.getFlow() - flow_out);
                    out_links.get(j).setFlow( out_links.get(j).getFlow() + flow_out);
                }

            }

            /*
            Iterator it = turning_ratios.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                // System.out.println(pair.getKey().getClass().getSimpleName());
                ArrayList<Link> pair_o_links = (ArrayList<Link>) pair.getKey();
                double t_ratio = (double) pair.getValue();
                Link in = pair_o_links.get(0);
                Link out = pair_o_links.get(1);
                in.setFlow(in.getFlow() * (1.0 - t_ratio));
                out.setFlow(in.getFlow() * t_ratio);
                // System.out.println(in);
                // System.out.println(out);
                // System.out.println(pair.getKey() + " = " + pair.getValue());
                // it.remove(); // avoids a ConcurrentModificationException
            }
            */


            System.out.println("Network at time " + i + ": after turning (assuming no turn conflicts)");
            System.out.println(University_10th_Ave.showFlow());
            System.out.println();

        }


        /*  TODO:
             hardcode intersection conflict

            Assume we follow a hardcoded cycle
            1) CE is green, CD is green, CF is green
            2) AE is green, BE is green
            3) AF is green, BD is green, BE is green

         */
        // TODO: make turning ratios happens
        // TODO: define signal on/off
        // TODO: define a move function that moves cars according to the current state

    }
}