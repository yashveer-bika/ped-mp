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
    private double network_time;

    public pedMPcontroller(Intersection intersection) {
        this.intersection = intersection;
    }

    public void updateTime(double newTime) {
        network_time = newTime;
    }

    public Set<Phase> selectBestPhaseSet() {
        // Set<Set<Phase>> setOfFeasiblePhaseGrouping = intersection.getSetOfFeasiblePhaseGrouping();

        // LOOK AT EACH Set<Phase> with setOfFeasiblePhaseGrouping
        // and find the best one (maximizes objective function)
        // TODO: test the pedMP logic


        try {
            IloCplex cplex = new IloCplex();

            // for every pedestrian movement \hat{phi}_{mn}(t)
            double max_ped_tolerance_time = 10.0;

            // TODO: get vehicle and ped turningMovements once and store them locally

            // constants
            // NEED r_ij (turning ratios)
            // \hat{phi}_{mn}(t) = maximum tolerance time at pedLink m to pedLink j at time t
            //                               we manually set this parameter
            // \alpha_{ij}^{n} = 0/1 to indicator to check whether vehicle movements
            //                                (i,j) intersect with crosswalk n or ij or no
            //                          This value comes from the intersection
            // Qc = max(i,j)|c∈Cij {Qij}
            //          precompute in the Intersection class???
            //          make a conflict region class???

            // for each turning movement, there is a cap. (Q^{v}_{ij})
            // vehicle turning movement capacity

            // CREATE VEHICLE SIGNALS
            Map<TurningMovement, IloIntVar> v_signals = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_ij");
                v_signals.put(turn, v_signal_ij);
            }

            // CREATE PEDESTRIAN SIGNALS
            Map<TurningMovement, IloIntVar> ped_signals = new HashMap<>();
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                IloIntVar p_signal_mn = cplex.intVar(0, 1, "s_mn");
                ped_signals.put(turn, p_signal_mn);
            }

            // TODO: verify getVehicleTurns()
            // CREATE number of vehicles to move through a turning movement
            // y^v_{ij}
            Map<TurningMovement, IloNumVar> vehMoveNums = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                IloNumVar num_vehs_to_move = cplex.numVar(0, Double.MAX_VALUE, "y^v_{ij}");
                vehMoveNums.put(turn, num_vehs_to_move);
            }

            // TODO: verify getVehicleTurns()
            // CREATE number of vehicles to move through a turning movement
            // y^p_{ij}
            Map<TurningMovement, IloNumVar> pedMoveNums = new HashMap<>();
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                IloNumVar num_peds_to_move = cplex.numVar(0, Double.MAX_VALUE, "y^p_{mn}");
                pedMoveNums.put(turn, num_peds_to_move);
            }

            /*
            // FETCH PEDESTRIAN WAIT TIMES
            Map<TurningMovement, Double> ped_wait_times = new HashMap<>(); // {phi}_{mn}(t}
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                // TODO:
                double wait_time = turn.getWaiting_time();
                ped_wait_times.put(turn, wait_time);
            }
             */

            // FETCH vehicle pedestrian conflicts
            // TODO: verify intersection.getVehPedConflicts() works
            // \alpha_{ij}^{n} : a vehicle turning movement ij conflicts with a crosswalk n
            Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflict = intersection.getVehPedConflicts();

            // FETCH vehicle queue lengths (x^v_{ij}}
            Map<TurningMovement, Double> vehQueueLengths = intersection.getVehQueueLengths();

            // FETCH pedestrian queue lengths (x^p_{ij}}
            Map<TurningMovement, Double> pedQueueLengths = intersection.getPedQueueLengths();

            Map<TurningMovement, Double> pedTurnMovCaps = intersection.getPedTurnMovCaps();



            /*** CONSTRAINTS ***/
            // NOTE: the numbering is directly from the paper
            // enforce pedestrian tolerance time
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                IloLinearNumExpr expr68b = cplex.linearNumExpr();
                try {
                    double wait_time = turn.getWaiting_time();
                    double time_diff = (wait_time - max_ped_tolerance_time);
                    // (1 - s_mn) (phi_mn - \hat{phi}_mn ) <= 0
                    // (phi_mn - \hat{phi}_mn ) - (phi_mn - \hat{phi}_mn ) * s_mn <= 0
                    //  - (phi_mn - \hat{phi}_mn ) * s_mn <= -(phi_mn - \hat{phi}_mn )
                    expr68b.addTerm(-1 * time_diff, ped_signals.get(turn));
                    cplex.addLe(expr68b, -1 * time_diff);
                } catch (EmptyQueueException e) {
                    // if we have an empty queue, turn the signal off
                    expr68b.addTerm(1, ped_signals.get(turn));
                    cplex.eq(0, expr68b);
                }
            }



            // prevent vehicle-pedestrian collision
            for (TurningMovement ped_turn : intersection.getPedestrianTurningMovements()) {
                for (TurningMovement veh_turn : intersection.getVehicleTurns()) {
                    try {
                        IloLinearNumExpr expr68c = cplex.linearNumExpr();
                        expr68c.addTerm(1, ped_signals.get(ped_turn)); // pedestrian signal
                        // interfering veh signal
                        // TODO: fix vehPedConflict (needs to be populated)
                        expr68c.addTerm(vehPedConflict.get(veh_turn).get(ped_turn), v_signals.get(veh_turn));
                        cplex.addLe(expr68c, 1);
                    } catch (NullPointerException e) {
                        System.out.println("null value related to vehPedConflicts");
                    }
                }
            }


            // sets the flow through a turning movement by capping the
            // flow with the capacity of the turn. mov., otherwise letting
            // the flow by the queue length for the turning movement
            // for vehicles
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                expr68e.addTerm(turn.getCapacity(), v_signals.get(turn));
                cplex.addEq(vehMoveNums.get(turn), cplex.min(expr68e, turn.getQueueLength() ));
            }
            // sets the flow through a turning movement by capping the
            // flow with the capacity of the turn. mov., otherwise letting
            // the flow by the queue length for the turning movement
            // for pedestrians
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                IloLinearNumExpr expr68f = cplex.linearNumExpr();
                expr68f.addTerm(turn.getCapacity(), ped_signals.get(turn));
                cplex.addEq( pedMoveNums.get(turn), cplex.min(expr68f, turn.getQueueLength()) );
            }

            // calculate vehicle weight ( w^{v}_{ij}(t) )
            // NOTE: weight calculation is a network level operation,
            //       since we look at the downstream turning movements

            // CREATE OBJECTIVE FUNCTION
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                double v_turn_mov_cap = turn.getCapacity();
                // TODO: calculate weight ( w^{v}_{ij}(t) )
                double v_weight_ij = 10;
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));

            }
            cplex.addMaximize(objExpr);

            // solve and retrieve optimal solution
            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
                for (TurningMovement turn : v_signals.keySet()) {
                    IloIntVar v_sig = v_signals.get(turn);
                    System.out.println("vehicle signal @ " + turn + " = " + cplex.getValue(v_sig));
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


}
