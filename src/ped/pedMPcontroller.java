package ped;

public class pedMPcontroller {
    // variables
    Network network;
    int time;


    public pedMPcontroller(Network network) {
        this.network = network;
        this.time = 0;
    }

    // THIS IS THE FUNCTION THAT DETERMINES HOW THE SIGNAL CONTROLLER WORKS
    public void runSimulation(int timeSteps) {
        while (time < timeSteps) {

            // set signals for every node based on the state of the network



            time += 1;
        }
    }

    // Define the control
    public void setSignals() {
        return;
    }

}
