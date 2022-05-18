package ped;

import java.io.File;

public class TestSFPhases {
    File sf_nodes_f;
    File sf_links_f;
    String controllerType;
    Simulator sf_net_vehs;
    Simulator sf_net_peds;


    public TestSFPhases() {
        sf_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
        sf_links_f = new File("data/SiouxFalls/network/links.txt");
        controllerType = "vehMP";
        // TODO: fix the veh-2-veh conflict solution on intersection 5 on PQ3
        sf_net_vehs = new Simulator(sf_nodes_f, sf_links_f, false, controllerType);
//        sf_net_vehs = new Simulator(sf_nodes_f, sf_links_f, true, controllerType);
        // sf_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        // sf_net.runSim(60*60, 60*60*10, Params.tolerance_time);
    }

    public void testVehFeasiblePhases() {
        System.out.println("\nTESTING : SiouxFalls FEASIBLE PHASES\n");
        for (Intersection i : sf_net_vehs.getIntersectionSet()) {
            int id = i.getId();
            System.out.println("Intersection " + id + ":");
//            System.out.println("\tTurning movements: " + i.getAllTurningMovements());
            switch (id) {
//                case 1:
////                    System.out.println("\tEXPECTED PHASES:  []");
////                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    System.out.println("\tGENERATED PHASES: ");
//                    for (Phase p : i.getFeasiblePhases()) {
//                        System.out.println("\t\t" + p);
//                    }
//                    break;
//                case 2:
////                    System.out.println("\tEXPECTED PHASES:  [[Turn124, Turn123]] (can also have [Turn124], [Turn123])");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 3:
////                    System.out.println("\tEXPECTED PHASES:  [[Turn234, Turn235]] (can also have [Turn235], [Turn234])");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 4:
////                    System.out.println("\tEXPECTED PHASES:  [[Turn345], [Turn245]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 5:
////                    System.out.println("\tEXPECTED PHASES:  " + "[[Turn356], [Turn456]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 6:
////                    System.out.println("\tEXPECTED PHASES:  [[Turn567]]");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
//                case 7:
////                    System.out.println("\tEXPECTED PHASES:  []");
//                    System.out.println("\tGENERATED PHASES: " + i.getFeasiblePhases());
//                    break;
                case 1:
//                    System.out.println("\t???what is this intersection???");
                    System.out.println("\tALL POSSIBLE PHASES: ");
                    for (Phase p : i.getPossiblePhases()) {
                        System.out.println("\t\t" + p);
                    }
                    System.out.println("\tGENERATED PHASES: ");
                    for (Phase p : i.getFeasiblePhases()) {
                        System.out.println("\t\t" + p);
                    }
                    break;
            }
        }
    }

    public void testPedFeasiblePhases() {
        System.out.println("TESTING FEASIBLE PHASES ON PQ3, including Pedestrians");
    }
}
