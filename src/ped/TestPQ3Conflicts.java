package ped;

import java.io.File;

public class TestPQ3Conflicts {
    File pq3_nodes_f;
    File pq3_links_f;
    String controllerType;
    Simulator pq3_net_vehs;
    Simulator pq3_net_peds;


    public TestPQ3Conflicts() {
        pq3_nodes_f = new File("data/PQ3/nodes.txt");
        pq3_links_f = new File("data/PQ3/links.txt");
        controllerType = "vehMP";
        // TODO: fix the veh-2-veh conflict solution on intersection 5 on PQ3
        pq3_net_vehs = new Simulator(pq3_nodes_f, pq3_links_f, false, controllerType);
//        pq3_net_vehs = new Simulator(pq3_nodes_f, pq3_links_f, true, controllerType);
        // pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        // pq3_net.runSim(60*60, 60*60*10, Params.tolerance_time);
    }

    public void testVehFeasiblePhases() {
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
    }

    public void testPedFeasiblePhases() {
        System.out.println("TESTING FEASIBLE PHASES ON PQ3, including Pedestrians");
    }
}
