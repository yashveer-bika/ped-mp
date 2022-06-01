package ped;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Simulator extends Network {
    private double simTime; // total network time (time in simulation)
    private double timeStepSize;
    private double toleranceTime; // pedestrian tolerance time
    private boolean ped;
//    entrance node, destination node, time, demand_quantity
//    private Map<Node, Map<Node, Map<Double, Double>>> demand;
//    entrance node, start time, end time, demand_quantity
    private Map<Node, Map<Node, Double>> static_demand;

    public Simulator(String path, boolean ped, String controllerType) {
        this(
                new File(path + "nodes.txt"),
                new File(path + "links.txt"),
                new File(path + "turning_proportions.txt"),
                ped,
                controllerType
        );


        File nodesFile = new File(path + "nodes.txt");
        File linksFile = new File(path + "links.txt");
        File demand_file = new File(path + "trips_static_od_demand.txt");
        File turn_props_file = new File(path + "turning_proportions.txt");

        loadStaticDemand(demand_file);


    }

    public Simulator(File nodesFile, File linksFile, File turnPropsFile, boolean ped, String controllerType) {
        super(nodesFile, linksFile, turnPropsFile, ped, controllerType);
        simTime = 0;
        timeStepSize = 0;
        toleranceTime = 0;
        this.ped = ped;
        this.static_demand = new HashMap<>();
    }

    public void loadStaticDemand(File demandFile) {
        try {
            String[] header = {};
            Scanner myReader = new Scanner(demandFile);
            ArrayList<String[]> demand_rows = new ArrayList();

            // read nodes from demandFile
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // at header
                if (data.charAt(0) == 'o') {
//                    System.out.println("HEADER");
                    header = data.split("\t");
                }
                // normal node
                else {
                    String[] node_data = data.split("\t");

                    demand_rows.add(node_data);
                }
            }
            myReader.close();


            Set<Integer> nodeIds = new HashSet<>();
            for (String[] demand_data : demand_rows) {
                int origin_id = Integer.parseInt(demand_data[0]);
                int dest_id = Integer.parseInt(demand_data[1]);
                nodeIds.add(origin_id);
                nodeIds.add(dest_id);
            }

            // initialize the static_demand
            for (Integer src : nodeIds) {
                Node src_n = getNode(src);
                if (src_n == null) {
                    continue;
                }
                static_demand.put(getNode(src), new HashMap<>());
                for (Integer dest : nodeIds) {
                    Node dest_n = getNode(dest);
                    if (dest_n == null) {
                        continue;
                    }
                    static_demand.get(getNode(src)).put(getNode(dest), 0.0);
                }
            }

            // read in demand
            for (String[] demand_data : demand_rows) {
//                System.out.println(demand_data[0]);
                int origin_id = Integer.parseInt(demand_data[0]);
                int dest_id = Integer.parseInt(demand_data[1]);
                double demand = Double.parseDouble(demand_data[2]); // assume vehicles / hour

                Node src = getNode(origin_id);
                Node dest = getNode(dest_id);
                if (src == null || dest == null) {
                    continue;
                } else {
                    static_demand.get(src).put(dest, demand);
                }
            }
//            System.out.println(static_demand);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public void addVehicleDemand(Map<Node, Map<Node, Double>> static_demand, double timeStepSize) {

//        // if (simTime == 0) {
//            for (Node src : static_demand.keySet()) {
//                for (Node dest : static_demand.get(src).keySet()) {
//                    // TODO: verify the units of num_vehs
//                    Double num_vehs = static_demand.get(src).get(dest); // in vehs / hr
//                    num_vehs = num_vehs * timeStepSize / 60 / 60;
////                    for (int i = 0; i < num_vehs; i++) {
////                        createVehicle(src, dest);
////                    }
//                }
//            }
//        // }

        // if (simTime == 0) {
            for (Node src : static_demand.keySet()) {
                for (Node dest : static_demand.get(src).keySet()) {
                    // TODO: verify the units of num_vehs
                    Double num_vehs = static_demand.get(src).get(dest); // in vehs / hr
                    num_vehs = num_vehs * timeStepSize / 60 / 60;
//                    for (int i = 0; i < num_vehs; i++) {
//                        createVehicle(src, dest);
//                    }
                    // TODO: test the line below
                    src.getEntryLink().addFlow(num_vehs);
                }
            }
            // }
    }

//    public void moveVehsToLinks() {
//        System.out.println("calling moveVehsToLinks");
//
//        // look at every node
//        for (Node n : this.getNodeSet()) {
//            // and every vehicle in each node
////            List<Vehicle> vehs = n.getVehicles();
////            System.out.println("\tVehicle size: " + vehs.size());
////            for (Vehicle v : n.getVehicles()) {
////                // and get the next node in each of these vehicles path
////                Node next_node = v.getNextNode(1);
////                System.out.println("\tvehicle: " + v);
//////                System.out.println("vpath: " + v.getPath());
////
////                // and the associated link to reach this next location
////                Link l = n.getOutgoingLink(next_node);
////                // move vehicles onto this link
//////                int prev_size = l.getVehs().size();
////                System.out.println("\tprev: " + l.getVehs());
////                l.addVehicle(v);
//////                int updated_size = l.getVehs().size();
////                System.out.println("\tupdated: " + l.getVehs());
//////                assert updated_size == prev_size + 1;
////            }
//            // n.clearVehicles();
//            n.moveVehsToLinks();
//        }
//    }

    // run controller on each intersection in this network
    // this means we calculate the best phase [set of (s_ij)'s] and flow values [(y_ij)'s]
    public void runController() {
        System.out.println("Running controller on simulator level");
        for (Intersection i : getIntersectionSet()) {
            System.out.println("Running controller on intersection " + i.getId());
            i.runController();
        }
        for (Intersection i : getIntersectionSet()) {
            System.out.println("Moving vehicles on intersection " + i.getId());
            // TODO: move vehicles based on flow calculation
             i.moveFlow();
        }
        for (Link l : getLinkSet()) {
            // in point queue, we move the entering flow into n, and the exiting flow out of n at each link
            l.update();
        }
    }

    public void runSim(double timeStepSize, double totalRunTime, double toleranceTime) {
        this.toleranceTime = toleranceTime;
        this.timeStepSize = timeStepSize;

        while (simTime <= totalRunTime) {
            System.out.println("Sim Time: " + simTime);
            addVehicleDemand(static_demand, timeStepSize);
            // print links
            for (Link l : getLinkSet()) {
                System.out.println("Link ID: " + l.getId() + ", vehsOnLink: " + l.getOccupancy());
            }
            // load demand onto links
            runController();
            updateTime();
            System.out.println();
        }
    }


    public void updateTime() {
        // send the time update to the Network components
        // turningMovement
        // controller

        simTime += timeStepSize;

        for (Intersection intersection : this.getIntersectionSet()) {
            intersection.updateTime(simTime);
        }

    }
}

