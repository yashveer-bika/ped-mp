package ped;

import ilog.concert.IloIntVar;
import util.Angle;
import util.DoubleE;
import util.PowerSet;
import util.Tuple;

import java.util.*;

public class Intersection {
    private final VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private final Set<PedNode> pedNodes;
    private final Set<Node> allNodes;

    private List<ConflictRegion> conflictRegions;


    //    private final HashSet<Link> allLinks;
    private Set<TurningMovement> vehicleTurns;
    private final Set<TurningMovement> pedestrianTurningMovements;
    private final Set<TurningMovement> allTurningMovements;

    private final HashMap<Node, Set<TurningMovement>> node_to_tms;

    // private final Set<Crosswalk> crosswalks;
    private Set<Phase> feasiblePhases;
    private Set<Phase> possiblePhases;

    private final Controller controller;
    private Phase currentPhase;
    private Map<TurningMovement, Integer> newFlowVals;

    // vehicle tm, pedestrian tm, 0/1 (0 if conflict, 1 if allowed)
    private final Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
    private final Map<TurningMovement, Map<TurningMovement, Integer>> v2vConflicts;
    private final HashMap<TurningMovement, Double> vehQueueLengths;
    private final HashMap<TurningMovement, Double> pedQueueLengths;
    private final HashMap<TurningMovement, Double> pedTurnMovCaps;
//    private boolean loadingComplete = false;
    private boolean ped; // says whether or not we allow pedestrians in this intersection
    // this is the Q_c defined in the paper
    private double capacityConflictRegion_Qc;
    private int num_forks;


    public Intersection(VehIntersection vehInt, String controllerType) {
        this.conflictRegions = new ArrayList<>();
        this.possiblePhases = new HashSet<>();
        this.currentPhase = new Phase();
        this.newFlowVals = new HashMap<>();
        this.pedNodes = new HashSet<>();
        this.allNodes = new HashSet<>();
        this.node_to_tms = new HashMap<>();
        this.feasiblePhases = new HashSet<>();
        this.vehInt = vehInt;
        this.vehicleTurns = new HashSet<>();
        vehInt.generateVehicleTurns();
        this.vehicleTurns = vehInt.getVehicleTurns();
        this.pedestrianTurningMovements = new HashSet<>();
        this.allTurningMovements = new HashSet<>();
        this.vehPedConflicts = new HashMap<>();
        this.v2vConflicts = new HashMap<>();
        this.vehQueueLengths = new HashMap<>();
        this.pedQueueLengths = new HashMap<>();
        this.pedTurnMovCaps = new HashMap<>();
        this.ped = false;
//        this.crosswalks = new HashSet<>();
        // this.conflictMap = new HashMap<Integer, Set<Integer>>();
//        this.allLinks = new HashSet<>();

        if (controllerType.equals("vehMP")) {
            this.controller = new vehMPcontroller(this);
        } else {
            // TODO: write other cases
            this.controller = new vehMPcontroller(this);
        }


//        // get all vehicle links
//        allLinks.addAll(vehInt.getIncomingLinks());
//        allLinks.addAll(vehInt.getOutgoingLinks());

        // TODO: verify the purpose of the code below
        // TODO: move this into the conflict region class
        // find the capacity of the conflict region
        // capacityConflictRegion_Qc
        // max Q_{ij} where ij is a turning movement
        double maxTurn = Double.MIN_VALUE;
        for (TurningMovement t : this.vehicleTurns) {
            double temp = t.getCapacity();
            if (temp > maxTurn) {
                maxTurn = temp;
            }
        }
        capacityConflictRegion_Qc = maxTurn;
        generateV2Vconflicts();
    }


    public Intersection(VehIntersection vehInt, ArrayList<PedIntersection> pedInts,
                        Set<Crosswalk> crosswalks, Set<PedNode> pedNodes, String controllerType) {
        this.ped = true;
        this.conflictRegions = new ArrayList<>();
        this.possiblePhases = new HashSet<>();
        this.allNodes = new HashSet<>();
        this.node_to_tms = new HashMap<>();
        this.feasiblePhases = new HashSet<>();
        this.pedNodes = pedNodes;
        this.vehInt = vehInt;
        // assert pedInts.size() == 4;
        this.pedInts = pedInts;
        this.vehicleTurns = new HashSet<>();
        vehInt.generateVehicleTurns();
        this.vehicleTurns = vehInt.getVehicleTurns();
        this.pedestrianTurningMovements = new HashSet<>();
        this.allTurningMovements = new HashSet<>();
        this.vehPedConflicts = new HashMap<>();
        this.v2vConflicts = new HashMap<>();
        this.vehQueueLengths = new HashMap<>();
        this.pedQueueLengths = new HashMap<>();
        this.pedTurnMovCaps = new HashMap<>();
//        this.crosswalks = crosswalks;
//        this.allLinks = new HashSet<>();
        if (controllerType.equals("vehMP")) {
            this.controller = new vehMPcontroller(this);
        } else {
            // TODO: write other cases
            this.controller = new vehMPcontroller(this);
        }

//        // get all the pedestrian links
//        for (PedIntersection pedInt : pedInts) {
//            allLinks.addAll(pedInt.getIncomingLinks());
//            allLinks.addAll(pedInt.getOutgoingLinks());
//        }

//        // get all vehicle links
//        allLinks.addAll(vehInt.getIncomingLinks());
//        allLinks.addAll(vehInt.getOutgoingLinks());
    }

    public List<ConflictRegion> getConflictRegions() {
        return conflictRegions;
    }

    public Set<Node> getAllNodes() {
        return this.allNodes;
    }

    public void setNumForks() {
        Set<Node> neighs = new HashSet<>();
        for (Link l : vehInt.getIncomingLinks()) {
            neighs.add( l.getSource() );
        }
        for (Link l : vehInt.getOutgoingLinks()) {
            neighs.add( l.getDest() );
        }
        if (getId() == 2) {
            System.out.println("neighbors: " + neighs);
        }
        num_forks = neighs.size();
    }

    public int getNum_forks() {
        return num_forks;
    }

    public void initializeNodeTMs() {
        // add nodes
//        System.out.println("this.getPedNodes(): " + this.getPedNodes());
//        System.out.println("this.vehInt: " + this.vehInt);
        allNodes.addAll(this.getPedNodes());
        allNodes.add(this.vehInt);

        for (Node n : allNodes) {
            node_to_tms.put(n, new HashSet<>());
        }

        for (TurningMovement tm : this.getAllTurningMovements()) {
            Node n = tm.getIncomingLink().getDest();
            // see if
            Set<TurningMovement> tms = node_to_tms.get(n);
            tms.add(tm);
            node_to_tms.put(n, tms);
        }
    }

    private List<Double> getAngles() {
//        System.out.println("\tGETTING ANGLES");

        List<Double> angles = new ArrayList<>();
        double temp;
        for (Link i : vehInt.getIncomingLinks()) {
            temp = Angle.bound(Math.PI*1 + i.getAngle());
//            System.out.println("\t\t" + temp);
            boolean inSet = false;
            for (double ang : angles) {
                if (DoubleE.equals(ang, temp)) {
                    inSet = true;
                    break;
                }
            }
            if (!inSet) {
                angles.add(temp);
            }
        }
        for (Link j : vehInt.getOutgoingLinks()) {
            temp = Angle.bound(Math.PI*0 + j.getAngle());
//            System.out.println("\t\t" + temp);
            boolean inSet = false;
            for (double ang : angles) {
                if (DoubleE.equals(ang, temp)) {
                    inSet = true;
                    break;
                }
            }
            if (!inSet) {
                angles.add(temp);
            }
        }

        Collections.sort(angles);
        return angles;
    }

    // precondition: boundaryAngles is sorted from smallest to largest, boundaryAngles isn't empty (size >= 1)
    // boundaryAngles' values and angle are bound by [0, 2*PI)
    private int getConflictRegion(double angle, List<Double> boundaryAngles) {
        assert boundaryAngles != null;
        // TODO: return the integer associated with this angle given the boundary angles
        int len = boundaryAngles.size();
        assert len >= 1;

//        System.out.println("boundary angles: " + boundaryAngles);
//        System.out.println("input angle: " + angle);
        for (int i = 0; i < len; i++) {
            if (angle < boundaryAngles.get(i)) {
                return i;
            }
        }
        // TODO: put a picture and javadoc explaining why we pick id=0 after the largest angle
        return 0;
    }

    private List<Double> bisectAngles(List<Double> linkAngles) {
//        System.out.println("\tBISECTING ANGLES");
//        System.out.println("\t\tinput angles" + linkAngles);

        List<Double> bisectAngles = new ArrayList<>();
        double temp;
        int l = linkAngles.size();
        for (int i=0 ; i < l-1 ; i++) {
            temp = linkAngles.get(i) + linkAngles.get(i+1);
            temp /= 2;
            temp = Angle.bound(temp);
            bisectAngles.add(temp);
        }
        temp = linkAngles.get(0) + Math.PI*2 + linkAngles.get(l-1);
        temp /= 2;
        temp = Angle.bound(temp);
        bisectAngles.add(temp);

         Collections.sort(bisectAngles);
//        System.out.println("\t\tsplit angles" + bisectAngles);
        return bisectAngles;
    }

    private List<Double> getBoundaryAngles() {
        return bisectAngles(getAngles());
    }

    public void initializeConflictRegions() {
//        System.out.println("Intersection " + getId());
//        System.out.println("\tgetNum_forks " + getNum_forks());

        // Precondition: assume that we have all of our vehicle turning movements
        // stored in vehicleTurns
        List<Double> boundaryAngles = getBoundaryAngles();

        assert boundaryAngles.size() == getNum_forks();
//        System.out.println("\tboundaryAngles " + boundaryAngles);

        for (int i = 0; i < getNum_forks(); i++) {
            conflictRegions.add(new ConflictRegion());
        }
        // assume boundaryAngles is already populated and sorted from smallest to largest
        for (TurningMovement tm : vehicleTurns) {
//            System.out.println("\ttm " + tm);
//            System.out.println("\tboundaryAngles " + boundaryAngles);
            double inAng = Angle.bound(tm.getIncomingLink().getAngle() + Math.PI);
//            System.out.println("\tinAng " + inAng);

            int inId = getConflictRegion(inAng, boundaryAngles);
            double outAng = Angle.bound(tm.getOutgoingLink().getAngle());
//            System.out.println("\toutAng " + outAng);

            int outId = getConflictRegion(outAng, boundaryAngles);
//            System.out.println("\tinId " + inId);
//            System.out.println("\toutId " + outId);

            if (outId < inId) {
                for (int i = inId; i < conflictRegions.size(); i++) {
                    conflictRegions.get(i).addTM(tm);
//                    System.out.println("added a TM to CR");
                }
                for (int i = 0; i <= outId; i++) {
                    conflictRegions.get(i).addTM(tm);
//                    System.out.println("added a TM to CR");
                }
            } else {
                for (int i = inId; i <= outId; i++) {
                    conflictRegions.get(i).addTM(tm);
//                    System.out.println("added a TM to CR");
                }
            }

//            for (int i = inId; i <= outId; i++) {
//                conflictRegions.get(i).addTM(tm);
//                System.out.println("added a TM to CR");
//            }

//            for (int i = outId; i < inId; i++) {
//                conflictRegions.get(i).addTM(tm);
//                System.out.println("added a TM to CR");
//            }
        }
        // Postcondition: We generate `numForks` numbers of conflict regions and each conflict region has a list of
        // turning movements

    }

    // makes sure that the phases are generated and code won't be redundantly called
    public void finishLoading() {
//        this.loadingComplete = true;
        setNumForks();

        if (ped) {
            setPedestrianTurningMovements();
            for (PedIntersection pi : pedInts) {
                pi.generatePedestrianTurns();
//                System.out.println("ped turns at " + pi.getId() + ": " + pi.getPedestrianTurns());
                pedestrianTurningMovements.addAll(pi.getPedestrianTurns());
            }
        }
//        System.out.println("pedestrianTurningMovements: " + pedestrianTurningMovements);
        allTurningMovements.addAll(vehicleTurns);
        allTurningMovements.addAll(pedestrianTurningMovements);
        initializeNodeTMs();
        initializeConflictRegions();



//        System.out.println(this.getId());
//        System.out.println("\tallTurningMovements: " + allTurningMovements);
         generatePhases();
//        Map<Link, Map<Link, TurningMovement>> conflicts = ConflictFactory.generate(this);

//        for (Link link_in : conflicts.keySet()) {
//            for (Link link_out : conflicts.get(link_in).keySet()) {
//                TurningMovement tm = conflicts.get(link_in).get(link_out);
//                System.out.println(link_in + " : " + link_out + " : " + tm);
//                System.out.println("\t" + tm.getConflictRegions());
//            }
//        }
        // initialize();
//        System.out.println("Intersection: " + getId());
//        System.out.println("\tFEASIBLE PHASES: " + feasiblePhases);
    }

//    public void initialize() {
//        // create possiblePhases
//        Set<Set<TurningMovement>> phases_ = PowerSet.powerSet(getAllTurningMovements());
//        for (Set<TurningMovement> ph_ : phases_) {
//            possiblePhases.add(new Phase(ph_));
//        }
//
//
//        // need to create phases here
//        Set<TurningMovement> matched = new HashSet<TurningMovement>();
//
//        Map<Link, Map<Link, TurningMovement>> conflicts = ConflictFactory.generate(this);
//
//
//        // feasiblePhases = new ArrayList<>();
//
//        // look for compatible combinations of 2 turns
//        // then add turns as feasible
//
//        // Set<TurningMovement> turns = getAllTurningMovements();
//        List<TurningMovement> turns = new ArrayList<>();
//        turns.addAll(getAllTurningMovements());
//
//        for (int i = 0; i < turns.size() - 1; i++) {
//            for (int j = i + 1; j < turns.size(); j++) {
//                if (this.getId() == 1) {
//                    System.out.println("turn 1: " + turns.get(i));
//                    System.out.println("turn 2: " + turns.get(j));
//                    System.out.println("are conflicting: " + hasConflicts(turns.get(i), turns.get(j), conflicts));
//                }
//                if (!hasConflicts(turns.get(i), turns.get(j), conflicts)) {
//
//                    List<TurningMovement> allowed = new ArrayList<>();
//                    allowed.add(turns.get(i));
//                    allowed.add(turns.get(j));
//
//                    outer:
//                    for (TurningMovement t : turns) {
//                        if (allowed.contains(t)) {
//                            continue;
//                        }
//
//                        for (TurningMovement t2 : allowed) {
//                            if (hasConflicts(t, t2, conflicts)) {
//                                continue outer;
//                            }
//                        }
//
//                        allowed.add(t);
//                    }
//
//                    for (TurningMovement t : allowed) {
//                        matched.add(t);
//                    }
//                    // Phase p = new Phase(0, allowed, Params.dt - LOST_TIME, 0, LOST_TIME);
//                    Set<TurningMovement> allowedTms = new HashSet<>();
//                    allowedTms.addAll(allowed);
//                    Phase p = new Phase(allowedTms);
//                    feasiblePhases.add(p);
//                }
//            }
//        }
//        for(TurningMovement t : turns)
//        {
//            if(!matched.contains(t))
//            {
//                Phase p = new Phase(new HashSet<>(){{add(t);}});
//                feasiblePhases.add(p);
//            }
//        }
//
//        // remove duplicate phases
//        List<Phase> duplicates = new ArrayList<>();
//        List<Phase> phases = new ArrayList<>();
//        phases.addAll(feasiblePhases);
//        for(int i = 0; i < phases.size()-1; i++)
//        {
//            for(int j = i+1; j < phases.size(); j++)
//            {
//                if(phases.get(i).equals(phases.get(j)))
//                {
//                    duplicates.add(phases.get(j));
//                }
//            }
//        }
//
//        for(Phase p : duplicates)
//        {
//            feasiblePhases.remove(p);
//        }
//    }


    public Set<Phase> getPossiblePhases() {
        return possiblePhases;
    }

//    // NOTE: FROM Michael's AVDTA code
//    public boolean hasConflicts (TurningMovement t1, TurningMovement t2, Map<Link, Map<Link, TurningMovement>> conflicts)
//    {
//        if (getId() == 1) {
//
//        }
//
//        // diverge links don't conflict
//        if(t1.getIncomingLink() == t2.getIncomingLink())
//        {
//            return false;
//        }
//
//        // TODO: what does this case mean?
////        if(t1.getOutgoingLink() == t2.getOutgoingLink() && getVehInt().getOutgoingLinks().size() == 2)
////        {
////            return false;
////        }
//
//
//        Set<ConflictRegion> c1 = conflicts.get(t1.getIncomingLink()).get(t1.getOutgoingLink()).getConflictRegions();
//        if (getId() == 1) {
//            System.out.println("c1 from mapping: " + c1);
//            System.out.println("c1 from tm object: " + t1.getConflictRegions());
//        }
////        assert c1.equals(t1.getConflictRegions());
//        Set<ConflictRegion> c2 = conflicts.get(t2.getIncomingLink()).get(t2.getOutgoingLink()).getConflictRegions();
////        assert c2.equals(t2.getConflictRegions());
//
//        if (getId() == 1) {
//            System.out.println("c2 from mapping: " + c2);
//            System.out.println("c2 from tm object: " + t2.getConflictRegions());
//        }
//
////        if (getId() == 1) {
////            System.out.println("t1 conflicts: " + c1);
////            System.out.println("t2 conflicts: " + c2);
////        }
//
//        Set<ConflictRegion> intersection = new HashSet<>();
//
//        for(ConflictRegion c : c1)
//        {
//            if(c2.contains(c))
//            {
//                intersection.add(c);
//            }
//        }
//
//        // no overlapping conflict regions
//        if(intersection.size() == 0)
//        {
//            return false;
//        }
//
////        // if the two turning movements don't share links, they can't conflict
////        if (!t1.shareLinks(t2)) {
////            return false;
////        }
//
//        // 2 left turns that don't share links
//        if(c1.size() <= 3 && c2.size() <= 3 &&
//                t1.getIncomingLink() != t2.getIncomingLink() &&
//                t1.getOutgoingLink() != t2.getOutgoingLink() &&
//                t1.getIncomingLink().getSource() != t2.getOutgoingLink().getDest() &&
//                t1.getOutgoingLink().getDest() != t1.getIncomingLink().getSource()
//        )
//        {
//            return false;
//        }
//
//
//        return true;
//    }

    public Map<TurningMovement, Map<TurningMovement, Integer>> getV2vConflicts() {
        return v2vConflicts;
    }

    /* TODO: test this function */
    private void generateV2Vconflicts() {
        for (TurningMovement veh_tm : vehicleTurns) {
            // Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
            Map<TurningMovement, Integer> tmp = new HashMap<>();
            for (TurningMovement veh_tm2 : vehicleTurns) {
                if (veh_tm.intersects(veh_tm2)) {
                    tmp.put(veh_tm2, 0); // conflict
                } else {
                    tmp.put(veh_tm2, 1); // no-conflict (allowed)
                }
            }
            v2vConflicts.put(veh_tm, tmp);
        }

        // print out vehicle turns
//        for (TurningMovement veh_tm : vehicleTurns) {
//            for (TurningMovement veh_tm2 : vehicleTurns) {
//                int signal = v2vConflicts.get(veh_tm).get(veh_tm2);
//                System.out.println("\t" + veh_tm + " : " + veh_tm2 + " : " + signal);
//            }
//        }

    }

    private boolean areConflicting(TurningMovement tm, Set<TurningMovement> tms) {
        Set<TurningMovement> conflictingTms = getConflicts(tm);
//        System.out.println("Conflicting Tms: " + conflictingTms);
//        System.out.println("tms: " + tms);
        Set<TurningMovement> intersection = new HashSet<>(conflictingTms);
        intersection.retainAll(tms);
//        System.out.println("set intersection: " + intersection);
        return intersection.size() != 0;
    }

    // TODO: make sure it works for ped_tm and veh_tm's
    // NOTE: this only works for veh_tms
    private Set<TurningMovement> getConflicts(TurningMovement tm) {
        Set<TurningMovement> conflicts = new HashSet<>();
        // get vehped conflicts
        // get v2v conflicts
        // Map<TurningMovement, Map<TurningMovement, Integer>> getVehPedConflicts()

//        System.out.println("v2p conflicts: " + getVehPedConflicts());
        Map<TurningMovement, Integer> conflicts_on_turn_p = getVehPedConflicts().get(tm);
//        System.out.println("V2P conflicts for intersection " + getId() + ": " + conflicts_on_turn_p);
        if (conflicts_on_turn_p != null) {
            for (TurningMovement key : conflicts_on_turn_p.keySet()) {
                if (conflicts_on_turn_p.get(key) == 0) { // 0 means intersections
                    conflicts.add(key);
                }
            }
        }
        Map<TurningMovement, Integer> conflicts_on_turn_v = getV2vConflicts().get(tm);
        if (conflicts_on_turn_v != null) {
            for (TurningMovement key : conflicts_on_turn_v.keySet()) {
                if (conflicts_on_turn_v.get(key) == 0) { // 0 means intersections
                    conflicts.add(key);
                }
            }
        }

        return conflicts;
    }

    public Set<Set<TurningMovement>> powerSetWFilter(Set<TurningMovement> originalSet) {
        Set<Set<TurningMovement>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<TurningMovement> list = new ArrayList<>(originalSet);
        TurningMovement head = list.get(0);
        Set<TurningMovement> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<TurningMovement> set : powerSetWFilter(rest)) {
            Set<TurningMovement> newSet = new HashSet<>();
            // if head conflicts with anything in the set, we skip this case
            sets.add(set);
            newSet.add(head);
            newSet.addAll(set);
            if (!areConflicting(head, set)) { // only add if non-conflicting
                sets.add(newSet);
            }

        }
        return sets;
    }

    private void generatePhases() {
//        if (this.getId() == 5) {
//            System.out.println("Turning movements: " + getAllTurningMovements());
//        }
//        System.out.println("Intersection: " + getId());
//        System.out.println("Turning movements: " + getAllTurningMovements());

        // Set<Set<TurningMovement>> possiblePhases = PowerSet.powerSet(this.allTurningMovements);
        // this.feasiblePhases = filterFeasiblePhases(possiblePhases);
        // TODO: verify this alg.
        // create a power set that checks conflicts before adding to set
        // System.out.println("Intersection: " + this.getId());
//         Set<Set<TurningMovement>> tms = powerSetWFilter(this.allTurningMovements);
        List<Set<TurningMovement>> tms = new ArrayList<>();
//        for (Set<TurningMovement> posPhase : PowerSet.powerSet(this.allTurningMovements)) {
        for (Set<TurningMovement> posPhase : powerSetWFilter(this.allTurningMovements)) {

        if (!containsConflicts(posPhase)) {
                tms.add(posPhase);
            }
        }

        class sortSetTM implements Comparator<Set<TurningMovement>>
        {
            // Used for sorting in ascending order of
            // roll number
            public int compare(Set<TurningMovement> a, Set<TurningMovement> b)
            {
                return a.size() - b.size();
            }
        }

        Collections.sort(tms, new sortSetTM());






        // remove any subsets of the largest sets (phases with less movements are a waste of time)
        // very slow
//         this.feasiblePhases = removeRedundantPhases(tms);

//        Set<Set<TurningMovement>> tms_copy = new HashSet<>(tms);
//        // based on the number of forks, filter out tiny phases
//        for (Set<TurningMovement> phase : tms_copy) {
//            if (num_forks == 4 && phase.size() < 4) {
//                // only in SF case with vehicles only
//                tms.remove(phase);
//            }
//
//            else if (phase.size() < num_forks - 1) {
//                tms.remove(phase);
//            }
//        }

        for (Set<TurningMovement> t_phase : tms) {
            feasiblePhases.add(new Phase(t_phase));
        }
    }

    public boolean containsConflicts(Set<TurningMovement> tms) {
//        System.out.println("Potential phase: " + tms);
//        System.out.println("v2v conflicts: " + v2vConflicts);
        List<TurningMovement> tms_copy = new ArrayList<>();
        tms_copy.addAll(tms);
        for (int i = 0; i < tms_copy.size(); i++) {
            for (int j = i+1; j < tms_copy.size(); j++) {
                TurningMovement tm1 = tms_copy.get(i);
                TurningMovement tm2 = tms_copy.get(j);
//                System.out.println(tm1);
//                System.out.println(v2vConflicts.get(tm1));
                if (v2vConflicts.get(tm1) == null) {
                    return false;
                }
                if (v2vConflicts.get(tm1).get(tm2) == 0) {
                    return true;
                }
                // TODO: incorporate pedestrians
            }
        }
        return false;
    }



    public void setPedestrianTurningMovements() {
        assert this.pedestrianTurningMovements.size() == 0;
        for (PedIntersection pedInt : this.pedInts) {
            this.pedestrianTurningMovements.addAll(pedInt.getPedestrianTurns());
        }
    }

    // NOTE: not efficient since this searchs over the sidewalks and may search over the
    // same vehLinks multiple times
    public void setVehPedConflicts() {
        for (TurningMovement veh_tm : vehicleTurns) {
            // Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
            Map<TurningMovement, Integer> tmp = new HashMap<>();
            for (TurningMovement ped_tm : pedestrianTurningMovements) {
                if (veh_tm.intersects(ped_tm)) {
                    tmp.put(ped_tm, 0);
                } else {
                    tmp.put(ped_tm, 1);
                }
            }
            vehPedConflicts.put(veh_tm, tmp);
        }
    }

    public Set<TurningMovement> getAllTurningMovements() {
        return allTurningMovements;
    }

    public HashMap<TurningMovement, Double> getPedTurnMovCaps() {
        return pedTurnMovCaps;
    }

    // veh_tm, ped_tm, 0/1
    public Map<TurningMovement, Map<TurningMovement, Integer>> getVehPedConflicts() {
        setVehPedConflicts();
        return this.vehPedConflicts;
    }

    public HashMap<TurningMovement, Double> getVehQueueLengths() {
        return vehQueueLengths;
    }

    public HashMap<TurningMovement, Double> getPedQueueLengths() {
        return pedQueueLengths;
    }

    public Set<PedNode> getPedNodes() {
        return this.pedNodes;
    }

    public Set<TurningMovement> getPedestrianTurningMovements() {
        return this.pedestrianTurningMovements;
    }

    public Set<TurningMovement> getVehicleTurningMovements() {
        return this.vehicleTurns;
    }
    public Set<TurningMovement> getVehicleTurns() {
        return this.vehicleTurns;
    }

    public VehIntersection getVehInt() {
        return this.vehInt;
    }


//    @Override
//    public String toString() {
//        return "Intersection{" +
//                "vehInt=" + vehInt +
//                ", pedInts=" + pedInts +
//                ", \n\tallLinks=" + allLinks +
//                ", \n\tvehicleTurns=" + vehicleTurns +
//                ", \n\tcrosswalks=" + crosswalks +
//                ", \n\tcontroller=" + controller +
//                '}';
//    }

    public void updateTime(double newTime) {
        controller.updateTime(newTime);
        vehInt.updateTime(newTime);
        if (ped) {
            for (PedIntersection pedInt : pedInts) {
                pedInt.updateTime(newTime);
            }
        }
    }

    public void iterateTimeStep() {

    }

    public void runController() {
        Tuple<Phase, Map<TurningMovement, Integer>> out = controller.run();
        currentPhase = out.getX();
        newFlowVals = out.getY();
    }

    public int getId() {
        return this.getVehInt().getId();
    }

    public Set<Phase> getFeasiblePhases() {
        return feasiblePhases;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public void setNewFlowVals(Map<TurningMovement, Integer> newFlowVals) {
        this.newFlowVals = newFlowVals;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public Map<TurningMovement, Integer> getNewFlowVals() {
        return newFlowVals;
    }

    public void moveVehicles() {
//        System.out.println("\tMOVING VEHICLES");
        System.out.println("\tID: " + getId());
//        System.out.println("\tphase: " + getCurrentPhase());
        System.out.println("\tflow vals: " + getNewFlowVals());

//        System.out.println("tms: " + getNewFlowVals().keySet().size());
        for (TurningMovement tm : getNewFlowVals().keySet()) {
//            System.out.println("tm: " + tm);
//            System.out.println("vehs: " + tm.getVehicles().size());
            int x_ij = tm.getVehicles().size();
            int y_ij = getNewFlowVals().get(tm);
            double turn_prop = tm.getTurningProportion();
//            System.out.println("x_ij: " + x_ij);
//            System.out.println("y_ij: " + y_ij);
            // ensure that y_ij <= x_ij
//            if (y_ij.compareTo(x_ij) > 0) {
//                assert false;
//            }
            assert y_ij <= x_ij;


            Link i = tm.getIncomingLink();
            Link j = tm.getOutgoingLink();
            int idx = 0;
            for (Vehicle v : tm.getVehicles()) {
//                System.out.println(v);
//                System.out.println(v.getCurrentNode().getId());
                if (idx >= y_ij * turn_prop) {
                    break;
                }
//                System.out.println(v);
//                System.out.println(v.getCurrentNode().getId());
                Node n = v.moveVehicle();
                if (n != null) {
                    j.addVehicle(v);
                }
                i.removeVehicle(v);

//                System.out.println(v.getCurrentNode().getId());
                idx += 1;
            }

        }
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "vehInt=" + vehInt.getLocation() + "";
    }

}
