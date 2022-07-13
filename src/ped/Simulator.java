package ped;

import ilog.cplex.IloCplex;

import java.io.*;
import java.util.*;
import java.util.function.DoubleBinaryOperator;

public class Simulator extends Network {
    private boolean ped;
    private String dataPath;
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
        dataPath = path;


        File nodesFile = new File(path + "nodes.txt");
        File linksFile = new File(path + "links.txt");
        File turn_props_file = new File(path + "turning_proportions.txt");
        File demand_file = new File(path + "trips_static_od_demand.txt");
        loadStaticDemand(demand_file);


    }

    public Simulator(File nodesFile, File linksFile, File turnPropsFile, boolean ped, String controllerType) {
        super(nodesFile, linksFile, turnPropsFile, ped, controllerType);
        this.ped = ped;
        this.static_demand = new HashMap<>();
    }

    public String getDataPath() {
        return dataPath;
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
                double demand = Double.parseDouble(demand_data[2]) ; // assume vehicles / day
                demand = demand * Params.dt / 24 / 60 / 60; // converts from vehs / day to vehs / timestep

                Node src = getNode(origin_id);
                Node dest = getNode(dest_id);
                if (src == null || dest == null) {
                    continue;
                } else {
                    static_demand.get(src).put(dest, demand * Params.demandScaleFactor);
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


    public void addPedestrianDemand() {
        for (PedNode pi : getPedNodes()) {
            pi.getEntryLink().addFlow(1);
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
        for (Intersection i : getIntersectionSet()) {
//            System.out.println("Running controller on intersection " + i.getId());
            for (TurningMovement tm : i.getPedestrianTurningMovements()) {
                tm.update();
            }
        }
        // goal, l.step(), then l.update()
    }

    public class LimitedSizeQueue<K> extends ArrayList<K> {

        private int maxSize;

        public LimitedSizeQueue(int size){
            this.maxSize = size;
        }

        public boolean add(K k){
            boolean r = super.add(k);
            if (size() > maxSize){
                removeRange(0, size() - maxSize);
            }
            return r;
        }

        public K getYoungest() {
            return get(size() - 1);
        }

        public K getOldest() {
            return get(0);
        }
    }

    // TODO: javadoc
    // true if stable, false if non-stable
    public boolean runSim(boolean kill) {

        File demand_file = new File(dataPath + "trips_static_od_demand.txt");
        loadStaticDemand(demand_file);

        // TODO: define a k for slope
        double k = Math.pow(10, -4);


        // keep 300 most recent network-level occupancy
        int queue_size = 300;
        List<Double> timeL = new ArrayList<>();
        for (double i = 0; Double.compare(i, queue_size) < 0; i++) {
            timeL.add(i * Params.dt);
        }
        LimitedSizeQueue<Double> occupancies = new LimitedSizeQueue<Double>(queue_size);

        PrintStream ps_console = System.out;
        PrintStream newPs = null;
        try {
            newPs = new PrintStream(new File(Params.getSim_output_filepath()));
            System.setOut(newPs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("STARTING NEW SIM");


        long startTime = System.nanoTime();


        boolean stable = false;

        while (Params.time <= Params.DURATION) {
            calculateTT();

//                myWriter.write("Sim Time: " + Params.time);
//                myWriter.write("\t network-level occupancy : " + getOccupancy());
//                myWriter.write("\t avg link tt : " + getAvgLinkTravelTime());
//                myWriter.write("\t avg delay : " + getAvgDelay());

            System.out.println("Sim Time: " + Params.time);
            System.out.println("\tnetwork-level occupancy: " + getVehicleOccupancy());
            System.out.println("\tnetwork-level ped occupancy: " + getPedOccupancy());
            System.out.println("\tavg link tt: " + getAvgVehicleLinkTravelTime());
            System.out.println("\tavg delay: " + getAvgVehicleDelay());
            System.out.println("\tavg ped link tt: " + getAvgPedLinkTravelTime());
            System.out.println("\tavg ped delay: " + getAvgPedDelay());

            occupancies.add(getVehicleOccupancy());

            addVehicleDemand(static_demand);
            addPedestrianDemand();
            runController();
            updateTime();

            // TODO: print occupancy
//            printOccupancy();

            // STABILITY CONDITION
            if (kill) {
                if (Params.time / Params.dt > queue_size + 1) {
                    // check if slope is greater than k
                    double data_slope = approximateSlope(timeL, occupancies);
//                System.out.println("\tSlope: " + data_slope);
                    if (data_slope < k) {
//                    System.out.println("occupancies: " + occupancies);
                        System.setOut(ps_console);
                        newPs.close();
                        return true;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        long elapsedTime = (endTime-startTime);

        // STABILITY CONDITION
        if (Params.time / Params.dt > queue_size + 1) {
            // check if slope is greater than k
            double data_slope = approximateSlope(timeL, occupancies);
//                System.out.println("\tSlope: " + data_slope);
            if (data_slope < k) {
//                    System.out.println("occupancies: " + occupancies);
                stable = true;
            }
        }

        System.out.println("\truntime: " + elapsedTime);
        System.out.println("\truntime / iteration: " + (elapsedTime / Params.n_steps * Math.pow(10, -9)));
        System.setOut(ps_console);
        newPs.close();
        System.out.println("Console again !!");
        System.out.println("occupancies: " + occupancies);
        return stable;
    }


    public double getAvgVehicleDelay() {

        double sumOfAvgDelay = 0.0;
        int count = 0;
        for (Link l : getVehicleLinks()) {
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

    public double getAvgPedDelay() {

        double sumOfAvgDelay = 0.0;
        int count = 0;
        for (Link l : getPedLinks()) {
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

    public double getAvgVehicleLinkTravelTime() {

        double sumOfAvgLinkTTs = 0.0;
        int count = 0;
        for (Link l : getVehicleLinks()) {
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

    public double getAvgPedLinkTravelTime() {

        double sumOfAvgLinkTTs = 0.0;
        int count = 0;
        for (Link l : getPedLinks()) {
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

    }


    public void printOccupancy() {
        for (Link l : getVehicleLinks()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            }
            System.out.println("\t\tLink " + l + "  :  " + l.getOccupancy());
        }
    }


    public double getVehicleOccupancy() {
        double totalOccupancy = 0;
        // TODO: get vehicle link set
        for (Link l : getVehicleLinks()) {
            if (l instanceof EntryLink || l instanceof ExitLink) {
                continue;
            }
            totalOccupancy += l.getOccupancy();
        }
        return totalOccupancy;
    }


    public double getPedOccupancy() {
        double totalOccupancy = 0;
        // TODO: get ped linkset
        System.out.println("Pedestrian links: " + getPedLinks());
        for (Link l : getPedLinks()) {
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
        for (Intersection i : getIntersectionSet()) {
            for (TurningMovement tm : i.getAllTurningMovements()) {
                tm.reset();
            }
        }
        Params.time = 0;
    }

    /*

    function binary_search(A, n, T) is
    L := 0
    R := n − 1
    while L ≤ R do
        m := floor((L + R) / 2)
        if A[m] < T then
            L := m + 1
        else if A[m] > T then
            R := m − 1
        else:
            return m
    return unsuccessful

     */

    // function to calculate m and c that best fit points
    // represented by x[] and y[]
    static double approximateSlope(List<Double> x, List<Double> y)
    {
        int n = x.size();
        double m, c, sum_x = 0, sum_y = 0,
                sum_xy = 0, sum_x2 = 0;
        for (int i = 0; i < n; i++) {
            sum_x += x.get(i);
            sum_y += y.get(i);
            sum_xy += x.get(i) * y.get(i);
            sum_x2 += Math.pow(x.get(i), 2);
        }

        m = (n * sum_xy - sum_x * sum_y) / (n * sum_x2 - Math.pow(sum_x, 2));
        c = (sum_y - m * sum_x) / n;

//        System.out.println("m = " + m);
//        System.out.println("c = " + c);
        return m;
    }

    public double findMaximalDemandScaleFactor(double min, double max) {
        // TODO: set up binary search to find the MDSF

//        // first find a maximum that is unstable
//        boolean stable = true;
//        while (stable) {
//            System.out.println("\t\tmax demand scale: " + max);
//            Params.demandScaleFactor = max;
//            stable = runSim(false);
//            reset();
//            max *= 2;
//        }

//        // first find a min that is stable
//        stable = false;
//        while (!stable) {
//            System.out.println("\t\tmin demand scale: " + min);
//            Params.demandScaleFactor = min;
//            stable = runSim(false);
//            reset();
//            min /= 2;
//        }

        boolean stable;
        double mid;
        while (true) {
            System.out.println("\t\tbest demand scale: " + min);
            mid = (min + max) / 2;
            Params.demandScaleFactor = mid;
            System.out.println("\t\t\t\tdsf: " + Params.demandScaleFactor);
            System.out.println("\t\t\t\tRUNNING SIM.....");
            stable = runSim(false);
            reset();
            System.out.println("\t\t\t\tFINISHED SIM AND RESET.....");
            if (stable) {
                if (Math.abs(mid - min) < Math.pow(10, -3)) {
                    System.out.println("\t\tbest demand scale: " + mid);
                    return mid;
                }
                min = mid;
            } else {
                max = mid;
            }
        }
    }

}

