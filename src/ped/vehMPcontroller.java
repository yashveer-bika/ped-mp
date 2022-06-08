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

    public Tuple<Phase, Map<TurningMovement, Double>> run() {
        // iterate over every phase
        Phase bestPhase = null;
        Map<TurningMovement, Double> flowVals = new HashMap<>();
        double max_value = Double.NEGATIVE_INFINITY;

//        System.out.println("INTERSECTION " + intersection.getId());
//        System.out.println("\t\t" + intersection.getInternalVehicleTurns());

        // TODO: write the CPLEX
        try {
            IloCplex cplex = new IloCplex();
            cplex.setOut(null);

            // CREATE VEHICLE SIGNALS (s_ij)
            Map<TurningMovement, IloIntVar> v_signals = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_{" + turn + "}");
                v_signals.put(turn, v_signal_ij);
            }
            // guarantee the entry turning movements are activated
            for (TurningMovement turn : intersection.getEntryTurns()) {
                cplex.addEq(v_signals.get(turn), 1); // no-conflicts
            }

            // CREATE FLOW VALS (y_ij)
            Map<TurningMovement, IloNumVar> flow_vals = new HashMap<>();
            for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
                IloNumVar y_ij = cplex.numVar(0, Double.MAX_VALUE, "y_{" + turn + "}");
                flow_vals.put(turn, y_ij);
            }

            // objective function max \sum{ s_{ij} * Q_{ij} * w_{ij} }
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
                double v_turn_mov_cap = turn.getCapacity();
                double v_weight_ij = turn.getWeight();
//                System.out.println("weight of " + turn + " --> " + v_weight_ij);
                // if weight == 0 and the turn doesn't add additional conflict, then add into turns???
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
            }
            cplex.addMaximize(objExpr);

            // turning movement conflict constraint
            for (TurningMovement tm1 : v_signals.keySet()) {
//                System.out.println("tm1: " + tm1);

                Map<TurningMovement, Integer> conflictMap = intersection.getV2vConflicts().get(tm1);
                if (conflictMap == null) {
                    continue;
                }

                for (TurningMovement tm2 : conflictMap.keySet()) {
                    // if tm1 and tm2 conflict
                    if (conflictMap.get(tm2) == 0 && !tm1.equals(tm2)) {
//                        System.out.println("tm1: " + tm1);
//                        System.out.println("tm2: " + tm2);
                        // don't allow tm2 to be activated if tm1 is activated
//                        IloConstraint r1 = cplex.eq(1, v_signals.get(tm1));
//                        IloConstraint r2 = cplex.eq(0, v_signals.get(tm2));
//                        cplex.ifThen(r1, r2);

//                        IloConstraint r3 = cplex.eq(1, v_signals.get(tm2));
//                        IloConstraint r4 = cplex.eq(0, v_signals.get(tm1));
//                        cplex.ifThen(r3, r4);

                        IloLinearNumExpr e = cplex.linearNumExpr();
                        e.addTerm(1, v_signals.get(tm1));
                        e.addTerm(1, v_signals.get(tm2));
                        cplex.addLe(e, 1, "turn. mov. conflict: " + tm1 + " --> " + tm2);
                    }
                }
            }


            // TODO: generate test data that utilizes the conflict region capacity constraint
            // conflict region capacity constraint
            for (ConflictRegion cr : intersection.getConflictRegions()) {
                IloLinearNumExpr expr68d = cplex.linearNumExpr();
                for (TurningMovement tm : cr.getTms()) {
                    expr68d.addTerm(1, flow_vals.get(tm));
                }
                cplex.addLe(expr68d, cr.getCapacity());
            }

            // flow value queue length/capacity constraint
//                 sets the flow through a turning movement by capping the
//                 flow with the capacity of the turn. mov., otherwise letting
//                 the flow by the queue length for the turning movement
//                 for vehicles
//            System.out.println("Intersection " + intersection.getId());
//            System.out.println("flow value queue length/capacity constraint");
            for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
                IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                expr68e.addTerm(turn.getCapacity(), v_signals.get(turn));
//                System.out.println("\t" + turn + " --> " + turn.getQueueLength());
//                System.out.println("\t" + "capacity" + " --> " + turn.getCapacity());
                cplex.addEq(flow_vals.get(turn), cplex.min(expr68e, turn.getQueueLength() ));
            }


            if (cplex.solve()) {
//                System.out.println("Optimal value = " + cplex.getObjValue());
//                for (TurningMovement turn : v_signals.keySet()) {
//                    IloIntVar v_sig = v_signals.get(turn);
//                    System.out.println("vehicle signal @ " + turn + " = " + cplex.getValue(v_sig));
//                }
                Set<TurningMovement> bestTurningMovements = new HashSet<>();
                for (TurningMovement tm : v_signals.keySet()) {
                    IloIntVar v_sig = v_signals.get(tm);
                    double on_off = cplex.getValue(v_sig);
//                    System.out.println("\t" + tm + " --> " + on_off);
                    if (((Double) on_off).equals(1.0)) {
                        bestTurningMovements.add(tm);
                    }
                }
                bestPhase = new Phase(bestTurningMovements);
//                System.out.println("Intersection " + intersection.getId());
//                System.out.println("Best phase: " + bestPhase);
//                System.out.println("Conflicts: " + intersection.getV2vConflicts());

                for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
                    double y_ij = cplex.getValue(flow_vals.get(turn));
                    flowVals.put(turn, y_ij);
//                    flowVals.put(turn, 5);
//                    System.out.println("\t" + turn + " --> " + y_ij);
                }

//                if (intersection.getId() == 2) {
//                    cplex.exportModel("lpex_" + network_time + ".lp");
//                }


//                System.out.println();
//                System.out.println();
            }


        } catch (IloException e) {
            e.printStackTrace();
        }


        return new Tuple(bestPhase, flowVals);
    }
}
