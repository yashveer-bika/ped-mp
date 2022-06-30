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

//        String path = "data/SiouxFalls/";
//        Simulator siouxFallsVehOnly = new Simulator(path, false, "vehMP");
//
//        Params.demandScaleFactor = 0.5;
//        siouxFallsVehOnly.runSim();

//        double minDSF = 0.1;
//        double maxDSF = 0.5;
//        Params.demandScaleFactor = (maxDSF + minDSF) / 2;
//        Params.demandScaleFactor = 0.1;
//        double mdsf = siouxFallsVehOnly.findMaximalDemandScaleFactor(12.57568359375, 12.578125);
//        System.out.println("BEST Demand scale factor: " + mdsf);

//        String path = "data/SiouxFalls/";
//        Params.path = path;
//        Params.controllerType = "pedMP";
//        Params.ped = true;
//
//        Simulator siouxFallsPeds = new Simulator(Params.path, Params.ped, Params.controllerType);
//        Params.DURATION = 60*60*6;
//        Params.tolerance_time = 15*2;
//        double minDSF = 5.1875;
//        double maxDSF = 5.1953125;
//        Params.demandScaleFactor = (maxDSF + minDSF) / 2;
////        Params.demandScaleFactor = 5.517578125;
//        siouxFallsPeds.runSim(false);

        // PEDESTRIAN SIMULATION

        String path = "data/SiouxFalls/";
        Params.path = path;
        Params.controllerType = "pedMP";
        Params.ped = true;

        Simulator siouxFallsPeds = new Simulator(Params.path, Params.ped, Params.controllerType);
        Params.DURATION = (int) (60*60 * 5.5)  ;

        Params.tolerance_time = 30*4;
        double minDSF = 0.17176580697500002;
        double maxDSF = 0.1723544093;
//        Params.demandScaleFactor = (double) Math.round( (minDSF + maxDSF) / 2 * 100 ) / 100;
        Params.demandScaleFactor = (minDSF + maxDSF) / 2;
        Params.demandScaleFactor = 0.2;

        System.out.println("DSF: " + Params.demandScaleFactor);
        boolean stable = siouxFallsPeds.runSim(false);
        if (stable) {
            System.out.println("Stable");
        } else {
            System.out.println("Not Stable");
        }
        System.out.println("Servable hourly demand: " + Params.demandScaleFactor * 360600.0 / 24.0);

//        double mdsf = siouxFallsPeds.findMaximalDemandScaleFactor(minDSF, maxDSF);
//        System.out.println("BEST Demand scale factor: " + mdsf);

        // VEHICLE SIMULATION

//        String path = "data/SiouxFalls/";
//        Params.path = path;
//        Params.controllerType = "vehMP";
//        Params.ped = false;
//
//        Simulator siouxFallsVeh = new Simulator(Params.path, Params.ped, Params.controllerType);
//        Params.DURATION = (int) (60*60 * 5.5)  ;
//
//        double minDSF = 0.52276611328125;
//        double maxDSF = 0.52294921875;
////        Params.demandScaleFactor = (double) Math.round( (minDSF + maxDSF) / 2 * 100 ) / 100;
//        Params.demandScaleFactor = (minDSF + maxDSF) / 2;
//
////        Params.demandScaleFactor = 50.0;
//
//        System.out.println("DSF: " + Params.demandScaleFactor);
//        boolean stable = siouxFallsVeh.runSim(false);
//        if (stable) {
//            System.out.println("Stable");
//        } else {
//            System.out.println("Not Stable");
//        }
//        System.out.println("Servable hourly demand: " + Params.demandScaleFactor * 360600.0 / 24.0);

//        double mdsf = siouxFallsVeh.findMaximalDemandScaleFactor(0.00001, 20);
//        System.out.println("BEST Demand scale factor: " + mdsf);
    }
}