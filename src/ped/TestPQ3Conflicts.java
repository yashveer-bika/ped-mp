package ped;

import java.io.File;

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


//        pq3_net_peds = new Simulator(pq3_nodes_f, pq3_links_f, true, controllerType);
        // pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        // pq3_net.runSim(60*60, 60*60*10, Params.tolerance_time);
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
        double timeStepSize = 60*60;
        double totalRunTime = 60*60*2;
        double toleranceTime = 0;
        pq3_net_vehs.runSim( timeStepSize,  totalRunTime, toleranceTime);

        System.out.println("\nFINISHED TESTING CONTROLLER\n");

    }

    public void testPedFeasiblePhases() {
        System.out.println("\nTESTING : PQ3 FEASIBLE PHASES w/ Ped\n");
        for (Intersection i : pq3_net_peds.getIntersectionSet()) {
            int id = i.getId();
            System.out.println("Intersection " + id);
            System.out.println("\tped nodes: " + i.getPedNodes());
            // TODO: make sure that the turning movements include the pedestrian turning movements!!
            System.out.println("\tTurning movements: " + i.getAllTurningMovements());
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
    }
}
