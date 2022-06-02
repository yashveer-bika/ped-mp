package ped;

import util.Angle;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TestSFPhases {
    File sf_nodes_f;
    File sf_links_f;
    String controllerType;
    Simulator sf_net_vehs;
    Simulator sf_net_peds;


    public TestSFPhases() {
        String dataPath = "data/SiouxFalls/";
        controllerType = "vehMP";
        sf_net_vehs = new Simulator(dataPath, false, controllerType);
//        sf_net_vehs = new Simulator(sf_nodes_f, sf_links_f, true, controllerType);
        // sf_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        // sf_net.runSim(60*60, 60*60*10, Params.tolerance_time);
    }

    public void testVehFeasiblePhases() {
        // TODO: read from conflictGroundTruth file and make sure we are consistent
        System.out.println("\nTESTING : SiouxFalls FEASIBLE VEHICLE PHASES\n");
        for (Intersection i : sf_net_vehs.getIntersectionSet()) {
            int id = i.getId();
//            System.out.println("Intersection " + id + ":");
//            System.out.println("\tTurning movements: " + i.getVehicleTurningMovements());
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
                default:
//                    System.out.println("\t???what is this intersection???");
//                    System.out.println("\tALL POSSIBLE PHASES: ");
//                    for (Phase p : i.getPossiblePhases()) {
//                        System.out.println("\t\t" + p);
//                    }
//                    System.out.println("\tGENERATED PHASES: ");
//                    for (Phase p : i.getFeasiblePhases()) {
//                        System.out.println("\t\t" + p);
//                    }

                    // System.out.println(i.getV2vConflicts());
//                    Set<TurningMovement> visited = new HashSet<>();
//                    for (TurningMovement t1: i.getV2vConflicts().keySet())
//                    {
//                        for (TurningMovement t2 : i.getV2vConflicts().get(t1).keySet())
//                        {
////                            System.out.println("TM1: " + t1 + ", TM2: " + t2 + ", " + i.getV2vConflicts().get(t1).get(t2));
//
////                            if (visited.contains(t2)) {
////                                continue;
////                            }
//
//                            String type = "";
//                            if (t1.getOutgoingLink().equals(t2.getOutgoingLink())) {
//                                type = "merge"; // conflict
////                                System.out.println(type);
//                                assert i.getV2vConflicts().get(t1).get(t2) == 0;
//                            } else if (t1.getIncomingLink().equals(t2.getIncomingLink())) {
//                                type = "diverge"; // allowed
////                                System.out.println(type);
//                                assert i.getV2vConflicts().get(t1).get(t2) == 1;
//                            } else if (t1.getOutgoingLink().getDest() == t2.getIncomingLink().getSource() && t2.getOutgoingLink().getDest() == t1.getIncomingLink().getSource()) {
//                                type = "complementaryLR"; // allowed
////                                System.out.println(type);
////                                System.out.println("TM1: " + t1 + ", TM2: " + t2 + ", " + i.getV2vConflicts().get(t1).get(t2) + ", type: " + type);
//                                assert i.getV2vConflicts().get(t1).get(t2) == 1;
//                            } else {
//                                System.out.println("TM1: " + t1 + ", TM2: " + t2 + ", " + i.getV2vConflicts().get(t1).get(t2));
////                                System.out.println("\tTM1 incoming angle: " + Angle.bound(Math.PI*1 + t1.getIncomingLink().getAngle()) );
////                                System.out.println("\tTM1 outgoing angle: " + Angle.bound(Math.PI*0 + t1.getOutgoingLink().getAngle()) );
////                                System.out.println("\tTM2 incoming angle: " + Angle.bound(Math.PI*1 + t2.getIncomingLink().getAngle()) );
////                                System.out.println("\tTM2 outgoing angle: " + Angle.bound(Math.PI*0 + t2.getOutgoingLink().getAngle()) );
//                            }
//                        }
//                        visited.add(t1);
//                    }

                    break;
            }
        }
    }

    public void testPedFeasiblePhases() {
        System.out.println("TESTING FEASIBLE PHASES ON PQ3, including Pedestrians");
    }
}
