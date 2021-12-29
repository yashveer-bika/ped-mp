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
        Intersection middle = new Intersection(1);
        VehNode top = new VehNode(2);
        VehNode bottom = new VehNode(3);
        VehNode left = new VehNode(4);
        VehNode right = new VehNode(5);

        // entry link
        VehLink B = new VehLink(top, middle, 200, true);
        VehLink A = new VehLink(left, middle, 200, true);
        Link[] entry_links = {A, B};

        // exit links
        VehLink C = new VehLink(middle, right, 200);
        VehLink D = new VehLink(middle, bottom, 200);

        Node[] nodes = {middle, top, bottom, left, right};
        Link[] links = {A, B, C, D};

        PedNode ped_1 = new PedNode(6);
        PedNode ped_2 = new PedNode(7);
        PedNode ped_3 = new PedNode(8);
        Intersection ped_4 = new Intersection(9);
        Intersection ped_5 = new Intersection(10);
        PedNode ped_6 = new PedNode(11);

        // entry links
        PedLink ped_1_to_4 = new PedLink(ped_1, ped_4, 200, true);
        PedLink ped_3_to_4 = new PedLink(ped_3, ped_4, 200, true);
        PedLink ped_2_to_5 = new PedLink(ped_2, ped_5, 200, true);
        PedLink ped_6_to_5 = new PedLink(ped_6, ped_5, 200, true);

        PedLink[] ped_entry_links = {ped_1_to_4, ped_3_to_4, ped_2_to_5, ped_6_to_5 };

        // crosswalks
        PedLink ped_4_to_5 = new PedLink(ped_4, ped_5, 200);
        PedLink ped_5_to_4 = new PedLink(ped_5, ped_4, 200);

        // exit links
        PedLink ped_4_to_1 = new PedLink(ped_4, ped_1, 200);
        PedLink ped_4_to_3 = new PedLink(ped_4, ped_3, 200);
        PedLink ped_5_to_2 = new PedLink(ped_5, ped_2, 200);
        PedLink ped_5_to_6 = new PedLink(ped_5, ped_6, 200);

        Link[] pedLinks = {ped_1_to_4, ped_3_to_4, ped_2_to_5, ped_6_to_5, ped_4_to_5, ped_5_to_4, ped_4_to_1, ped_4_to_3, ped_5_to_2, ped_5_to_6};
        Node[] pedNodes = {ped_1, ped_2, ped_3, ped_4, ped_5, ped_6};



        // TODO: make square vehicle node grid

        // TODO: make square pedestrian node grid
        PedNode[][] pedNodeGrid = {{}, {}};


        Network University_10th_Ave = new Network(nodes, links);

        Network University_10th_Ave_ped = new Network(pedNodes, pedLinks);

        // TODO: make engine network (has everything)

        // TODO: make intersections using the nodes and links

        Intersection mainIntersection = new Intersection(0, 0, 1, University_10th_Ave);


        /*
        for (Link link : University_10th_Ave.getLinks()) {
            link.setQueueLength(100);
        }

        for (Link link : University_10th_Ave_ped.getLinks()) {
            link.setQueueLength(100);
        }
        */




        // NOTE: CAR MOVEMENT

        /*
        // turn all signals on
        for (Link link : University_10th_Ave.getLinks()) {
            for (String key : link.getSignals().keySet() ) {
                // set signal
                link.setSignal(key, 1);
            }
        }
        System.out.println("(1)");
        // print out queue lengths
        for (Link link : University_10th_Ave.getLinks()) {
            System.out.println(link.getQueueLength());
        }

        // MOVE 5 CARS IF SIGNAL IS ON, FOR EACH SIGNAL
        for (Link linkA : University_10th_Ave.getLinks()) {
            for (Link linkB : linkA.getOutgoing()) {
                String key = linkA + "::" + linkB;
                // if the signal from link to out is on
                if (linkA.getSignals().get(key) == 1) {
                    linkA.moveCars(linkB, 5);
                }
            }
        }
        System.out.println("(2)");
        // print out queue lengths
        for (Link link : University_10th_Ave.getLinks()) {
            System.out.println(link.getQueueLength());
        }

        */

        /* NOTE: PEDESTRIAN MOVEMENT */

        /*
        // turn all signals on
        for (Link link : University_10th_Ave_ped.getLinks()) {
            for (String key : link.getSignals().keySet() ) {
                // set signal
                link.setSignal(key, 1);
            }
        }
        System.out.println("(1)");
        // print out queue lengths
        for (Link link : University_10th_Ave_ped.getLinks()) {
            System.out.println(link.getQueueLength());
        }

        // MOVE 5 CARS IF SIGNAL IS ON, FOR EACH SIGNAL
        for (Link linkA : University_10th_Ave_ped.getLinks()) {
            for (Link linkB : linkA.getOutgoing()) {
                String key = linkA + "::" + linkB;
                // if the signal from link to out is on
                if (linkA.getSignals().get(key) == 1) {
                    linkA.moveCars(linkB, 5);
                }
            }
        }
        System.out.println("\n(2)");
        // print out queue lengths
        for (Link link : University_10th_Ave_ped.getLinks()) {
            System.out.println(link.getQueueLength());
        }
        */


        //        for (Link link : University_10th_Ave.getLinks()) {
//            System.out.println(link.getSignals());
//        }

        // University_10th_Ave.printSignal();
        // University_10th_Ave.setSignal(A, C, 1);
        // University_10th_Ave.printSignal();

        /*
        for (Link link : University_10th_Ave.getLinks()) {
            System.out.println(link);
            System.out.println(link.getFlow());
        }
        */

        /*
        for (Node node : University_10th_Ave.getNodes()) {
            System.out.println("Node");
            System.out.println(node);
            System.out.println("Incoming links");
            System.out.println(node.getIncoming());
            System.out.println("Outgoing links");
            System.out.println(node.getOutgoing());
            System.out.println();

        }

        */

//        // iterate over 6 time steps, we have demand for each link
//        // define demand from each entry link for each time step
//
//        int T = 6; // NUMBER OF TIME STEPS
//
//        ArrayList<ArrayList> demands = new ArrayList<ArrayList>();
//        for (int i=0; i < T; i++) { // 6 time steps
//            ArrayList<Double> demand_at_time = new ArrayList<Double>();
//
//            for (int j=0; j < entry_links.length; j++) {
//                demand_at_time.add(10.0);
//            }
//            demands.add(demand_at_time);
//        }
//
//
//        // System.out.println("Demands");
//        // System.out.println(demands);
//        // System.out.println(demands.get(0).get(0));
//        // System.out.println(demands.get(0).get(0).getClass().getSimpleName());
//
//        // Each entry link goes into Node middle (1 in the drawing)
//        // So, we get the outgoing links from middle
//        ArrayList<Link> outgoing = middle.getOutgoing();
//
//        // MAKE TURNING RATIOS
//        HashMap<ArrayList<Link>, Double> turning_ratios = new HashMap<ArrayList<Link>, Double>();
//
//        for (int i = 0; i < entry_links.length; i++) {
//            for (int j = 0; j < outgoing.size(); j++) {
//                Link entry = entry_links[i];
//                Link out_link = outgoing.get(j);
//                ArrayList<Link> pair = new ArrayList<Link>();
//                pair.add(entry);
//                pair.add(out_link);
//                turning_ratios.put(pair, 1.0 / outgoing.size());
//            }
//        }
//        // System.out.println("Turning ratios");
//        // System.out.println(turning_ratios);
//
//
//        // make a hash table of turn ratios
//        System.out.println("Initial flow");
//        System.out.println(University_10th_Ave.showFlow());
//
//        for (int i=0; i < T; i++) { // Iterates over 6 time steps
//            System.out.println();
//
//            // Adds demand flow into the entry links
//            for (int j = 0; j < entry_links.length; j++) {
//                Link link = entry_links[j];
//                double demand = (double) demands.get(i).get(j);
//                double current_flow = link.getFlow();
//                link.setFlow(current_flow + demand);
//            }
//            System.out.println("Network at time " + i + ": just adding demand");
//            System.out.println(University_10th_Ave.showFlow());
//
//
//            // Moves all the flow according to turn ratio
//
//            for (Link entry_link : entry_links) {
//                // iterate over the links leaving Node 1
//                double init_entry_flow = entry_link.getFlow();
//                ArrayList<Link> out_links = middle.getOutgoing();
//                for (int j = 0; j < out_links.size(); j++) {
//                    // edit flow, set tr_{ij} = 1 / (no. of possible j's)
//                    double tr = (1.0 / out_links.size()); // HARDCODED TURNING RATIO
//                    double flow_out = tr * init_entry_flow;
//                    entry_link.setFlow(entry_link.getFlow() - flow_out);
//                    out_links.get(j).setFlow( out_links.get(j).getFlow() + flow_out);
//                }
//
//            }
//
//
//            System.out.println("Network at time " + i + ": after turning (assuming no turn conflicts)");
//            System.out.println(University_10th_Ave.showFlow());
//            System.out.println();
//
//        }




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