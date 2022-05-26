package ped;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplexModeler;
import util.Tuple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
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

    public Tuple<Phase, Map<TurningMovement, Integer>> run() {
        // iterate over every phase
        Phase bestPhase = null;
        Map<TurningMovement, Integer> flowVals = new HashMap<>();
        double max_value = Double.NEGATIVE_INFINITY;


        // TODO: write the CPLEX
        try {
            IloCplex cplex = new IloCplex();

            // CREATE VEHICLE SIGNALS (s_ij)
            Map<TurningMovement, IloIntVar> v_signals = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_ij");
                v_signals.put(turn, v_signal_ij);
            }

            // CREATE FLOW VALS (y_ij)
            Map<TurningMovement, IloNumVar> flow_vals = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                IloNumVar y_ij = cplex.numVar(0, Double.MAX_VALUE, "y_ij");
                flow_vals.put(turn, y_ij);
            }

            // objective function max \sum{ s_{ij} * Q_{ij} * w_{ij} }
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                double v_turn_mov_cap = turn.getCapacity();
                double v_weight_ij = turn.getWeight();
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
            }
            cplex.addMaximize(objExpr);

            // turning movement conflict constraint
            for (TurningMovement tm1 : v_signals.keySet()) {
                Map<TurningMovement, Integer> conflictMap = intersection.getV2vConflicts().get(tm1);
                for (TurningMovement tm2 : conflictMap.keySet()) {
                    // if tm1 and tm2 conflict
                    if (conflictMap.get(tm2) == 0) {
                        // don't allow tm2 to be activated if tm1 is activated
//                        IloCplex c1 = new IloCplex();
                        IloConstraint r1 = cplex.eq(1, v_signals.get(tm1));
                        IloConstraint r2 = cplex.eq(0, v_signals.get(tm2));
                        cplex.ifThen(r1, r2);
                    }
                }
            }

            // TODO: how do I code this up in CPLEX??
//            // conflict region capacity constraint
//            IloLinearNumExpr expr68d = cplex.linearNumExpr();
//            for (TurningMovement tm : flow_vals.keySet()) {
//
//            }

            // flow value queue length/capacity constraint
//                 sets the flow through a turning movement by capping the
//                 flow with the capacity of the turn. mov., otherwise letting
//                 the flow by the queue length for the turning movement
//                 for vehicles
            for (TurningMovement turn : intersection.getVehicleTurns()) {
                IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                expr68e.addTerm(turn.getCapacity(), v_signals.get(turn));
                System.out.println("\t" + turn + " --> " + turn.getQueueLength());
                cplex.addEq(flow_vals.get(turn), cplex.min(expr68e, turn.getQueueLength() ));
            }


            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
//                for (TurningMovement turn : v_signals.keySet()) {
//                    IloIntVar v_sig = v_signals.get(turn);
//                    System.out.println("vehicle signal @ " + turn + " = " + cplex.getValue(v_sig));
//                }
                Set<TurningMovement> bestTurningMovements = new HashSet<>();
                for (TurningMovement tm : v_signals.keySet()) {
                    IloIntVar v_sig = v_signals.get(tm);
                    double on_off = cplex.getValue(v_sig);
                    if (((Double) on_off).equals(1.0)) {
                        bestTurningMovements.add(tm);
                    }
                }
                bestPhase = new Phase(bestTurningMovements);
                System.out.println("Best phase: " + bestPhase);

                for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                    int y_ij = (int) cplex.getValue(flow_vals.get(turn));
                    flowVals.put(turn, y_ij);
                    System.out.println("\t" + turn + " --> " + y_ij);
                }


                System.out.println();
                System.out.println();
            }


        } catch (IloException e) {
            e.printStackTrace();
        }


        return new Tuple(bestPhase, flowVals);
    }
}
