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
