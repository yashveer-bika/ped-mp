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
    public static String path = "data/SiouxFalls/";
    private static String getDataName() {
        String[] splits = path.split("/");
        return splits[splits.length - 1];
    }

    public static String controllerType = "pedMP";

    /**
     * uniform demand scale
     */
    public static double demandScaleFactor = 15;


    public static boolean ped = false;

    /**
     * This is the time step, in seconds
     */
    public final static int dt = 15;

    /**
     * This is the current time in the network. It is updated when {@link Network#nextTimestep()} is called.
     */
    public static int time = 0;
    // TODO: update time each timestep in the simulation

    /**
     * This is the pedestrian tolerance time in the simulation, in seconds
     */
    public static int tolerance_time = 15*4;



    /**
     * this is the end time of the simulation, in s. The maximum number of time steps is {@link #DURATION}/{@link #dt}.
     */
    public static int DURATION = 60*60*3;
//    public final static int DURATION = dt * 40;


    public final static int n_steps = DURATION / dt;



//    public final static
    private static long t = System.currentTimeMillis();

//    public final static String sim_output_filepath = String.format("veh_only_sim_ds=%s_dur=%s_ts=%s_id=%s.txt", demandScaleFactor, DURATION, dt, t);

    public static String getSim_output_filepath() {
        if (ped) {
            return String.format(
                    "data=%s_control=%s_tol_time=%s_veh_ped_sim_ds=%s_dur=%s_ts=%s_id=%s.txt",
                    getDataName(), controllerType, tolerance_time, demandScaleFactor,
                    DURATION, dt, System.currentTimeMillis());
        } else {
            return String.format(
                    "data=%s_control=%s_veh_only_sim_ds=%s_dur=%s_ts=%s_id=%s.txt",
                    getDataName(), controllerType, demandScaleFactor,
                    DURATION, dt, System.currentTimeMillis());
        }

    }

    public static String getCplexOutPath() {
        return String.format("cplex_out/cplex_out_ts=%s.txt", time);
    }


    // public final static String avg_occupancy_out_filepath = String.format("avg_occupancy_ts=%s.txt");

    /**
     * The exogenous jam density.
     */
    public static double JAM_DENSITY = 264.0;
}
