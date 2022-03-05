package ped;

import java.io.File;

public class Simulator extends Network {
    private double simTime; // total network time (time in simulation)
    private double timeStepSize;
    private double toleranceTime; // pedestrian tolerance time
    private boolean ped;

    public Simulator(File nodesFile, File linksFile) {
        super(nodesFile, linksFile);
        simTime = 0;
        timeStepSize = 0;
        toleranceTime = 0;
        ped = true;
    }

    public void runSim(double timeStepSize, boolean ped, double toleranceTime) {
        this.toleranceTime = toleranceTime;
        this.ped = ped;
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

