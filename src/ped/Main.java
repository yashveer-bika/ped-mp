/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

import java.io.File;
import java.util.*;

import util.*;

/**
 *
 * @author bikax003, micha
 */
public class Main 
{
    public static void main(String[] args)
    {
        TestVehicle tv = new TestVehicle();
        tv.testGetNextNode();

        TestTurningMovement ttm = new TestTurningMovement();
        ttm.testGetVehicles();

        TestPointQueue tpq = new TestPointQueue();
        tpq.testGetVehicles();

        TestPQ3Conflicts tpq3 = new TestPQ3Conflicts();
        tpq3.testVehFeasiblePhases();

        System.out.println("");
        System.out.println("TESTING SIOUX FALLS BB!");
        System.out.println("");

        TestSFPhases test_sf = new TestSFPhases();
        test_sf.testVehFeasiblePhases();

        // TODO: fix the veh-2-veh conflict solution on intersection 5 on PQ3

//        File sf_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
//        File sf_links_f = new File("data/SiouxFalls/network/links.txt");
//        String controllerType = "vehMP";
//        Simulator sf_net = new Simulator(sf_nodes_f, sf_links_f, false, controllerType);
//        sf_net.loadStaticDemand(new File("data/SiouxFalls/trips_static_od_demand.txt"));
//        sf_net.runSim(60*60, 60*60*2, Params.tolerance_time);

//        File pq3_nodes_f = new File("data/PQ3/nodes.txt");
//        File pq3_links_f = new File("data/PQ3/links.txt");
//        String controllerType = "vehMP";
//        // TODO: fix the veh-2-veh conflict solution on intersection 5 on PQ3
//        Simulator pq3_net = new Simulator(pq3_nodes_f, pq3_links_f, false, controllerType);
//        pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
//        pq3_net.runSim(60*60, 60*60*10, Params.tolerance_time);

    }
}