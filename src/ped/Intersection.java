package ped;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import Geometry.Geometry;
import util.PowerSet;

public class Intersection {
    private VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private Set<PedNode> pedNodes;
    private Set<Node> allNodes;

    private HashSet<Link> allLinks;
    private Set<TurningMovement> vehicleTurns;
    private Set<TurningMovement> pedestrianTurningMovements;
    private Set<TurningMovement> allTurningMovements;

    private HashMap<Node, Set<TurningMovement>> node_to_tms;

    private Set<Crosswalk> crosswalks;
    // private Set<Set<Phase>> setOfFeasiblePhaseGrouping; // a set of a set of phase that can run at once
    private Set<Phase2> feasiblePhases;
    private Controller controller;
    // vehicle tm, pedestrian tm, 0/1
    private Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
    private Map<TurningMovement, Map<TurningMovement, Integer>> v2vConflicts;
    private HashMap<TurningMovement, Double> vehQueueLengths;
    private HashMap<TurningMovement, Double> pedQueueLengths;
    private HashMap<TurningMovement, Double> pedTurnMovCaps;
    private boolean loadingComplete = false;
    private boolean ped; // says whether or not we allow pedestrians in this intersection



    // this is the Q_c defined in the paper
    private double capacityConflictRegion_Qc;


    // private Set<VecPhase> vecPhases;


    // private Set<Turn> allTurns; // DEPRECATED

    private HashMap<String, Set<String>> conflictingVehicleDirections;
    // private HashMap<Integer, Set<Integer>> conflictMap; // includes veh and ped
    private HashMap<Integer, String> numToDirectionMap;
    private HashMap<String, Integer> directionToNumMap;
    private HashMap<Phase, Integer> phaseToNumMap;
    private HashMap<Integer, Phase> numToPhaseMap;
    private int num_forks;

//    public Intersection() {
//
//    }

    public Intersection(VehIntersection vehInt) {
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
        this.crosswalks = crosswalks;
        // this.conflictMap = new HashMap<Integer, Set<Integer>>();
        this.allLinks = new HashSet<Link>();
        // TOOD: a vehicle only MP controller
        this.controller = new vehMPcontroller(this);

        // get all vehicle links
        allLinks.addAll(vehInt.getIncomingLinks());
        allLinks.addAll(vehInt.getOutgoingLinks());

        // TODO: verify the purpose of the code below
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
        generateV2Vconflicts();
    }


    public Intersection(VehIntersection vehInt, ArrayList<PedIntersection> pedInts,
                        Set<Crosswalk> crosswalks, Set<PedNode> pedNodes) {
        this.allNodes = new HashSet<>();
        this.node_to_tms = new HashMap<>();
        this.feasiblePhases = new HashSet<>();
        this.pedNodes = pedNodes;
        this.vehInt = vehInt;
        assert pedInts.size() == 4;
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

        this.crosswalks = crosswalks;
        // this.conflictMap = new HashMap<Integer, Set<Integer>>();
        this.allLinks = new HashSet<Link>();
        this.controller = new pedMPcontroller(this);

        // get all the pedestrian links
        if (pedInts != null) {
            for (PedIntersection pedInt : pedInts) {
                allLinks.addAll( ((PedNode) pedInt).getIncomingLinks() );
                allLinks.addAll( ((PedNode) pedInt).getOutgoingLinks() );
            }
        }
        // get all vehicle links
        allLinks.addAll(vehInt.getIncomingLinks());
        allLinks.addAll(vehInt.getOutgoingLinks());


//        // create numToDirectionMap
//        numToDirectionMap = new HashMap<>();
//        numToDirectionMap.put(0, "NS");
//        numToDirectionMap.put(1, "NW");
//        numToDirectionMap.put(2, "NE");
//        numToDirectionMap.put(3, "EN");
//        numToDirectionMap.put(4, "ES");
//        numToDirectionMap.put(5, "EW");
//        numToDirectionMap.put(6, "SN");
//        numToDirectionMap.put(7, "SW");
//        numToDirectionMap.put(8, "SE");
//        numToDirectionMap.put(9, "WN");
//        numToDirectionMap.put(10, "WS");
//        numToDirectionMap.put(11, "WE");
//        numToDirectionMap.put(12, "LEFT"); // The crosswalk on the left side
//        numToDirectionMap.put(13, "RIGHT"); // The crosswalk on the right side
//        numToDirectionMap.put(14, "TOP"); // The crosswalk on the top side
//        numToDirectionMap.put(15, "BOTTOM"); // The crosswalk on the bottom side
//
//        // create directionToNumMap (inverse mapping)
//        directionToNumMap = new HashMap<>();
//        for(Map.Entry<Integer, String> entry : numToDirectionMap.entrySet()){
//            directionToNumMap.put(entry.getValue(), entry.getKey());
//        }


        // TODO: verify the purpose of the code below
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

//        // make a hashmap, (mapping turn to phase)
//        this.phaseToNumMap = new HashMap<>();
//        for (Turn turn : this.vehicleTurns) {
//            this.phaseToNumMap.put(turn, turn.getId());
//        }
//        for (Crosswalk cWalk : this.crosswalks) {
//            this.phaseToNumMap.put(cWalk, cWalk.getId());
//        }

//        // create numToPhaseMap (inverse mapping)
//        this.numToPhaseMap = new HashMap<>();
//        for(Map.Entry<Phase, Integer> entry : this.phaseToNumMap.entrySet()){
//            this.numToPhaseMap.put(entry.getValue(), entry.getKey());
//        }



    }

    public void setNumForks() {
        Set<Node> neighs = new HashSet<>();
        for (Link l : vehInt.getIncomingLinks()) {
            neighs.add( l.getStart() );
        }
        for (Link l : vehInt.getOutgoingLinks()) {
            neighs.add( l.getDestination() );
        }
        num_forks = neighs.size();
    }

    public void initializeNodeTMs() {

        // add nodes
        allNodes.addAll(this.getPedNodes());
        allNodes.add(this.vehInt);

        for (Node n : allNodes) {
            node_to_tms.put(n, new HashSet<>());
        }

        for (TurningMovement tm : this.getAllTurningMovements()) {
            Node n = tm.getIncomingLink().getDestination();
            // see if
            Set<TurningMovement> tms = node_to_tms.get(n);
            tms.add(tm);
            node_to_tms.put(n, tms);
        }
    }

    // makes sure that the phases are generated and code won't be redundantly called
    public void finishLoading() {
        this.loadingComplete = true;
        setNumForks();

        if (ped) {
            setPedestrianTurningMovements();
        }
        allTurningMovements.addAll(vehicleTurns);
        allTurningMovements.addAll(pedestrianTurningMovements);
        this.initializeNodeTMs();

//        System.out.println(this.getId());
//        System.out.println("\tallTurningMovements: " + allTurningMovements);
        generatePhases();
    }

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
                    tmp.put(veh_tm2, 0);
                } else {
                    tmp.put(veh_tm2, 1);
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

    private static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
        return setB.containsAll(setA);
    }

    private boolean areConflicting(TurningMovement tm, Set<TurningMovement> tms) {
        Set<TurningMovement> conflictingTms = getConflicts(tm);
//        System.out.println("Conflicting Tms: " + conflictingTms);
//        System.out.println("tms: " + tms);
        Set<TurningMovement> intersection = new HashSet<TurningMovement>(conflictingTms);
        intersection.retainAll(tms);
//        System.out.println("set intersection: " + intersection);
        if (intersection.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasConflicts(Set<TurningMovement> tms) {
        for (TurningMovement tm1 : tms) {
            if (areConflicting(tm1, tms)) {
                return true;
            }
        }
        return false;
    }

    // TODO: make sure it works for ped_tm and veh_tm's
    // NOTE: this only works for veh_tms
    private Set<TurningMovement> getConflicts(TurningMovement tm) {
        Set<TurningMovement> conflicts = new HashSet<>();
        // get vehped conflicts
        // get v2v conflicts
        // Map<TurningMovement, Map<TurningMovement, Integer>> getVehPedConflicts()

        Map<TurningMovement, Integer> conflicts_on_turn_p = getVehPedConflicts().get(tm);
        for (TurningMovement key : conflicts_on_turn_p.keySet()) {
            if (conflicts_on_turn_p.get(key) == 0) { // 0 means intersections
                conflicts.add(key);
            }
        }
        Map<TurningMovement, Integer> conflicts_on_turn_v = getV2vConflicts().get(tm);

        for (TurningMovement key : conflicts_on_turn_v.keySet()) {
            if (conflicts_on_turn_v.get(key) == 0) { // 0 means intersections
                conflicts.add(key);
            }
        }

        return conflicts;
    }

    public Set<Phase2> filterFeasiblePhases(Set<Set<TurningMovement>> possiblePhases) {
        Set<Phase2> feasiblePhases = new HashSet<>();
        // Filter possiblePhases using conflictMap
        for (Set<TurningMovement> possiblePhase : possiblePhases) {
            // if this phase has a conflict, ignore it
            // (1) check for conflict
            boolean hasConflict = false;

            Set<TurningMovement> nonConflicting = new HashSet<>();
            Set<TurningMovement> conflicts;

            for (TurningMovement turn: possiblePhase) {
                // TODO: find the conflicts using the v2v and vehped conflicts
                conflicts = getConflicts(turn);
                // Do set intersection of conflicts and nonconflicting turns
                conflicts.retainAll(nonConflicting);
                // if there is no conflict, our current turn is nonconflicting with
                // previous turns
                if (conflicts.size() == 0) {
                    nonConflicting.add(turn);
                } else { // we have a conflict, must end
                    hasConflict = true;
                    break;
                }
            }
            if (hasConflict) {
                continue;
            } else {
                Phase2 phase = new Phase2(nonConflicting);
                feasiblePhases.add(phase);
            }
        }
        return feasiblePhases;
    }

    // true if setA is a subset of setB
    // setA: [C, D]
    // setB: [C, D, E, F]
    // isSubset: true


    private Set<Phase2> removeRedundantPhases(Set<Set<TurningMovement>> tms) {
        Set<Phase2> feasiblePhases = new HashSet<>();
        for (Set<TurningMovement> t_phase : tms) {
            feasiblePhases.add(new Phase2(t_phase));
        }
        ArrayList<Phase2> feasPhases = new ArrayList<>(feasiblePhases);

        Collections.sort(feasPhases);
        Collections.reverse(feasPhases);
        for (Phase2 phase : feasPhases) {
            for (Phase2 phase_ : feasPhases) {
                if (phase.equals(phase_)) {
                    continue;
                } else {
                    if (phase_.isSubset(phase)) {
                        feasiblePhases.remove(phase);
                        continue;
                    } else {

                    }
                }
            }
        }

        // TODO: update this shit
        return feasiblePhases;
    }

    public <T> boolean existsSubset(Set<T> e, Set<Set<T>> set) {
        for (Set<T> e_ : set) {
            if (isSubset(e, e_)) {
                return true;
            }
        }
        return false;
    }

    public Set<Set<TurningMovement>> powerSetWFilter(Set<TurningMovement> originalSet) {
        Set<Set<TurningMovement>> sets = new HashSet<Set<TurningMovement>>();
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


//    public Set<Set<TurningMovement>> powerSetTopDown(Set<TurningMovement> originalSet) {
//        if (originalSet.size() == 1 || (!hasConflicts(originalSet))) {
//            Set retset = new HashSet();
//            retset.add(originalSet);
//            return retset;
//        } else {
//            Set<TurningMovement> temp = new HashSet<>();
//            temp.addAll(originalSet);
//            Set<Set<TurningMovement>> validPhases = new HashSet<>();
//            for (TurningMovement tm : originalSet) {
//                temp.remove(tm);
//                validPhases.addAll(powerSetTopDown(temp));
//                temp.add(tm);
//            }
//            return validPhases;
//        }
//    }

    private void generatePhases() {
        // Set<Set<TurningMovement>> possiblePhases = PowerSet.powerSet(this.allTurningMovements);
        // this.feasiblePhases = filterFeasiblePhases(possiblePhases);
        // TODO: verify this alg.
        // create a power set that checks conflicts before adding to set
        // System.out.println("Intersection: " + this.getId());
        Set<Set<TurningMovement>> tms = powerSetWFilter(this.allTurningMovements);

        // remove any subsets of the largest sets (phases with less movements are a waste of time)
        // very slow
        // this.feasiblePhases = removeRedundantPhases(tms);

        Set<Set<TurningMovement>> tms_copy = new HashSet<>();
        tms_copy.addAll(tms);
        // based on the number of forks, filter out tiny phases
        for (Set<TurningMovement> phase : tms_copy) {
            if (num_forks == 4 && phase.size() < 4) {
                // only in SF case with vehicles only
                tms.remove(phase);
            }

            else if (phase.size() < num_forks - 1) {
                tms.remove(phase);
            }
        }

        for (Set<TurningMovement> t_phase : tms) {
            this.feasiblePhases.add(new Phase2(t_phase));
        }
    }

    public Set<Phase2> getFeasiblePhases() {
        // generatePhases();
        return feasiblePhases;
    }



    public void setPedestrianTurningMovements() {
        assert this.pedestrianTurningMovements.size() == 0;
        for (PedIntersection pedInt : this.pedInts) {
            for (Link l : pedInt.getIncomingLinks()) {
            }
            pedInt.generatePedestrianTurns();
            this.pedestrianTurningMovements.addAll(pedInt.getPedestrianTurns());
        }
    }


    public void setPedestrianTurningMovements(Set<TurningMovement> pedestrianTurningMovements) {
        this.pedestrianTurningMovements = pedestrianTurningMovements;
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

    public ArrayList<PedIntersection> getPedInts() {
        return pedInts;
    }


//    public void generateVehPedConflictMap() {
//        // the possible vehicle directions are
//        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
//        // 12 possible (0-11)
//        // the possible pedestrian directions are
//        // NS/SN 1/3, NS/SN 2/4, EW/WE 1/2, EW/WE 3/4
//        // 4 possible (12-15)
//        // each phaseSet is a 16-element binary vector
//        //
//        // vehPedConflictMap = new HashMap<String, Set<String>>();
//
//
//        // the possible vehicle directions are
//        // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
//        // conflictMap = new HashMap<Integer, Set<Integer>>();
//
//        // (0) set of directions that conflict with NS
//        Integer[] confDirsNS = {5, 11, 4, 10, 9};
//        List allNS = Arrays.asList(confDirsNS);
//        HashSet<Integer> conflictNS = new HashSet<Integer>(allNS);
//        conflictMap.put(0, conflictNS);
//
//        // (1) set of directions that conflict with SN
//        HashSet<Integer> conflictSN = new HashSet<Integer>();
//        Integer[] confDirsSN = {5, 11, 3, 9, 4};
//        List allSN = Arrays.asList(confDirsSN);
//        conflictSN.addAll(allSN);
//        conflictMap.put(6, conflictSN);
//
//        // (2) set of directions that conflict with EW
//        HashSet<Integer> conflictEW = new HashSet<Integer>();
//        Integer[] confDirsEW = {0, 6, 1, 7, 2};
//        List allEW = Arrays.asList(confDirsEW);
//        conflictEW.addAll(allEW);
//        conflictMap.put(5, conflictEW);
//
//        // (3) set of directions that conflict with WE
//        HashSet<Integer> conflictWE = new HashSet<Integer>();
//        Integer[] confDirsWE = {0, 6, 8, 7, 2};
//        List allWE = Arrays.asList(confDirsWE);
//        conflictWE.addAll(allWE);
//        conflictMap.put(11, conflictWE);
//
//        // (4) set of directions that conflict with NW
//        HashSet<Integer> conflictNW = new HashSet<Integer>();
//        Integer[] confDirsNW = {5, 7};
//        List allNW = Arrays.asList(confDirsNW);
//        conflictNW.addAll(allNW);
//        conflictMap.put(1, conflictNW);
//
//        // (5) set of directions that conflict with WN
//        HashSet<Integer> conflictWN = new HashSet<Integer>();
//        Integer[] confDirsWN = {0, 2, 5, 3, 6, 7};
//        List allWN = Arrays.asList(confDirsWN);
//        conflictWN.addAll(allWN);
//        conflictMap.put(9, conflictWN);
//
//        // (6) set of directions that conflict with NE
//        HashSet<Integer> conflictNE = new HashSet<Integer>();
//        Integer[] confDirsNE = {4, 5, 6, 8, 9, 11};
//        List allNE = Arrays.asList(confDirsNE);
//        conflictNE.addAll(allNE);
//        conflictMap.put(2, conflictNE);
//
//        // (7) set of directions that conflict with EN
//        HashSet<Integer> conflictEN = new HashSet<Integer>();
//        Integer[] confDirsEN = {6, 9};
//        List allEN = Arrays.asList(confDirsEN);
//        conflictEN.addAll(allEN);
//        conflictMap.put(3, conflictEN);
//
//        // (8) set of directions that conflict with ES
//        HashSet<Integer> conflictES = new HashSet<Integer>();
//        Integer[] confDirsES = {0, 2, 6, 7, 10, 11};
//        List allES = Arrays.asList(confDirsES);
//        conflictES.addAll(allES);
//        conflictMap.put(4, conflictES);
//
//        // (9) set of directions that conflict with SE
//        HashSet<Integer> conflictSE = new HashSet<Integer>();
//        Integer[] confDirsSE = {2, 11};
//        List allSE = Arrays.asList(confDirsSE);
//        conflictSE.addAll(allSE);
//        conflictMap.put(8, conflictSE);
//
//        // (10) set of directions that conflict with SW
//        HashSet<Integer> conflictSW = new HashSet<Integer>();
//        Integer[] confDirsSW = {0, 1, 4, 5, 9, 11};
//        List allSW = Arrays.asList(confDirsSW);
//        conflictSW.addAll(allSW);
//        conflictMap.put(7, conflictSW);
//
//        // (11) set of directions that conflict with WS
//        Integer[] confDirsWS = {0, 4};
//        List allWS = Arrays.asList(confDirsWS);
//        HashSet<Integer> conflictWS = new HashSet<Integer>(allWS);
//        conflictMap.put(10, conflictWS);
//
//
//        // (12) set of directions that conflict with pedestrian NS/SN 1/3
//        // all the vehicle turns that move through the west side are conflicting
//        Integer[] confDirsPedLeft = {1, 5, 7, 9, 10, 11};
//        List allPedLeft = Arrays.asList(confDirsPedLeft);
//        HashSet<Integer> conflictPedLeft = new HashSet<Integer>(allPedLeft);
//        conflictMap.put(12, conflictPedLeft);
//
//        // (13) set of directions that conflict with pedestrian NS/SN 2/4
//        // all the vehicle turns that move through the east side are conflicting
//        Integer[] confDirsPedRight = {2, 3, 4, 5, 8, 11};
//        List allPedRight = Arrays.asList(confDirsPedRight);
//        HashSet<Integer> conflictPedRight = new HashSet<>(allPedRight);
//        conflictMap.put(13, conflictPedRight);
//
//        // (14) set of directions that conflict with pedestrian EW/WE 1/2
//        HashSet<Integer> conflictPedTop = new HashSet<Integer>();
//        // all the vehicle turns that move through the north side are conflicting
//        Integer[] confDirsPedTop = {0, 1, 2, 3, 6, 9};
//        List allPedTop = Arrays.asList(confDirsPedTop);
//        conflictPedTop.addAll(allPedTop);
//        conflictMap.put(14, conflictPedTop);
//
//        // (15) set of directions that conflict with pedestrian EW/WE 3/4
//        HashSet<Integer> conflictPedBottom = new HashSet<Integer>();
//        // all the vehicle turns that move through the south side are conflicting
//        Integer[] confDirsPedBottom = {0, 4, 6, 7, 8, 10};
//        List allPedBottom = Arrays.asList(confDirsPedBottom);
//        conflictPedBottom.addAll(allPedBottom);
//        conflictMap.put(15, conflictPedBottom);
//    }

    /*

    public Set<Integer> generatePhaseNums() {
        Set<Integer> allPhaseNums = new HashSet<>();
        allPhaseNums = new HashSet<>();
        for (Phase curTurn : this.vehicleTurns) {
            String curDir = curTurn.getDirection();
            allPhaseNums.add(directionToNumMap.get(curDir));
        }
        //
        for (Phase cWalk : this.crosswalks) {
            String curDir = cWalk.getDirection();
            allPhaseNums.add(directionToNumMap.get(curDir));
        }
        return allPhaseNums;
    }


     */
//    public Set<Set<Integer>> filterFeasiblePhaseSets(Set<Set<Integer>> possiblePhaseSets) {
//        Set<Set<Integer>> feasiblePhaseSets = new HashSet<>();
//
//        // Filter possiblePhases using conflictMap
//        for (Set<Integer> possiblePhaseSet : possiblePhaseSets) {
//            // if this phase has a conflict, ignore it
//            // (1) check for conflict
//            boolean hasConflict = false;
//
//            Set<Integer> nonConflicting = new HashSet<>();
//            Set<Integer> conflicts;
//
//            for (Integer turn: possiblePhaseSet) {
//                conflicts = new CopyOnWriteArraySet( conflictMap.get(turn) );
//
//                // Do set intersection of conflicts and nonconflicting turns
//                conflicts.retainAll(nonConflicting);
//
//                // if there is no conflict, our current turn is nonconflicting with
//                // previous turns
//                if (conflicts.size() == 0) {
//                    nonConflicting.add(turn);
//                } else { // we have a conflict, must end
//                    hasConflict = true;
//                    break;
//                }
//            }
//
//
//
//            if (hasConflict) {
//                continue;
//                // conflictPhases.add(possiblePhase);
//            } else {
//                feasiblePhaseSets.add(possiblePhaseSet);
//            }
//
//        }
//        return feasiblePhaseSets;
//    }

//    public Set<Set<Phase>> convertIntToPhase(Set<Set<Integer>> feasibleIntsSets) {
//        Set<Set<Phase>> feasiblePhaseSets = new HashSet<>();
//        for (Set<Integer> intSet : feasibleIntsSets) {
//            Set<Phase> curPhases = new HashSet<>();
//            for (Integer inte : intSet) {
//                curPhases.add(numToPhaseMap.get(inte));
//            }
//            feasiblePhaseSets.add(curPhases);
//        }
//
//        return feasiblePhaseSets;
//    }


//    /**
//     * generate all feasible phaseSets (groups of phases)
//     * An example of a single feasible phaseSet:
//     *      {NS, SN, LEFT, RIGHT}
//     */
//    public void generatePhaseSet() {
//        generateVehPedConflictMap();
//        Set<Integer> allPhaseNums = generatePhaseNums();
//        Set<Set<Integer>> possiblePhaseSets = PowerSet.powerSet(allPhaseNums);
//
//        Set<Set<Integer>> feasiblePhaseSets = filterFeasiblePhaseSets(possiblePhaseSets);
//
//        /*
//        System.out.println("Number of feasible phase sets");
//        System.out.println(feasiblePhaseSets.size());
//        System.out.println("Feasible phase sets");
//
//        for (Set<Integer> phaseSet : feasiblePhaseSets) {
//            System.out.println(phaseSet);
//        }
//         */
//
//        this.setOfFeasiblePhaseGrouping = convertIntToPhase(feasiblePhaseSets);
//
//        /*
//        System.out.println("Phase Sets");
//        for (Set<Phase> phaseSet : setOfFeasiblePhaseGrouping) {
//            System.out.println(phaseSet);
//        }
//         */
//
//    }
//

    public void iterateTimeStep() {
        Set<Phase> best_phase_set = controller.selectBestPhaseSet();
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

    public int getId() {
        return this.getVehInt().getId();
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "vehInt=" + vehInt.getLocation() + "";
    }

}
