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

        if (intersection.getId() == 5) {
            System.out.println("Intersection 5");
            System.out.println("feasible phases: " + intersection.getFeasiblePhases());

        }

        // iterate over each feasible phase
        for (Phase candidatePhase : intersection.getFeasiblePhases()) {

            // calculate objective function for candidatePhase

            // IloCplex cplex = new IloCplex();


            // CREATE number of vehicles to move through a turning movement
            // y^v_{ij}
            Map<TurningMovement, Integer> vehMoveNums = new HashMap<>();
            for (TurningMovement tm : intersection.getAllTurningMovements()) {
                vehMoveNums.put(tm, 0);
            }

            /*** CONSTRAINTS ***/


            // sets the flow through a turning movement by capping the
            // flow with the capacity of the turn. mov., otherwise letting
            // the flow by the queue length for the turning movement
            // for vehicles
            for (TurningMovement turn : candidatePhase.getTurningMovements()) {
                // IloLinearNumExpr expr68e = cplex.linearNumExpr();
                // y^{v}_{ij} = min ( Q^{v}_{ij} * s^{v}_{ij}, x^{v}_{ij} )
                // but we forced the signal on and only look at non-conflicting turns


                vehMoveNums.put(turn, Math.min(turn.getCapacity(), turn.getQueueLength()) );
                if (intersection.getId() == 5) {
                    System.out.println("tm: " + turn);
                    System.out.println("Q_ij: " + turn.getCapacity());
                    System.out.println("x_ij: " + turn.getQueueLength());
                }
            }


            // calculate vehicle weight ( w^{v}_{ij}(t) )

            // CREATE OBJECTIVE FUNCTION
            double obj_val = 0;
            // IloLinearNumExpr objExpr = cplex.linearNumExpr();
            for (TurningMovement turn : candidatePhase.getTurningMovements()) {
                double v_turn_mov_cap = turn.getCapacity();
                double v_weight_ij = turn.getWeight();
                // objExpr.addTerm(v_turn_mov_cap * v_weight_ij, v_signals.get(turn));
                obj_val += v_turn_mov_cap * v_weight_ij;

            }

            // update optimal (maximal) flows and phase
            if (obj_val > max_value) {
                max_value = obj_val;
                bestPhase = candidatePhase;
                flowVals = vehMoveNums;
            }
        }

//        if (intersection.getId() == 2) {
//            System.out.println("TMs: " + intersection.getAllTurningMovements());
//            System.out.println("id: " + intersection.getId());
//            System.out.println("time: " + network_time);
//            System.out.println("max_value: "  + max_value);
//            System.out.println("bestPhase: " + bestPhase);
//            System.out.println("flowVals: " + flowVals);
//        }

        return new Tuple(bestPhase, flowVals);
    }
}
