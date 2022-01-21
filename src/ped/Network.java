package ped;

import java.util.Set;
import java.util.HashSet;

public class Network {
    private Set<Intersection> intersectionSet;
    private Set<Link> linkSet;
    private Set<Node> nodeSet;

    public Network(Set<Intersection> intersectionSet, Set<Link> linkSet, Set<Node> nodeSet) {
        this.intersectionSet = intersectionSet;
        this.linkSet = linkSet;
        this.nodeSet = nodeSet;
    }

    // TODO: what are network-level operations

    /**
     * method: runSimulation(int time_steps)
     * purpose: run through a simulation with over 'time_steps' number of discrete time steps
     * parameters:
     */

    /**
     * PURPOSE: run all the operations of the simulation for one time step
     * parameters:
     *
     * pseudocode
     *  for each intersection
     *      use max pressure to find best phase set at the current time
     *      activate the phase chosen the by controller
     *      move vehicles / pedestrians according to the phase
     *
     */
    public void incrementTime() {

    }

}
