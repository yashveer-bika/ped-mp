package ped;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import util.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class pedMPcontroller implements Controller {
    // variables
    Intersection intersection;

    public pedMPcontroller(Intersection intersection) {
        this.intersection = intersection;
    }


    public Tuple<Phase, Map<TurningMovement, Double>> run() {
        // iterate over every phase
        Phase bestPhase = null;
        Map<TurningMovement, Double> flowVals = new HashMap<>();

//        System.out.println("INTERSECTION " + intersection.getId());
//        System.out.println("\t\t" + intersection.getInternalVehicleTurns());

        PrintStream ps = null;
        try {
            IloCplex cplex = new IloCplex();

            ps = new PrintStream(Params.getCplexOutPath());
            cplex.setOut(ps);

            // CREATE VEHICLE SIGNALS (s_ij)
            Map<TurningMovement, IloIntVar> v_signals = new HashMap<>();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_{" + turn + "}");
                v_signals.put(turn, v_signal_ij);
            }
            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_{" + turn + "}");
                v_signals.put(turn, v_signal_ij);
            }
            for (TurningMovement turn : intersection.getEntryTurns()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_{" + turn + "}");
                v_signals.put(turn, v_signal_ij);
                cplex.addEq(v_signal_ij, 1); // no-conflicts
            }
            for (TurningMovement turn : intersection.getExitTurns()) {
                IloIntVar v_signal_ij = cplex.intVar(0, 1, "s_{" + turn + "}");
                v_signals.put(turn, v_signal_ij);
                 cplex.addEq(v_signal_ij, 1); // no-conflicts
            }

            // CREATE FLOW VALS (y_ij)
            Map<TurningMovement, IloNumVar> flow_vals = new HashMap<>();

            Set<TurningMovement> internalTms = new HashSet<>();
            internalTms.addAll(intersection.getInternalVehicleTurns());
            internalTms.addAll(intersection.getInternalPedestrianTurningMovements());
            for (TurningMovement turn : internalTms) {
                IloNumVar y_ij = cplex.numVar(0, Double.MAX_VALUE, "y_{" + turn + "}");
                flow_vals.put(turn, y_ij);
            }
            for (TurningMovement turn : intersection.getEntryTurns()) {
                IloNumVar y_ij = cplex.numVar(0, Double.MAX_VALUE, "y_{" + turn + "}");
                flow_vals.put(turn, y_ij);
            }

//            for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
//                IloNumVar y_ij = cplex.numVar(0, Double.MAX_VALUE, "y_{" + turn + "}");
//                flow_vals.put(turn, y_ij);
//            }

            // objective function max \sum{ s_{ij} * Q_{ij} * w_{ij} }
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
                double v_turn_mov_cap = turn.getCapacity();
                double v_weight_ij = turn.getWeight();
//                System.out.println("weight of " + turn + " --> " + v_weight_ij);
//                System.out.println("\tcapacity " + turn.getCapacity());
                // if weight == 0 and the turn doesn't add additional conflict, then add into turns???
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
            }
            cplex.addMaximize(objExpr);

            // turning movement conflict constraint
//            System.out.println("CONFLICTS");
//            for ( TurningMovement tm1 : intersection.getV2vConflicts().keySet() ) {
//                for ( TurningMovement tm2 : intersection.getV2vConflicts().get(tm1).keySet() ) {
//                    System.out.println("\t" + tm1 + " --- " + tm2 + " --> " + intersection.getV2vConflicts().get(tm1).get(tm2) );
//                }
//            }
            for (TurningMovement tm1 : v_signals.keySet()) {
//                System.out.println("tm1: " + tm1);
                Map<TurningMovement, Integer> conflictMap = intersection.getV2vConflicts().get(tm1);
                if (conflictMap == null) {
                    continue;
                }
                for (TurningMovement tm2 : conflictMap.keySet()) {
                    // if tm1 and tm2 conflict
                    if (conflictMap.get(tm2) == 0 && !tm1.equals(tm2)) {
                        IloLinearNumExpr e = cplex.linearNumExpr();
                        e.addTerm(1, v_signals.get(tm1));
                        e.addTerm(1, v_signals.get(tm2));
                        cplex.addLe(e, 1, "turn. mov. conflict: " + tm1 + " --> " + tm2);
                    }
                }
            }
//
            for (TurningMovement tm1 : v_signals.keySet()) {
//                System.out.println("tm1: " + tm1);
                Map<TurningMovement, Integer> conflictMap = intersection.getVehPedConflicts().get(tm1);
                if (conflictMap == null) {
                    continue;
                }
                for (TurningMovement tm2 : conflictMap.keySet()) {
                    // if tm1 and tm2 conflict
                    if (conflictMap.get(tm2) == 0 && !tm1.equals(tm2)) {
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
//            System.out.println();
            for (TurningMovement turn : internalTms) {
                IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                expr68e.addTerm(turn.getCapacity(), v_signals.get(turn));
//                System.out.println("\t" + turn + " --> signal --> " + v_signals.get(turn) );
//                System.out.println("\t" + turn + " --> " + turn.getQueueLength());
//                System.out.println("\t" + "capacity" + " --> " + turn.getCapacity());
//                System.out.println("\t" + cplex.min(expr68e, turn.getQueueLength() ) );
//                System.out.println("\t" + flow_vals.get(turn) );

                cplex.addEq(flow_vals.get(turn), cplex.min(expr68e, turn.getQueueLength() ) );
            }

            for (TurningMovement turn : intersection.getEntryTurns()) {
                IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                expr68e.addTerm(turn.getCapacity(), v_signals.get(turn));
//                System.out.println("\t" + turn + " --> signal --> " + v_signals.get(turn) );
//                System.out.println("\t" + turn + " --> " + turn.getQueueLength());
//                System.out.println("\t" + "capacity" + " --> " + turn.getCapacity());
//                System.out.println("\t" + cplex.min(expr68e, turn.getQueueLength() ) );
//                System.out.println("\t" + flow_vals.get(turn) );

                cplex.addEq(flow_vals.get(turn), cplex.min(expr68e, turn.getQueueLength() ) );
            }


            // TOLERANCE TIME CONSTRAINT
            for (TurningMovement tm: intersection.getPedestrianTurningMovements()) {
                if (tm.forceOn()) {
                    cplex.addEq(v_signals.get(tm), 1);
                }
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
//                    System.out.println(tm);
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


                for (TurningMovement turn : flow_vals.keySet()) {
//                    System.out.println(flow_vals.get(turn));
//                    System.out.println(cplex.getValue(flow_vals.get(turn)));

                    double y_ij = cplex.getValue(flow_vals.get(turn));
                    flowVals.put(turn, y_ij);
//                    flowVals.put(turn, 5);
//                    System.out.println("\t" + turn + " --> " + y_ij);
                }


//                for (TurningMovement turn : intersection.getInternalVehicleTurns()) {
//                    double y_ij = cplex.getValue(flow_vals.get(turn));
//                    flowVals.put(turn, y_ij);
////                    flowVals.put(turn, 5);
////                    System.out.println("\t" + turn + " --> " + y_ij);
//                }
//                for (TurningMovement turn : intersection.getPedestrianTurningMovements()) {
//                    double y_ij = cplex.getValue(flow_vals.get(turn));
//                    flowVals.put(turn, y_ij);
////                    flowVals.put(turn, 5);
////                    System.out.println("\t" + turn + " --> " + y_ij);
//                }


//                if (intersection.getId() == 2) {
//                    cplex.exportModel("lpex_" + network_time + ".lp");
//                }


//                System.out.println();
//                System.out.println();
            }


        } catch (IloException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        ps.close();

        return new Tuple(bestPhase, flowVals);
    }
}
