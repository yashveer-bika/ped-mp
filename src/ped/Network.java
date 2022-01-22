package ped;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Network {
    private Set<Intersection> intersectionSet;
    private Set<Link> linkSet;
    private Set<Node> nodeSet;

    public Network(Set<Intersection> intersectionSet, Set<Link> linkSet, Set<Node> nodeSet) {
        this.intersectionSet = intersectionSet;
        this.linkSet = linkSet;
        this.nodeSet = nodeSet;
    }

    // TODO: make sure this works on Sioux Falls Network
    public Network(File networkFile) {
        try {
            String[] header = {};
            Scanner myReader = new Scanner(networkFile);
            ArrayList<String[]> link_rows = new ArrayList();

            // read nodes/links from net.txt
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // things we ignore
                if (data == "" || data.charAt(0) == '<') {
                    continue;
                }
                if (data.charAt(0) == '~') {
                    System.out.println("HEADER");
                    header = data.split("\t");
                    continue;
                }
                if (data.charAt(0) == '\t') { // normal link
                    String[] link_data = data.split("\t");
                    link_rows.add(link_data);
                }
            }
            myReader.close();

            // print header
            System.out.println("Header length: ");
            int i=0;
            for (String val : header) {
                System.out.println(val);
                i++;
            }
            System.out.println("i = " + i);

            // print link rows
            for (String[] link : link_rows) {
                int j=0;
                for (String val : link) {
                    System.out.print(val + "\t");
                    j++;
                }
                System.out.println("\nj = " + j);
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


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
