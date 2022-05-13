package ped;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class vehMPcontroller implements Controller {
    // variables
    Intersection intersection;
    private double network_time;

    public vehMPcontroller(Intersection intersection) {
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

        // TODO: generate all phases

        System.out.println("Intersection: " + this.intersection.getId());

        try {
            IloCplex cplex = new IloCplex();
            File f = new File(Params.cplex_out_filepath);
            FileOutputStream f_out = new FileOutputStream(f);
//            cplex.setOut(f_out);
            cplex.setOut(null);
            // cplex.setWarning(null);

            // constants
            // NEED r_ij (turning ratios)
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
                int i = turn.getIncomingLink().getId();
                int j = turn.getOutgoingLink().getId();
                String name = "s_{" +  + i + "," + j + "}";
                IloIntVar v_signal_ij = cplex.intVar(0, 1, name);
                v_signals.put(turn, v_signal_ij);
            }

            // CREATE number of vehicles to move through a turning movement
            // y^v_{ij}
            Map<TurningMovement, IloNumVar> vehMoveNums = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                IloNumVar num_vehs_to_move = cplex.numVar(0, Double.MAX_VALUE, "y^v_{ij}");
                vehMoveNums.put(turn, num_vehs_to_move);
            }

            // FETCH vehicle queue lengths (x^v_{ij}}
            Map<TurningMovement, Double> vehQueueLengths = intersection.getVehQueueLengths();


            /*** CONSTRAINTS ***/


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


            // calculate vehicle weight ( w^{v}_{ij}(t) )

            // CREATE OBJECTIVE FUNCTION
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                double v_turn_mov_cap = turn.getCapacity();
                System.out.println("v_turn_mov_cap: " + v_turn_mov_cap);
                double v_weight_ij = turn.getWeight();
                System.out.println("v_weight_ij: " + v_weight_ij);
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
            }
            cplex.addMaximize(objExpr);

            // solve and retrieve optimal solution
            if (cplex.solve()) {
                System.out.println("PRINTS decision var");
                System.out.println(cplex);
//                System.out.println("Optimal value = " + cplex.getObjValue());
//                for (TurningMovement turn : v_signals.keySet()) {
//                    IloIntVar v_sig = v_signals.get(turn);
//                    System.out.println("vehicle signal @ " + turn + " = " + cplex.getValue(v_sig));
//                }
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }


}
