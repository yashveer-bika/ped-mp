package ped;

import ilog.cplex.IloCplex;

import java.io.*;
import java.util.*;

public class Simulator extends Network {
//    private double simTime; // total network time (time in simulation)
    private double timeStepSize;
    private double toleranceTime; // pedestrian tolerance time
    private boolean ped;
    private String dataPath;
//    entrance node, destination node, time, demand_quantity
//    private Map<Node, Map<Node, Map<Double, Double>>> demand;
//    entrance node, start time, end time, demand_quantity
    private Map<Node, Map<Node, Double>> static_demand;

    public Simulator(String path, boolean ped, String controllerType, double demandScaleFactor) {
        this(
                new File(path + "nodes.txt"),
                new File(path + "links.txt"),
                new File(path + "turning_proportions.txt"),
                ped,
                controllerType
        );
        dataPath = path;


        File nodesFile = new File(path + "nodes.txt");
        File linksFile = new File(path + "links.txt");
        File demand_file = new File(path + "trips_static_od_demand.txt");
        File turn_props_file = new File(path + "turning_proportions.txt");

        loadStaticDemand(demand_file, demandScaleFactor);


    }

    public Simulator(String path, boolean ped, String controllerType) {
        this(
                new File(path + "nodes.txt"),
                new File(path + "links.txt"),
                new File(path + "turning_proportions.txt"),
                ped,
                controllerType
        );
        dataPath = path;


        File nodesFile = new File(path + "nodes.txt");
        File linksFile = new File(path + "links.txt");
        File demand_file = new File(path + "trips_static_od_demand.txt");
        File turn_props_file = new File(path + "turning_proportions.txt");

        loadStaticDemand(demand_file, Params.demandScaleFactor);


    }

    public Simulator(File nodesFile, File linksFile, File turnPropsFile, boolean ped, String controllerType) {
        super(nodesFile, linksFile, turnPropsFile, ped, controllerType);
        timeStepSize = 0;
        toleranceTime = 0;
        this.ped = ped;
        this.static_demand = new HashMap<>();
    }

    public String getDataPath() {
        return dataPath;
    }


    public void loadStaticDemand(File demandFile, double demandScaleFactor) {
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
                double demand = Double.parseDouble(demand_data[2]) * demandScaleFactor; // assume vehicles / hour
                demand = demand * Params.dt / 24 / 60 / 60; // converts from vehs / day to vehs / timestep

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


    public void addVehicleDemand(Map<Node, Map<Node, Double>> static_demand) {
        for (Node src : static_demand.keySet()) {
            for (Node dest : static_demand.get(src).keySet()) {
                // We assume num_vehs has unit vehs / Params.dt
                // This is determined when populating static_demand
                Double num_vehs = static_demand.get(src).get(dest);
                src.getEntryLink().addFlow(num_vehs);
            }
        }
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
//        System.out.println("Running controller on simulator level");
        for (Intersection i : getIntersectionSet()) {
//            System.out.println("Running controller on intersection " + i.getId());
            i.runController();
        }
        for (Intersection i : getIntersectionSet()) {
//            System.out.println("Moving vehicles on intersection " + i.getId());
             i.moveFlow();
        }
        for (Link l : getLinkSet()) {
            // in point queue, we move the entering flow into n, and the exiting flow out of n at each link
            l.update();
        }
        // goal, l.step(), then l.update()
    }

    // TODO: javadoc
    // true if stable, false if non-stable
    public boolean runSim() {
        // TODO: define a k for slope

        PrintStream ps_console = System.out;

        try {
            System.setOut(new PrintStream(new File(Params.getSim_output_filepath())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("STARTING NEW SIM");


        long startTime = System.nanoTime();

        while (Params.time <= Params.DURATION) {
            calculateTT();

//                myWriter.write("Sim Time: " + Params.time);
//                myWriter.write("\t network-level occupancy : " + getOccupancy());
//                myWriter.write("\t avg link tt : " + getAvgLinkTravelTime());
//                myWriter.write("\t avg delay : " + getAvgDelay());


            System.out.println("Sim Time: " + Params.time);
            System.out.println("\tnetwork-level occupancy: " + getOccupancy());
            System.out.println("\tavg link tt: " + getAvgLinkTravelTime());
            System.out.println("\tavg delay: " + getAvgDelay());

            addVehicleDemand(static_demand);

//            // print links
//            for (Link l : getLinkSet()) {
//                System.out.println("Link ID: " + l.getId() + ", vehsOnLink: " + l.getOccupancy());
//            }
            runController();
            updateTime();
        }
        long endTime = System.nanoTime();
        long elapsedTime = (endTime-startTime);

        System.out.println("\truntime: " + elapsedTime);
        System.out.println("\truntime / iteration: " + (elapsedTime / Params.n_steps * Math.pow(10, -9)));


        System.setOut(ps_console);
        System.out.println("Console again !!");

        return false;
    }


    public double getAvgDelay() {

        double sumOfAvgDelay = 0.0;
        int count = 0;
        for (Link l : getLinkSet()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            }
            else {
                count++;
                sumOfAvgDelay += l.getAvgDelay();
            }
        }
        return sumOfAvgDelay / count;
    }

    public void calculateTT() {
        for (Link l : getLinkSet()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            } else {
                l.logOccupancyTime();
            }
        }

        for (Link l : getLinkSet()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            } else {
                l.calculateTravelTime();
            }
        }
    }

    public double getAvgLinkTravelTime() {

        double sumOfAvgLinkTTs = 0.0;
        int count = 0;
        for (Link l : getLinkSet()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            }
            else {
                count++;
                sumOfAvgLinkTTs += l.getAvgTT();
            }
        }
        return sumOfAvgLinkTTs / count;
    }

    public void updateTime() {
        // send the time update to the Network components
        // turningMovement
        // controller

//        simTime += Params.dt;
        Params.time += Params.dt;

        for (Intersection intersection : this.getIntersectionSet()) {
            intersection.updateTime(Params.time);
        }

    }



    public double getOccupancy() {
        double totalOccupancy = 0;
        for (Link l : getLinkSet()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            }
            totalOccupancy += l.getOccupancy();
        }
        return totalOccupancy;
    }


    /*
    Purpose:
        reset of the links and time, so I can start a fresh simulation on the same network with the same demand file
     */
    public void reset() {
        for (Link l : getLinkSet()) {
            l.reset();
        }
        Params.time = 0;
    }


    public void findMaximalDemandScaleFactor(double min_scale, double max_scale) {
        // TODO: set up binary search to find the MDSF
        int maxIter = 10;
        double min = min_scale;
        double max = max_scale;
        min = 0.024 ;
        max = 0.24 ;

        while (true) {

            reset();
        }
    }

}

