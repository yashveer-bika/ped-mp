package ped;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class TestPQ3Conflicts {
    File pq3_nodes_f;
    File pq3_links_f;
    String controllerType;
    Simulator pq3_net_vehs;
    Simulator pq3_net_peds;

    File pq3_way2_nodes_f;
    File pq3_way2_links_f;
    Simulator pq3_net_way2_vehs;
    Simulator pq3_net_way2_peds;


    public TestPQ3Conflicts() {
//        pq3_nodes_f = new File("data/PQ3/nodes.txt");
//        pq3_links_f = new File("data/PQ3/links.txt");
//        controllerType = "vehMP";
        pq3_net_vehs = new Simulator("data/PQ3/", false, "vehMP");

//        pq3_way2_nodes_f = new File("data/PQ3way2/nodes.txt");
//        pq3_way2_links_f = new File("data/PQ3way2/links.txt");
//        pq3_net_way2_vehs = new Simulator(pq3_way2_nodes_f, pq3_way2_links_f, false, "vehMP");
        pq3_net_way2_vehs = new Simulator("data/PQ3way2/", false, "vehMP");


//        pq3_net_peds = new Simulator("data/PQ3/", true, "vehMP");
        // pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        // pq3_net.runSim(60*60, 60*60*10, Params.tolerance_time);
    }

//    public void printAllLocations() {
//        System.out.format("%-20s %-20s %-20s %n", "id", "x", "y");
//        for (int id : pq3_net_peds.getNodes().keySet()) {
//            Node n = pq3_net_peds.getNodes().get(id);
//            System.out.format("%-20s %-20s %-20s %n", id, n.getX(), n.getY());
//        }
//    }

    public void writeOutPedNetworkData() {
        System.out.println("\nwriting out fake pedestrian data\n");

        // write out pedNodes
        try {
            FileWriter myWriter = new FileWriter(pq3_net_peds.getDataPath() + "nodesWPeds.txt");
//            myWriter.write("Files in Java might be tricky, but it is fun enough!");
//            myWriter.close();

            String format = "%s\t%s\t%s\t%s\t%s\t%n";
            String s = String.format(format, "id",  "type", "longitude", "latitude", "elevation");
            myWriter.write(s);
            for (Node n : pq3_net_peds.getNodes().values()) {
                // assume elevation=0
                s = String.format(format, n.getId(),  n.getType(), n.getX(), n.getY(), 0);
                myWriter.write(s);
//            System.out.println("node: " + n.getId() + n.getType() + n.getX() + n.getY() + "0");
            }

            myWriter.close();

            System.out.println("Successfully wrote to the pedNodes file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

//        System.out.format("%-20s %-20s %-20s %-20s %-20s %n", "id",  "type", "longitude", "latitude", "elevation");
//        for (Node n : pq3_net_peds.getPedNodes()) {
//            // assume elevation=0
//            System.out.format("%-20s %-20s %-20s %-20s %-20s %n", n.getId(),  n.getType(), n.getX(), n.getY(), 0);
//
////            System.out.println("node: " + n.getId() + n.getType() + n.getX() + n.getY() + "0");
//        }

//        System.out.println("LINKS");
////        String format = "%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %n";
//        System.out.format(format, "id",  "type", "source", "dest", "length (mi)", "ffspd (mph)", "capacity(veh/hr)", "num_lanes");
//        for (Link l : pq3_net_peds.getLinkSet()) {
//            System.out.format(format, l.getId(),  l.getType(), l.getSource().getId(), l.getDest().getId(), l.getLength(), l.getFFSpeed(), l.getCapacity(), l.getNumLanes());
//
////            System.out.println("node: " + n.getId() + n.getType() + n.getX() + n.getY() + "0");
//        }

        // write out allLinks
        try {
            FileWriter myWriter = new FileWriter(pq3_net_peds.getDataPath() + "linksWPeds.txt");
//            myWriter.write("Files in Java might be tricky, but it is fun enough!");
//            myWriter.close();

            String format = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s%n";
            String s = String.format(format, "id",  "type", "source", "dest", "length (mi)", "ffspd (mph)", "capacity(veh/hr)", "num_lanes");
            myWriter.write(s);
            for (Link l : pq3_net_peds.getLinkSet()) {
                s = String.format(format, l.getId(),  l.getType(), l.getSource().getId(), l.getDest().getId(), l.getLength(), l.getFFSpeed(), l.getCapacity(), l.getNumLanes());
                myWriter.write(s);
//            System.out.println("node: " + n.getId() + n.getType() + n.getX() + n.getY() + "0");
            }

            myWriter.close();

            System.out.println("Successfully wrote to the linksWPeds file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


    public void writeOutPedTurningProportions() {
        // TODO
        // assume that the link and node files already exist, with names linksWPeds.txt, nodesWPeds.txt??
        System.out.println("\nWRITING OUT PED PQ3 TURNING PROPORTIONS\n");


        System.out.println("LINKS");
        for (Link l : pq3_net_peds.getLinkSet()) {
            System.out.println("\t" + l.getId());
        }

        try {
            FileWriter myWriter = new FileWriter(pq3_net_peds.getDataPath() + "pedTurningProportions.txt");

            String format = "%s\t%s\t%s%n";
            String s = String.format(format, "src",  "dest", "turning proportions");
            myWriter.write(s);

            double turningProportion;
            int srcId;
            int destId;
            for (Link l1 : pq3_net_peds.getLinkSet()) {
                Node start = l1.getSource();
                if ( (l1.getSource() instanceof PedNode || l1.getDest() instanceof PedNode) && !(l1 instanceof ExitLink) ) {
                    boolean isEntry = (l1 instanceof EntryLink);
                    Node mid = l1.getDest();
                    srcId = l1.getId();
                    double count = 1; // each none- dummy node has an exit link

                    if (isEntry) {
                        count -= 1;
                    }
                    for (Link j : mid.getOutgoingLinks()) {
                        if (!j.getDest().equals(start)) {
                            count += 1;
                        }
                    }

                    for (Link l2 : pq3_net_peds.getLinkSet()) {
                        if (l2.getSource() instanceof PedNode || l2.getDest() instanceof PedNode && !(l2 instanceof EntryLink)) {
                            destId = l2.getId();
                            // skip u-turns
                            if (start.equals(l2.getDest())) {
                                turningProportion = 0;
                            }
                            // if the two links connect, set equally spread turning proportions
                            else if (mid.equals(l2.getSource())) {
                                turningProportion = 1 / count;
//                                System.out.println("adding turn prop");
                            } else {
                                turningProportion = 0;
                            }

                            if (srcId == 904666902) {
                                System.out.println("904666902 outgoing links");
                                System.out.println("\t" + mid.getOutgoingLinks());
                            }

                            s = String.format(format, srcId,  destId, turningProportion);
                            myWriter.write(s);
                        }
                    }
//                    // write out to exit link
//                    System.out.println("mid ID: " + mid.getId());
//                    System.out.println("\tcount: " + count);
//                    destId = mid.getExitLink().getId();
//                    turningProportion = 1 / count;
//                    s = String.format(format, srcId,  destId, turningProportion);
//                    myWriter.write(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("\nFINISHED WRITING OUT PED PQ3 TURNING PROPORTIONS\n");
    }

    public void writeOutPedDemand() {
        // TODO

    }

    public void testV2VConflicts() {
        System.out.println("\nTESTING : v2v PQ3 CONFLICTS\n");

        System.out.format("%-20s %-20s %-20s %n", "tm1", "tm2", "conflict/not (0/1)");
        for (Intersection i : pq3_net_vehs.getIntersectionSet()) {
            Map<TurningMovement, Map<TurningMovement, Integer>> conflictMap = i.getV2vConflicts();
            for (TurningMovement vehTM1 : conflictMap.keySet()) {
                for (TurningMovement vehTM2 : conflictMap.get(vehTM1).keySet()) {
//                    System.out.println("tm1: " + vehTM1 + "    " + "tm2: " + vehTM2 + " ---> " + conflictMap.get(vehTM1).get(vehTM2));
                    System.out.format("%-25s %-25s %-25s %n", vehTM1, vehTM2, conflictMap.get(vehTM1).get(vehTM2));
                }
            }
        }

        System.out.println("\nFINISHED TESTING : v2v PQ3 CONFLICTS\n");

    }

    public void testV2PConflicts() {
        System.out.println("\nTESTING : v2p PQ3 CONFLICTS\n");

//        System.out.println("all links: " + pq3_net_peds.getLinkSet());
//
//
//        System.out.println("all nodes: " + pq3_net_peds.getNodes().keySet());
//        System.out.println("ped nodes: " + pq3_net_peds.getPedNodes());
//
//        for (Intersection i : pq3_net_peds.getIntersectionSet()) {
//            for (PedIntersection pi : i.getPedInts()) {
//                System.out.println("\tIntersection " + i.getId());
//                System.out.println("\tPed Int " + pi.getId());
//                System.out.println("\t\tNeighbors: " + pi.getNeighbors());
//                System.out.println("\t\tAll: " + pi.getAllLinks());
//            }
//        }

        System.out.format("%-20s %-20s %-20s %n", "tm1", "tm2", "conflict/not (0/1)");
        for (Intersection i : pq3_net_peds.getIntersectionSet()) {
            Map<TurningMovement, Map<TurningMovement, Integer>> conflictMap = i.getVehPedConflicts();
            for (TurningMovement vehTM1 : conflictMap.keySet()) {
                for (TurningMovement pedTM : conflictMap.get(vehTM1).keySet()) {
//                    System.out.println("tm1: " + vehTM1 + "    " + "tm2: " + vehTM2 + " ---> " + conflictMap.get(vehTM1).get(vehTM2));
                    System.out.format("%-20s %-20s %-20s %n", vehTM1, pedTM, conflictMap.get(vehTM1).get(pedTM));
                }
            }
        }

        System.out.println("\nFINISHED TESTING : v2p PQ3 CONFLICTS\n");

    }

    public void testVehFeasiblePhases() {
        // TODO: read the ground truth file and write tests in expected/generated format
        System.out.println("\nTESTING : PQ3 FEASIBLE PHASES\n");
        for (Intersection i : pq3_net_vehs.getIntersectionSet()) {
            int id = i.getId();
            System.out.println("Intersection " + id);
//            System.out.println("\tTurning movements: " + i.getAllTurningMovements());
            switch (id) {
                case 1:
                    System.out.println("\tEXPECTED PHASES:  []");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 2:
                    System.out.println("\tEXPECTED PHASES:  [[Turn:1,2,4, Turn:1,2,3]] (can also have [Turn:1,2,4], [Turn:1,2,3])");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 3:
                    System.out.println("\tEXPECTED PHASES:  [[Turn:2,3,4, Turn:2,3,5]] (can also have [Turn:2,3,5], [Turn:2,3,4])");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 4:
                    System.out.println("\tEXPECTED PHASES:  [[Turn:3,4,5], [Turn:2,4,5]]");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 5:
                    System.out.println("\tEXPECTED PHASES:  " + "[[Turn:3,5,6], [Turn:4,5,6]]");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 6:
                    System.out.println("\tEXPECTED PHASES:  [[Turn:5,6, 7]]");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                case 7:
                    System.out.println("\tEXPECTED PHASES:  []");
                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
                    break;
                default:
                    System.out.println("\t???what is this intersection???");
                    break;
            }
        }

        System.out.println("\nFINISHED TESTING : PQ3 FEASIBLE PHASES\n");
    }

    public void testConflictRegions() {
        // TODO: read the ground truth file and write tests in expected/generated format
        System.out.println("\nTESTING CONFLICT REGION GENERATION\n");
        for (Intersection i : pq3_net_way2_vehs.getIntersectionSet()) {
            System.out.println("intersection " + i.getId());
//            System.out.println("turning movements: " + i.getAllTurningMovements());
//            System.out.println("\tConflict regions: " + i.getConflictRegions());
            int size = i.getConflictRegions().size();
            for (int j=0 ; j<size; j++) {
                ConflictRegion cr = i.getConflictRegions().get(j);
                System.out.println("\t" + j);
                System.out.println("\t\t" + cr.getTms());
                System.out.println("\t\tcr capacity: " + cr.getCapacity());
            }
        }
        System.out.println("\nFINISHED TESTING CONFLICT REGION GENERATION\n");
    }

    public void testTurningProportions() {
        // Purpose: Make sure that we load the turning proportions correctly
        // TODO: read the ground truth file and write tests in expected/generated format
        System.out.println("\nTESTING TURNING PROPORTION LOADING\n");
        for (Intersection i : pq3_net_way2_vehs.getIntersectionSet()) {
//            System.out.println("intersection " + i.getId());
//            System.out.println("turning movements: " + i.getAllTurningMovements());
//            System.out.println("\tConflict regions: " + i.getConflictRegions());
            for (TurningMovement tm : i.getAllTurningMovements()) {
//                System.out.println("turning movement: " + tm);
//                System.out.println("turning proportion: " + tm.getTurningProportion() );
            }
        }
        System.out.println("\nFINISHED TESTING TURNING PROPORTION LOADING\n");
    }

    public void testController() {
        System.out.println("\nTESTING CONTROLLER\n");
        pq3_net_vehs.runSim(false);

        System.out.println("\nFINISHED TESTING CONTROLLER\n");

    }

    public void testVehMPControllerWPeds() {
        System.out.println("\nTESTING CONTROLLER\n");
        pq3_net_peds.runSim(false);

        System.out.println("\nFINISHED TESTING CONTROLLER\n");

    }



//    public void testPedFeasiblePhases() {
//        System.out.println("\nTESTING : PQ3 FEASIBLE PHASES w/ Ped\n");
//        for (Intersection i : pq3_net_peds.getIntersectionSet()) {
//            int id = i.getId();
//            System.out.println("Intersection " + id);
//            System.out.println("\tped nodes: " + i.getPedNodes());
//            // TODO: make sure that the turning movements include the pedestrian turning movements!!
//            System.out.println("\tTurning movements: " + i.getAllTurningMovements());
//            switch (id) {
//                case 1:
//                    System.out.println("\tEXPECTED PHASES:  []");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 2:
//                    System.out.println("\tEXPECTED PHASES:  [[Turn:1,2,4, Turn:1,2,3]] (can also have [Turn:1,2,4], [Turn:1,2,3])");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 3:
//                    System.out.println("\tEXPECTED PHASES:  [[Turn:2,3,4, Turn:2,3,5]] (can also have [Turn:2,3,5], [Turn:2,3,4])");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 4:
//                    System.out.println("\tEXPECTED PHASES:  [[Turn:3,4,5], [Turn:2,4,5]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 5:
//                    System.out.println("\tEXPECTED PHASES:  " + "[[Turn:3,5,6], [Turn:4,5,6]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 6:
//                    System.out.println("\tEXPECTED PHASES:  [[Turn:5,6, 7]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 7:
//                    System.out.println("\tEXPECTED PHASES:  []");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                default:
//                    System.out.println("\t???what is this intersection???");
//                    break;
//            }
//        }
//    }
}
