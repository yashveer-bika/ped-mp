package ped;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;


public class Intersection extends VehNode {
    // TODO: implement an IntersectionControl class

    public static boolean PRINT_STATUS = false;
    private Map<Link, Map<Link, Set<ConflictPoint>>> conflictPoints;
    private Map<Location, ConflictPoint> allConflicts;
    private Map<VehLink, List<VehLink>> vehTurns;
    private Map<PedLink, List<PedLink>> pedTurns;
    private HashSet<PedNode> pedIntersections;
    private HashSet<Pedestrian> pedestrians;
    private HashSet<Crosswalk> crosswalks;
    private HashMap<Vehicle, Vehicle> conflictingVehicles;

    // public Map<ConflictPoint, IloNumVar[]> deltas; // NOTE: some Cplex thing
    public Network engine;
    private IntersectionControl control;

    public Intersection(int row, int col, int id, Network engine)
    {
        super(row, col, id, engine);
        conflictPoints = new HashMap<Link, Map<Link, Set<ConflictPoint>>>();
        allConflicts = new HashMap<Location, ConflictPoint>();
        vehTurns = new HashMap<VehLink, List<VehLink>>();
        pedTurns = new HashMap<PedLink, List<PedLink>>();
        crosswalks = new HashSet<Crosswalk>();
        pedIntersections = new HashSet<PedNode>();
        conflictingVehicles = new HashMap<>();
        this.engine = engine;

        // TODO: fill out the vehTurns, pedTurns, etc..

    }


    public Intersection(int id) {
        super(id);
    }

    public void setControl(IntersectionControl c)
    {
        control = c;

        control.setNode(this);
    }


}
