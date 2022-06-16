/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import ilog.concert.IloException;
import util.*;

/**
 *
 * @author bikax003, micha
 */
public class Main 
{
    public static void main(String[] args) throws IloException {
//        System.out.println(path);
//        File nodesFile = new File(path + "nodes.txt");
//        File linksFile = new File(path + "links.txt");
//        File demand_file = new File(path + "trips_static_od_demand.txt");
//        File turn_props_file = new File(path + "turning_proportions.txt");
////
//        Simulator test_net = new Simulator(path, false, "vehMP");
//        System.out.println(test_net.getNodes().keySet());
//
//        System.out.println(test_net.getLinkSet());





//        TestVehicle tv = new TestVehicle();
//        tv.testGetNextNode();

//        TestTurningMovement ttm = new TestTurningMovement();
//        ttm.testGetVehicles();

//        TestPointQueue tpq = new TestPointQueue();
//        tpq.testGetVehicles();
//

//        TestCPLEX testCPLEX = new TestCPLEX();

//        TestPQ3Conflicts tpq3 = new TestPQ3Conflicts();
////        tpq3.testControllerWPeds();
//        // TODO: later, make my phase generation logic less expensive so I can test on larger networks or ped networks
//        tpq3.testVehFeasiblePhases();
//        tpq3.testV2VConflicts();

//        tpq3.testController();

//        TestINT4 testINT4 = new TestINT4();
//        testINT4.testController();

//        TestSFPhases testSFPhases = new TestSFPhases();

//        tpq3.testPedFeasiblePhases();

//        System.out.println("");
//        System.out.println("TESTING SIOUX FALLS BB!");
//        System.out.println("");
////
//        TestSFPhases test_sf = new TestSFPhases();
//        test_sf.testVehFeasiblePhases();

//        File sf_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
//        File sf_links_f = new File("data/SiouxFalls/network/links.txt");
//        String controllerType = "vehMP";
//        Simulator sf_net = new Simulator(sf_nodes_f, sf_links_f, false, controllerType);
//        sf_net.loadStaticDemand(new File("data/SiouxFalls/trips_static_od_demand.txt"));
//        sf_net.runSim(60*60, 60*60*2, Params.tolerance_time);
//
//        File pq3_nodes_f = new File("data/PQ3/nodes.txt");
//        File pq3_links_f = new File("data/PQ3/links.txt");
//        String controllerType = "vehMP";
//        Simulator pq3_net = new Simulator(pq3_nodes_f, pq3_links_f, false, controllerType);
//        pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
//        pq3_net.runSim(60*60, 60*60*2, Params.tolerance_time);

        String path = "data/SiouxFalls/";
        Simulator siouxFallsVehOnly = new Simulator(path, false, "vehMP");
        double minDSF = 0.1;
        double maxDSF = 0.5;
        Params.demandScaleFactor = (maxDSF + minDSF) / 2;
//        Params.demandScaleFactor = 0.1;
        siouxFallsVehOnly.runSim();

//        String path = "data/SiouxFalls/";
//        Simulator siouxFallsPeds = new Simulator(path, false, "vehMP");
//        siouxFallsPeds.runSim();

        // TODO: write some

    }
}