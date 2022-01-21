package ped;

public class evenOddController {
    // variables
    OldNetwork network;
    int time;


    public evenOddController(OldNetwork network) {
        this.network = network;
        this.time = 0;
    }

    // THIS IS THE FUNCTION THAT DETERMINES HOW THE SIGNAL CONTROLLER WORKS
    public void runSimulation(int timeSteps) {
        while (time < timeSteps) {

            // set signals for every node based on the state of the network
            setSignals();


            time += 1;
        }
    }

    // Define the control
    public void setSignals() {

        return;
    }

}
