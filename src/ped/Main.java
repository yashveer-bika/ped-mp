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
//         File sf_net_file = new File("data/SiouxFalls/net.txt");
//         File sf_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
//         File sf_links_f = new File("data/SiouxFalls/network/links.txt");
//         Network sf_net2 = new Network(sf_nodes_f, sf_links_f);
//         // Network sf_net = new Network(sf_net_file);
//         System.out.println( sf_net2.getNodes() );

//        File pq3_nodes_f = new File("data/SiouxFalls/network/nodes.txt");
//        File pq3_links_f = new File("data/SiouxFalls/network/links.txt");

        TestVehicle tv = new TestVehicle();
        tv.testGetNextNode();
        TestTurningMovement ttm = new TestTurningMovement();
        ttm.testGetVehicles();
        TestPointQueue tpq = new TestPointQueue();
        tpq.testGetVehicles();

        // assert 1 == 2;

        File pq3_nodes_f = new File("data/PQ3/nodes.txt");
        File pq3_links_f = new File("data/PQ3/links.txt");
        String controllerType = "vehMP";

        // TODO: verify the veh-2-veh conflict solution
        Simulator pq3_net = new Simulator(pq3_nodes_f, pq3_links_f, false, controllerType);
        pq3_net.loadStaticDemand(new File("data/PQ3/trips_static_od_demand.txt"));
        pq3_net.runSim(60*60, 60*60*1, Params.tolerance_time);

    }
}