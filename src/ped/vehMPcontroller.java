package ped;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import util.Tuple;

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

    public Tuple<Phase, Map<TurningMovement, Integer>> run() {
        // iterate over every phase
        Phase bestPhase = new Phase();
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

            // objective function max \sum{ s_{ij} * Q_{ij} * w_{ij} }
            IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : intersection.getVehicleTurningMovements()) {
                double v_turn_mov_cap = turn.getCapacity();
                double v_weight_ij = turn.getWeight();
                objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
            }
            cplex.addMaximize(objExpr);


            if (cplex.solve()) {
                System.out.println("Optimal value = " + cplex.getObjValue());
                for (TurningMovement turn : v_signals.keySet()) {
                    IloIntVar v_sig = v_signals.get(turn);
                    System.out.println("vehicle signal @ " + turn + " = " + cplex.getValue(v_sig));
                }
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
            }


        } catch (IloException e) {
            e.printStackTrace();
        }


        return new Tuple(bestPhase, flowVals);
    }
}
