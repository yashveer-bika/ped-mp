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
    private Map<Node, double[]> static_demand;

    public Simulator(File nodesFile, File linksFile, boolean ped, String controllerType) {
        super(nodesFile, linksFile, ped, controllerType);
        simTime = 0;
        timeStepSize = 0;
        toleranceTime = 0;
        this.ped = ped;
        this.static_demand = new HashMap<Node, double[]>();
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
                if (data.charAt(0) == 'n') {
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


            for (String[] demand_data : demand_rows) {

                int nodeId = Integer.parseInt(demand_data[0]);
                double startTime = Double.parseDouble(demand_data[1]);
                double endTime = Double.parseDouble(demand_data[2]);
                double rate = Double.parseDouble(demand_data[3]); // assume (vph)

                /*
                *   {nodeId: [startTime, endTime, rate]}
                */
                double time_and_rate[] = {startTime, endTime, rate};
                for (Node n : this.getNodeSet()) {
                    if (n.getId() == nodeId) {
                        static_demand.put(n, time_and_rate);
                    }
                }

            }
            for (Node n : static_demand.keySet()) {
                System.out.println(n.getId());
                double[] arr = static_demand.get(n);
                for (double d : arr) {
                    System.out.print(d + " ");
                }
                System.out.println();
            }
//            System.out.println(static_demand);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void runSim(double timeStepSize, double totalRunTime, double toleranceTime) {
        this.toleranceTime = toleranceTime;
        this.timeStepSize = timeStepSize;

        while (simTime <= totalRunTime) {
            System.out.println("Sim Time: " + simTime);

            // load demand into network
            this.addDemandToNodes(static_demand, simTime, timeStepSize);
            this.splitDemandToLinks();
            this.printNodeDemands();



            // TODO: print out the queue lengths of the whole network
            // this.printNetwork();

            // run controller
            for (Intersection i : this.getIntersectionSet()) {
                i.iterateTimeStep();
            }
            // update the queues
            updateTime();
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

