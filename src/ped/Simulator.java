package ped;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class Simulator extends Network {
    private double simTime; // total network time (time in simulation)
    private double timeStepSize;
    private double toleranceTime; // pedestrian tolerance time
    private boolean ped;
//    entrance node, destination node, time, demand_quantity
//    private Map<Node, Map<Node, Map<Double, Double>>> demand;
//    entrance node, start time, end time, demand_quantity
    private Map<Node, Map<Double, Map<Double, Double>>> static_demand;

    public Simulator(File demandFile, File linksFile, boolean ped) {
        super(demandFile, linksFile, ped);
        simTime = 0;
        timeStepSize = 0;
        toleranceTime = 0;
        this.ped = ped;
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
                double rate = Double.parseDouble(demand_data[3]);

                // TODO: put this demand data in some data structure

            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void runSim(double timeStepSize, double toleranceTime) {
        this.toleranceTime = toleranceTime;
        this.timeStepSize = timeStepSize;



        // update time
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

