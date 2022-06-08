/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

// TODO: Set params according to our simulation

/**
 * This class stores important global parameters that are useful across the entire project.
 *
 * @author Michael Levin
 */
public class Params
{

    /**
     * uniform demand scale
     */
    public final static double demandScaleFactor = 0.1;

    /**
     * This is the time step, in seconds
     */
    public final static double dt = 15;

    /**
     * This is the current time in the network. It is updated when {@link Network#nextTimestep()} is called.
     */
    public static int time = 0;

    /**
     * This is the pedestrian tolerance time in the simulation, in seconds
     */
    public final static int tolerance_time = 60;


    public final static int n_steps = 25;


    /**
     * this is the end time of the simulation, in s. The maximum number of time steps is {@link #DURATION}/{@link #dt}.
     */
    public final static double DURATION = dt * n_steps;

    public final static String cplex_out_filepath = "cplex_out.txt";

    public final static String avg_occupancy_out_filepath = String.format("avg_occupancy_ts=%s.txt");

    /**
     * The exogenous jam density.
     */
    public static double JAM_DENSITY = 264.0;
}
