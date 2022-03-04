package ped;

import java.io.File;

public class Simulator extends Network {
    private double simTime; // total network time (time in simulation)
    private double cycleTime; // time within a cycle of the simulation
    private double toleranceTime; // pedestrian tolerance time

    public Simulator(File nodesFile, File linksFile) {
        super(nodesFile, linksFile);
        simTime = 0;
        cycleTime = 0;
        toleranceTime = 0;
    }

    public void runSim(double toleranceTime) {
        this.toleranceTime = toleranceTime;

        // update time

    }
}

