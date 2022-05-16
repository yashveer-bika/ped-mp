package ped;


import ilog.concert.IloIntVar;
import util.Tuple;

import java.util.Map;
import java.util.Set;

interface Controller {
    Tuple<Phase, Map<TurningMovement, Integer>> run();

    void updateTime(double newTime);
}
