/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

import java.util.*;

/**
 *
 * @author micha
 */
public class Main 
{
    public static void main(String[] args)
    {
        VehIntersection middle = new VehIntersection(0);
        VehNode top = new VehNode(1);
        VehNode bottom = new VehNode(2);
        VehNode left = new VehNode(3);
        VehNode right = new VehNode(4);

        // incoming links
        VehLink B = new VehLink(top, middle, 200, "NS");
        VehLink A = new VehLink(left, middle, 200, "WE");
        HashSet<VehLink> incomingLinks = new HashSet<VehLink>();
        incomingLinks.add(A);
        incomingLinks.add(B);
        middle.setIncomingLinks(incomingLinks);

        // outgoing links
        VehLink C = new VehLink(middle, right, 200, "WE");
        VehLink D = new VehLink(middle, bottom, 200, "NS");
        HashSet<VehLink> outgoingLinks = new HashSet<VehLink>();
        outgoingLinks.add(C);
        outgoingLinks.add(D);
        middle.setOutgoingLinks(outgoingLinks);

        // add links
        // NOTE: this uses the incoming and outgoing vehicle links to set allLinks
        middle.setVehLinks();

        // Node[] nodes = {middle, top, bottom, left, right};
        // Link[] links = {A, B, C, D};

        PedIntersection ped_1 = new PedIntersection(6);
        PedIntersection ped_2 = new PedIntersection(7);
        PedIntersection ped_3 = new PedIntersection(8);
        PedIntersection ped_4 = new PedIntersection(9);

        // entry links
        PedLink into_ped_1 = new PedLink(null, ped_1, 200, "NS");
        PedLink into_ped_2 = new PedLink(null, ped_2, 200, "NS");
        PedLink into_ped_3 = new PedLink(null, ped_3, 200, "SN");
        PedLink into_ped_4 = new PedLink(null, ped_4, 200, "SN");

        // exit links
        PedLink out_ped_1 = new PedLink(ped_1, null, 200, "EW");
        PedLink out_ped_2 = new PedLink(ped_2, null, 200, "WE");
        PedLink out_ped_3 = new PedLink(ped_3, null, 200, "EW");
        PedLink out_ped_4 = new PedLink(ped_4, null, 200, "WE");

        // crosswalks
        PedLink ped_1_to_2 = new PedLink(ped_1, ped_2, 200, "WE");
        PedLink ped_1_to_3 = new PedLink(ped_1, ped_3, 200, "NS");
        PedLink ped_2_to_4 = new PedLink(ped_2, ped_4, 200, "NS");
        PedLink ped_3_to_4 = new PedLink(ped_3, ped_4, 200, "WE");
        PedLink ped_2_to_1 = new PedLink(ped_2, ped_1, 200, "EW");
        PedLink ped_3_to_1 = new PedLink(ped_3, ped_1, 200, "SN");
        PedLink ped_4_to_2 = new PedLink(ped_4, ped_2, 200, "SN");
        PedLink ped_4_to_3 = new PedLink(ped_4, ped_3, 200, "EW");

        // ped 1
        HashSet<PedLink> ped1IncomingLinks = new HashSet<PedLink>();
        HashSet<PedLink> ped1OutgoingLinks = new HashSet<PedLink>();
        ped1IncomingLinks.add(into_ped_1);
        ped1IncomingLinks.add(ped_2_to_1);
        ped1IncomingLinks.add(ped_3_to_1);
        ped1OutgoingLinks.add(out_ped_1);
        ped1OutgoingLinks.add(ped_1_to_3);
        ped1OutgoingLinks.add(ped_1_to_2);
        ped_1.setIncomingLinks(ped1IncomingLinks);
        ped_1.setOutgoingLinks(ped1OutgoingLinks);
        ped_1.setPedLinks();

        // ped2
        HashSet<PedLink> ped2IncomingLinks = new HashSet<PedLink>();
        HashSet<PedLink> ped2OutgoingLinks = new HashSet<PedLink>();
        ped2IncomingLinks.add(into_ped_2);
        ped2IncomingLinks.add(ped_4_to_2);
        ped2IncomingLinks.add(ped_1_to_2);
        ped2OutgoingLinks.add(out_ped_2);
        ped2OutgoingLinks.add(ped_2_to_1);
        ped2OutgoingLinks.add(ped_2_to_4);
        ped_2.setIncomingLinks(ped2IncomingLinks);
        ped_2.setOutgoingLinks(ped2OutgoingLinks);
        ped_2.setPedLinks();

        // ped3
        HashSet<PedLink> ped3IncomingLinks = new HashSet<PedLink>();
        HashSet<PedLink> ped3OutgoingLinks = new HashSet<PedLink>();
        ped3IncomingLinks.add(into_ped_3);
        ped3IncomingLinks.add(ped_4_to_3);
        ped3IncomingLinks.add(ped_1_to_3);
        ped3OutgoingLinks.add(out_ped_3);
        ped3OutgoingLinks.add(ped_3_to_1);
        ped3OutgoingLinks.add(ped_3_to_4);
        ped_3.setIncomingLinks(ped3IncomingLinks);
        ped_3.setOutgoingLinks(ped3OutgoingLinks);
        ped_3.setPedLinks();

        // ped 4
        HashSet<PedLink> ped4IncomingLinks = new HashSet<PedLink>();
        HashSet<PedLink> ped4OutgoingLinks = new HashSet<PedLink>();
        ped4IncomingLinks.add(into_ped_4);
        ped4IncomingLinks.add(ped_3_to_4);
        ped4IncomingLinks.add(ped_2_to_4);
        ped4OutgoingLinks.add(out_ped_4);
        ped4OutgoingLinks.add(ped_4_to_2);
        ped4OutgoingLinks.add(ped_4_to_3);
        ped_4.setIncomingLinks(ped4IncomingLinks);
        ped_4.setOutgoingLinks(ped4OutgoingLinks);
        ped_4.setPedLinks();


        VehIntersection vehInt = middle;
        ArrayList<PedIntersection> pedInts = new ArrayList<>() {
            {
                add(ped_1);
                add(ped_2);
                add(ped_3);
                add(ped_4);
            }
        };
        // pedInts = {ped_1, ped_2, ped_3, ped_4};

        // Intersection: VehIntersection, Set PedIntersection

        Intersection mainIntersection = new Intersection(vehInt, pedInts);
        mainIntersection.generatePhases(); // TODO: working on this

        // PedIntersection[] pedInts2 = {ped_1, null, ped_3, ped_4};
        // Intersection ignoreSomePedsIntersection = new Intersection(vehInt, pedInts2);


        // Network University_10th_Ave = new Network(nodes, links);
        // Network University_10th_Ave_ped = new Network(pedNodes, pedLinks);


        System.out.println("ooga booga");

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