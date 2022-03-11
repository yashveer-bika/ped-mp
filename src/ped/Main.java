/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

import java.io.File;
import java.util.*;

import util.*;

/**
 *
 * @author bikax003, micha
 */
public class Main 
{
    public static void main(String[] args)
    {
//         File sf_net_file = new File("data/SiouxFalls/net.txt");
//         File sf_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
//         File sf_links_f = new File("data/SiouxFalls/network/links.txt");
//         Network sf_net2 = new Network(sf_nodes_f, sf_links_f);
//         // Network sf_net = new Network(sf_net_file);
//         System.out.println( sf_net2.getNodes() );

        File pq3_nodes_f = new File("data/PQ3/nodes.txt");
        File pq3_links_f = new File("data/PQ3/links.txt");
        // TODO: implement a robust veh-2-veh conflict solution
        Simulator pq3_net = new Simulator(pq3_nodes_f, pq3_links_f, false);




        // run iterate on all intersections
        for (Intersection i : pq3_net.getIntersectionSet()) {
            i.iterateTimeStep();
        }


//        for (Node n : pq3_net.getNodeSet()) {
//            System.out.println("node : " + n.getId() + ", " + n.getLocation());
//
//        }


        // generate orthogonal point

//        /*
//        input : double angle
//                Location loc
//                double dist
//         */
//        double angle;
//        Location loc;
//        double dist;
//        angle = 0*Math.PI / 4;
//        loc = new Location(0,0);
//        dist = 1.0;
//        Location newLoc = loc.spawnNewLocation(dist, angle);
//        System.out.println("newLoc: " + newLoc);



        // pq3_net.drawAllNodes();
        // sanity checks
//        System.out.println( pq3_net.getNodes().size() );
//        System.out.println( pq3_net.getNodes());
//        System.out.println( pq3_net.getLinkSet().size() );
//        System.out.println( pq3_net.getLinkSet());
//
//        System.out.println( pq3_net.getIntersectionSet());

        /*
        VehIntersection leftVehInt = new VehIntersection(0);
        VehNode top = new VehNode(1);
        VehNode bottom = new VehNode(2);
        VehNode left = new VehNode(3);
        VehIntersection rightVehInt = new VehIntersection(4);
        VehNode right = rightVehInt;


        // incoming links
        VehLink B = new VehLink(top, leftVehInt, 200, "NS");
        VehLink A = new VehLink(left, leftVehInt, 200, "WE");
        HashSet<VehLink> incomingLinks = new HashSet<VehLink>();
        incomingLinks.add(A);
        incomingLinks.add(B);
        leftVehInt.setIncomingLinks(incomingLinks);

        // outgoing links
        VehLink C = new VehLink(leftVehInt, right, 200, "WE");
        VehLink D = new VehLink(leftVehInt, bottom, 200, "NS");
        HashSet<VehLink> outgoingLinks = new HashSet<VehLink>();
        outgoingLinks.add(C);
        outgoingLinks.add(D);
        leftVehInt.setOutgoingLinks(outgoingLinks);

        // add links
        // NOTE: this uses the incoming and outgoing vehicle links to set allLinks
        leftVehInt.setVehLinks();

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
        Set<Crosswalk> crosswalks = new HashSet<>();
        PedLink ped_1_to_2 = new PedLink(ped_1, ped_2, 200, "WE");
        PedLink ped_2_to_1 = new PedLink(ped_2, ped_1, 200, "EW");
        Crosswalk cTop = new Crosswalk(ped_1_to_2, ped_2_to_1, "TOP");
        crosswalks.add(cTop);

        PedLink ped_1_to_3 = new PedLink(ped_1, ped_3, 200, "NS");
        PedLink ped_3_to_1 = new PedLink(ped_3, ped_1, 200, "SN");
        Crosswalk cLeft = new Crosswalk(ped_1_to_3, ped_3_to_1, "LEFT");
        crosswalks.add(cLeft);

        PedLink ped_2_to_4 = new PedLink(ped_2, ped_4, 200, "NS");
        PedLink ped_4_to_2 = new PedLink(ped_4, ped_2, 200, "SN");
        Crosswalk cRight = new Crosswalk(ped_2_to_4, ped_4_to_2, "RIGHT");
        crosswalks.add(cRight);

        PedLink ped_3_to_4 = new PedLink(ped_3, ped_4, 200, "WE");
        PedLink ped_4_to_3 = new PedLink(ped_4, ped_3, 200, "EW");
        Crosswalk cBottom = new Crosswalk(ped_3_to_4, ped_4_to_3, "BOTTOM");
        crosswalks.add(cBottom);

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

        VehIntersection vehInt = leftVehInt;
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
        Intersection mainIntersection = new Intersection(vehInt, pedInts, crosswalks);
        mainIntersection.generatePhaseSet();

        // PedIntersection[] pedInts2 = {ped_1, null, ped_3, ped_4};
        // Intersection ignoreSomePedsIntersection = new Intersection(vehInt, pedInts2);


        // BUILD THE RIGHT SIDE INTERSECTION


        VehNode top_R = new VehNode(5);
        VehNode bottom_R = new VehNode(6);
        VehNode left_R = leftVehInt;
        VehNode right_R = new VehNode(7);

        // incoming links
        VehLink B_R = new VehLink(top_R, rightVehInt, 200, "NS");
        VehLink A_R = new VehLink(left_R, rightVehInt, 200, "WE");

        HashSet<VehLink> incomingLinks_R = new HashSet<>();
        incomingLinks_R.add(A_R);
        incomingLinks_R.add(B_R);
        rightVehInt.setIncomingLinks(incomingLinks_R);



        // outgoing links
        VehLink C_R = new VehLink(rightVehInt, right_R, 200, "WE");
        VehLink D_R = new VehLink(rightVehInt, bottom_R, 200, "NS");
        HashSet<VehLink> outgoingLinks_R = new HashSet<>();
        outgoingLinks_R.add(C_R);
        outgoingLinks_R.add(D_R);
        rightVehInt.setOutgoingLinks(outgoingLinks_R);

        // add links
        // NOTE: this uses the incoming and outgoing vehicle links to set allLinks
        rightVehInt.setVehLinks();

        // Node[] nodes = {middle, top_R, bottom, left, right};
        // Link[] links = {A, B, C, D};
        PedIntersection ped_1_R = new PedIntersection(6);
        PedIntersection ped_2_R = new PedIntersection(7);
        PedIntersection ped_3_R = new PedIntersection(8);
        PedIntersection ped_4_R = new PedIntersection(9);

        // entry links
        PedLink into_ped_1_R = new PedLink(null, ped_1_R, 200, "NS");
        PedLink into_ped_2_R = new PedLink(null, ped_2_R, 200, "NS");
        PedLink into_ped_3_R = new PedLink(null, ped_3_R, 200, "SN");
        PedLink into_ped_4_R = new PedLink(null, ped_4_R, 200, "SN");

        // exit links
        PedLink out_ped_1_R = new PedLink(ped_1_R, null, 200, "EW");
        PedLink out_ped_2_R = new PedLink(ped_2_R, null, 200, "WE");
        PedLink out_ped_3_R = new PedLink(ped_3_R, null, 200, "EW");
        PedLink out_ped_4_R = new PedLink(ped_4_R, null, 200, "WE");

        // crosswalks
        Set<Crosswalk> crosswalks_R = new HashSet<>();
        PedLink ped_1_to_2_R = new PedLink(ped_1_R, ped_2_R, 200, "WE");
        PedLink ped_2_to_1_R = new PedLink(ped_2_R, ped_1_R, 200, "EW");
        Crosswalk cTop_R = new Crosswalk(ped_1_to_2_R, ped_2_to_1_R, "TOP");
        crosswalks_R.add(cTop_R);

        PedLink ped_1_to_3_R = new PedLink(ped_1_R, ped_3_R, 200, "NS");
        PedLink ped_3_to_1_R = new PedLink(ped_3_R, ped_1_R, 200, "SN");
        Crosswalk cLeft_R = new Crosswalk(ped_1_to_3_R, ped_3_to_1_R, "LEFT");
        crosswalks_R.add(cLeft_R);

        PedLink ped_2_to_4_R = new PedLink(ped_2_R, ped_4_R, 200, "NS");
        PedLink ped_4_to_2_R = new PedLink(ped_4_R, ped_2_R, 200, "SN");
        Crosswalk cRight_R = new Crosswalk(ped_2_to_4_R, ped_4_to_2_R, "RIGHT");
        crosswalks_R.add(cRight_R);

        PedLink ped_3_to_4_R = new PedLink(ped_3_R, ped_4_R, 200, "WE");
        PedLink ped_4_to_3_R = new PedLink(ped_4_R, ped_3_R, 200, "EW");
        Crosswalk cBottom_R = new Crosswalk(ped_3_to_4_R, ped_4_to_3_R, "BOTTOM");
        crosswalks_R.add(cBottom_R);

        // ped 1
        HashSet<PedLink> ped1IncomingLinks_R = new HashSet<PedLink>();
        HashSet<PedLink> ped1OutgoingLinks_R = new HashSet<PedLink>();
        ped1IncomingLinks_R.add(into_ped_1_R);
        ped1IncomingLinks_R.add(ped_2_to_1_R);
        ped1IncomingLinks_R.add(ped_3_to_1_R);
        ped1OutgoingLinks_R.add(out_ped_1_R);
        ped1OutgoingLinks_R.add(ped_1_to_3_R);
        ped1OutgoingLinks_R.add(ped_1_to_2_R);
        ped_1_R.setIncomingLinks(ped1IncomingLinks_R);
        ped_1_R.setOutgoingLinks(ped1OutgoingLinks_R);
        ped_1_R.setPedLinks();

        // ped2
        HashSet<PedLink> ped2IncomingLinks_R = new HashSet<PedLink>();
        HashSet<PedLink> ped2OutgoingLinks_R = new HashSet<PedLink>();
        ped2IncomingLinks_R.add(into_ped_2_R);
        ped2IncomingLinks_R.add(ped_4_to_2_R);
        ped2IncomingLinks_R.add(ped_1_to_2_R);
        ped2OutgoingLinks_R.add(out_ped_2_R);
        ped2OutgoingLinks_R.add(ped_2_to_1_R);
        ped2OutgoingLinks_R.add(ped_2_to_4_R);
        ped_2_R.setIncomingLinks(ped2IncomingLinks_R);
        ped_2_R.setOutgoingLinks(ped2OutgoingLinks_R);
        ped_2_R.setPedLinks();

        // ped3
        HashSet<PedLink> ped3IncomingLinks_R = new HashSet<PedLink>();
        HashSet<PedLink> ped3OutgoingLinks_R = new HashSet<PedLink>();
        ped3IncomingLinks_R.add(into_ped_3_R);
        ped3IncomingLinks_R.add(ped_4_to_3_R);
        ped3IncomingLinks_R.add(ped_1_to_3_R);
        ped3OutgoingLinks_R.add(out_ped_3_R);
        ped3OutgoingLinks_R.add(ped_3_to_1_R);
        ped3OutgoingLinks_R.add(ped_3_to_4_R);
        ped_3_R.setIncomingLinks(ped3IncomingLinks_R);
        ped_3_R.setOutgoingLinks(ped3OutgoingLinks_R);
        ped_3_R.setPedLinks();

        // ped 4
        HashSet<PedLink> ped4IncomingLinks_R = new HashSet<PedLink>();
        HashSet<PedLink> ped4OutgoingLinks_R = new HashSet<PedLink>();
        ped4IncomingLinks_R.add(into_ped_4_R);
        ped4IncomingLinks_R.add(ped_3_to_4_R);
        ped4IncomingLinks_R.add(ped_2_to_4_R);
        ped4OutgoingLinks_R.add(out_ped_4_R);
        ped4OutgoingLinks_R.add(ped_4_to_2_R);
        ped4OutgoingLinks_R.add(ped_4_to_3_R);
        ped_4_R.setIncomingLinks(ped4IncomingLinks_R);
        ped_4_R.setOutgoingLinks(ped4OutgoingLinks_R);
        ped_4_R.setPedLinks();

        VehIntersection vehInt_R = rightVehInt;
        ArrayList<PedIntersection> pedInts_R = new ArrayList<>() {
            {
                add(ped_1_R);
                add(ped_2_R);
                add(ped_3_R);
                add(ped_4_R);
            }
        };

        // Intersection: VehIntersection, Set PedIntersection
        Intersection mainIntersection_R = new Intersection(vehInt_R, pedInts_R, crosswalks_R);
        mainIntersection_R.generatePhaseSet();
        System.out.println(mainIntersection_R.getSetOfFeasiblePhaseGrouping());
        // mainIntersection_R.iterateTimeStep();





        // Network University_10th_Ave = new Network(nodes, links);
        // Network University_10th_Ave_ped = new Network(pedNodes, pedLinks);


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


    }
}