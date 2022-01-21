package ped;

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
        Set<Set<Phase>> setOfFeasiblePhaseGrouping = intersection.getSetOfFeasiblePhaseGrouping();

        // LOOK AT EACH Set<Phase> with setOfFeasiblePhaseGrouping
        // and find the best one (maximizes objective function)
        // TODO: implement the pedMP logic

        try {
            // create cplex environment
            IloCplex cplex = new IloCplex();

            // create decision variables
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            // create constraints
            IloLinearNumExpr expr = cplex.linearNumExpr();
            expr.addTerm(1, x);
            expr.addTerm(1, y);
            cplex.addLe(expr, 1);
            // x + y <= 1

            // add objective
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            objExpr.addTerm(3, x);
            objExpr.addTerm(2, y);
            cplex.addMaximize(objExpr);
            // max 3x+2y
            System.out.println("OBJECTIVE FUNCTION");
            System.out.println();


            /*
            // constants
            // NEED r_ij (turning ratios)
            // \hat{phi}_{mn}(t) = maximum tolerance time at pedLink m to pedLink j at time t
            //                               we manually set this parameter
            // \alpha_{ij}^{n} = 0/1 to indicator to check whether vehicle movements
            //                                (i,j) intersect with crosswalk n or ij or no
            //                          This value comes from the intersection
            // Qc = max(i,j)|c∈Cij {Qij}
            //          precompute in the Intersection class???

            // for every pedestrian movement
            double max_ped_tolerance_time = 10.0;

            // create decision variables
            IloNumVar v_signal_ij = cplex.numVar(0, 1, "s_ij");
            IloNumVar v_flow_ij = cplex.numVar(0, Integer.MAX_VALUE, "y^v_ij");
            IloNumVar v_queue_ij = cplex.numVar(0, Integer.MAX_VALUE, "x^v_ij");
            IloNumVar v_weight_ij = cplex.numVar(Integer.MIN_VALUE, Integer.MAX_VALUE, "w^v_ij");
            IloNumVar p_signal_mn = cplex.numVar(0, 1, "s_mn");
            IloNumVar waiting_time_mn = cplex.numVar(0, max_ped_tolerance_time, "phi_mn");

            TODO: Question
                    In equation (31), what is p_{jk}(t)? For now I assume it equals 1

           s_ij(t) = signal on off 0-1
                        decision var.
           Q^v_ij = capacity of turning movement from link i to link j
                       we set this parameter (from data??)
           w^ij(t) = the weight of turn from link i to j
                       decision var, equation (31)
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


            // solve and retrieve optimal solution
            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));
            }
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
