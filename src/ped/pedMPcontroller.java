package ped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ilog.concert.*;
import ilog.cplex.*;


public class pedMPcontroller implements Controller {
    // variables
    Intersection intersection;

    public pedMPcontroller(Intersection intersection) {
        this.intersection = intersection;
    }

    public Set<Phase> selectBestPhaseSet() {
        // Set<Set<Phase>> setOfFeasiblePhaseGrouping = intersection.getSetOfFeasiblePhaseGrouping();

        // LOOK AT EACH Set<Phase> with setOfFeasiblePhaseGrouping
        // and find the best one (maximizes objective function)
        // TODO: implement the pedMP logic

        System.out.println("\nWORKING ON CPLEX EQUATIONS\n");

        try {
            // create cplex environment
            IloCplex cplex2 = new IloCplex();

            // create decision variables
            IloNumVar x2 = cplex2.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex2.numVar(0, Double.MAX_VALUE, "y");


            // create constraints
            IloLinearNumExpr expr2 = cplex2.linearNumExpr();
            expr2.addTerm(1, x2);
            expr2.addTerm(1, y);
            cplex2.addLe(expr2, 1);
            // x + y <= 1

            // add objective
            IloLinearNumExpr objExpr2 = cplex2.linearNumExpr();
            objExpr2.addTerm(3, x2);
            objExpr2.addTerm(2, y);
            cplex2.addMaximize(objExpr2);
            // max 3x+2y
            System.out.println("OBJECTIVE FUNCTION");
            System.out.println();


            IloCplex cplex = new IloCplex();
            // create objective function's expression
            IloLinearNumExpr objExpr = cplex.linearNumExpr();

            // for every pedestrian movement \hat{phi}_{mn}(t)
            double max_ped_tolerance_time = 10.0;


            // constants
            // NEED r_ij (turning ratios)
            // \hat{phi}_{mn}(t) = maximum tolerance time at pedLink m to pedLink j at time t
            //                               we manually set this parameter
            // \alpha_{ij}^{n} = 0/1 to indicator to check whether vehicle movements
            //                                (i,j) intersect with crosswalk n or ij or no
            //                          This value comes from the intersection
            // Qc = max(i,j)|c∈Cij {Qij}
            //          precompute in the Intersection class???

            // for each turning movement, there is a cap. (Q^{v}_{ij})
            // vehicle turning movement capacity

            // TODO: correctly generate all vehicle turning movements [intersection.getVehicleTurns()]
            // CREATE VEHICLE SIGNALS
            Map<Turn, IloIntVar> v_signals = new HashMap<>();
            for (Turn t : intersection.getVehicleTurns()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_ij");
                v_signals.put(t, v_signal_ij);
            }
            // TODO: correctly generate all ped turning movements [intersection.getPedestrianTurningMovements()]
            // CREATE PEDESTRIAN SIGNALS
            Map<Turn, IloIntVar> ped_signals = new HashMap<>();
            for (Turn t : intersection.getPedestrianTurningMovements()) {
                IloIntVar p_signal_mn = cplex.intVar(0, 1, "s_ij");
                ped_signals.put(t, p_signal_mn);
            }
            // FETCH PEDESTRIAN WAIT TIMES
            Map<Turn, Double> ped_wait_times = new HashMap<>(); // {phi}_{mn}(t}
            for (Turn t : intersection.getPedestrianTurningMovements()) {
                // TODO: verify that t.getPedWaitTime() works correctly
                double wait_time = t.getPedWaitTime();
                ped_wait_times.put(t, wait_time);
            }
            // get vehicle pedestrian conflicts
            // TODO: verify intersection.getVehPedConflicts() works
            // \alpha_{ij}^{n} : a vehicle turning movement ij conflicts with a crosswalk n
            Map<Turn, Crosswalk> vehPedConflict = intersection.getVehPedConflicts();

            Map<Turn, Double> queueLengths = intersection.getQueueLengths();



            // CREATE OBJECTIVE FUNCTION
            for (Turn t : intersection.getVehicleTurns()) {
                // TODO: get capacity from network
                double v_turn_mov_cap = Double.MAX_VALUE;
                // TODO: calculate weight (w^{v}_{ij}(t) )
                double v_weight_ij = Double.MAX_VALUE;
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(t));
                cplex.addMaximize(objExpr);
            }

            // solve and retrieve optimal solution
            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
                for (Turn t : v_signals.keySet()) {
                    IloIntVar v_sig = v_signals.get(t);
                    System.out.println("vehicle signal @ " + t + " = " + cplex.getValue(v_sig));
                }
            }



            /*
            // we want constraint that v_signal_ij is {0,1}
            IloNumVar v_flow_ij = cplex.numVar(0, Integer.MAX_VALUE, "y^v_ij");
            // we want integer constraints
            IloNumVar v_queue_ij = cplex.numVar(0, Integer.MAX_VALUE, "x^v_ij");
            // we want integer constraints
            IloNumVar v_weight_ij = cplex.numVar(Integer.MIN_VALUE, Integer.MAX_VALUE, "w^v_ij");
            IloNumVar p_signal_mn = cplex.numVar(0, 1, "s_mn");

            IloNumVar waiting_time_mn = cplex.numVar(0, max_ped_tolerance_time, "phi_mn");
            */


            /*



           s_ij(t) = signal on off 0-1
                        decision var.
           Q^v_ij = capacity of turning movement from link i to link j
                       we set this parameter (from data??)
           w_ij(t) = the weight of turn from link i to j
                       decision var, equation (31),
                       x_ij(t) - avg. queue length of movements on downstream links
           s_mn(t) = signal on off 0-1
                 decision var.
           phi_{mn}(t) = The pedestrians waiting time for cross movement (m, n) at time step t
                            decision var < \hat{phi}_{mn}(t)
           \hat{phi}_{mn}(t) = maximum tolerance time at pedLink m to pedLink j at time t
                               we manually set this parameter
           \alpha_{ij}^{n} = 0/1 to indicator to check whether vehicle movements
                                (i,j) intersect with crosswalk n or ij or not

           y_{ij}^v(t) = the flow of vehicles from i to j at time t, which is controlled by traffic signal.
                decision variable

            Constraints (32b) represent the relationship with vehicle
            movements and pedestrians movement, αn is a 0 or 1 indicator to check
            whether vehicle movements (i,j) intersect with crosswalk n or ij
            not. For instance, when the vehicles movement is activated, then if it
            intersects with crosswalk n, smn(t) is forced to be zero. However, if the
            vehicles movement is activated, then if it doesn’t conflict with crosswalk n
            , the smn(t) is could be 0 or 1.

            (32c) says that if the vehicle movement interferes with crosswalk n,
            moving from pedLink m through crosswalk n is not allowed
                This should be handled by the feasible phase set generator???

            (32d) indicates the sum movements of vehicle should
            less equal to the capacity of the conflict region








            // create constraint
            // 68b - 68j

             */
        } catch (IloException e) {
            e.printStackTrace();
        }


        return null;
    }

    public void moveObjectsThroughIntersection() {
        // move objects
        //the intersection
        Set<Phase> best_phase_set = selectBestPhaseSet();
        //
    }
}
