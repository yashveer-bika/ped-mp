package ped;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import Geometry.Geometry;
import util.PowerSet;

public class Intersection {
    private VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private Set<PedNode> pedNodes;

    private HashSet<Link> allLinks;
    private Set<TurningMovement> vehicleTurns;
    private Set<TurningMovement> pedestrianTurningMovements;
    private Set<TurningMovement> allTurningMovements;

    private Set<Crosswalk> crosswalks;
    private Set<Set<Phase>> setOfFeasiblePhaseGrouping; // a set of a set of phase that can run at once
    private Controller controller;
    // vehicle tm, pedestrian tm, 0/1
    private Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
    private HashMap<TurningMovement, Double> vehQueueLengths;
    private HashMap<TurningMovement, Double> pedQueueLengths;
    private HashMap<TurningMovement, Double> pedTurnMovCaps;



    // this is the Q_c defined in the paper
    private double capacityConflictRegion_Qc;


    // private Set<VecPhase> vecPhases;


    // private Set<Turn> allTurns; // DEPRECATED

    private HashMap<String, Set<String>> conflictingVehicleDirections;
    private HashMap<Integer, Set<Integer>> conflictMap; // includes veh and ped
    private HashMap<Integer, String> numToDirectionMap;
    private HashMap<String, Integer> directionToNumMap;
    private HashMap<Phase, Integer> phaseToNumMap;
    private HashMap<Integer, Phase> numToPhaseMap;

//    public Intersection() {
//
//    }

    public Intersection(VehIntersection vehInt, ArrayList<PedIntersection> pedInts,
                        Set<Crosswalk> crosswalks, Set<PedNode> pedNodes) {
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
        this.vehQueueLengths = new HashMap<>();
        this.pedQueueLengths = new HashMap<>();
        this.pedTurnMovCaps = new HashMap<>();

        this.crosswalks = crosswalks;
        this.conflictMap = new HashMap<Integer, Set<Integer>>();
        this.allLinks = new HashSet<Link>();
        this.controller = new pedMPcontroller(this);

        // get all the pedestrian links
        for (PedIntersection pedInt : pedInts) {
            allLinks.addAll( ((PedNode) pedInt).getIncomingLinks() );
            allLinks.addAll( ((PedNode) pedInt).getOutgoingLinks() );
        }
        // get all vehicle links
        allLinks.addAll(vehInt.getIncomingLinks());
        allLinks.addAll(vehInt.getOutgoingLinks());


        // create numToDirectionMap
        numToDirectionMap = new HashMap<>();
        numToDirectionMap.put(0, "NS");
        numToDirectionMap.put(1, "NW");
        numToDirectionMap.put(2, "NE");
        numToDirectionMap.put(3, "EN");
        numToDirectionMap.put(4, "ES");
        numToDirectionMap.put(5, "EW");
        numToDirectionMap.put(6, "SN");
        numToDirectionMap.put(7, "SW");
        numToDirectionMap.put(8, "SE");
        numToDirectionMap.put(9, "WN");
        numToDirectionMap.put(10, "WS");
        numToDirectionMap.put(11, "WE");
        numToDirectionMap.put(12, "LEFT"); // The crosswalk on the left side
        numToDirectionMap.put(13, "RIGHT"); // The crosswalk on the right side
        numToDirectionMap.put(14, "TOP"); // The crosswalk on the top side
        numToDirectionMap.put(15, "BOTTOM"); // The crosswalk on the bottom side

        // create directionToNumMap (inverse mapping)
        directionToNumMap = new HashMap<>();
        for(Map.Entry<Integer, String> entry : numToDirectionMap.entrySet()){
            directionToNumMap.put(entry.getValue(), entry.getKey());
        }


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




    public void setPedestrianTurningMovements() {
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

    // NOTE: not efficient, searchs over the sidewalks and may search over the
    // same vehLinks multiple times
    public void setVehPedConflicts() {
        for (TurningMovement veh_tm : vehicleTurns) {
            // Map<TurningMovement, Map<TurningMovement, Integer>> vehPedConflicts;
            Map<TurningMovement, Integer> tmp = new HashMap<>();
            for (TurningMovement ped_tm : pedestrianTurningMovements) {
                if (veh_tm.intersects(ped_tm)) {
                    tmp.put(ped_tm, 1);
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
        setPedestrianTurningMovements();
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

    public Set<Set<Phase>> getSetOfFeasiblePhaseGrouping() {
        return setOfFeasiblePhaseGrouping;
    }

    public void generateVehPedConflictMap() {
        // the possible vehicle directions are
        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
        // 12 possible (0-11)
        // the possible pedestrian directions are
        // NS/SN 1/3, NS/SN 2/4, EW/WE 1/2, EW/WE 3/4
        // 4 possible (12-15)
        // each phaseSet is a 16-element binary vector
        //
        // vehPedConflictMap = new HashMap<String, Set<String>>();


        // the possible vehicle directions are
        // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
        // conflictMap = new HashMap<Integer, Set<Integer>>();

        // (0) set of directions that conflict with NS
        Integer[] confDirsNS = {5, 11, 4, 10, 9};
        List allNS = Arrays.asList(confDirsNS);
        HashSet<Integer> conflictNS = new HashSet<Integer>(allNS);
        conflictMap.put(0, conflictNS);

        // (1) set of directions that conflict with SN
        HashSet<Integer> conflictSN = new HashSet<Integer>();
        Integer[] confDirsSN = {5, 11, 3, 9, 4};
        List allSN = Arrays.asList(confDirsSN);
        conflictSN.addAll(allSN);
        conflictMap.put(6, conflictSN);

        // (2) set of directions that conflict with EW
        HashSet<Integer> conflictEW = new HashSet<Integer>();
        Integer[] confDirsEW = {0, 6, 1, 7, 2};
        List allEW = Arrays.asList(confDirsEW);
        conflictEW.addAll(allEW);
        conflictMap.put(5, conflictEW);

        // (3) set of directions that conflict with WE
        HashSet<Integer> conflictWE = new HashSet<Integer>();
        Integer[] confDirsWE = {0, 6, 8, 7, 2};
        List allWE = Arrays.asList(confDirsWE);
        conflictWE.addAll(allWE);
        conflictMap.put(11, conflictWE);

        // (4) set of directions that conflict with NW
        HashSet<Integer> conflictNW = new HashSet<Integer>();
        Integer[] confDirsNW = {5, 7};
        List allNW = Arrays.asList(confDirsNW);
        conflictNW.addAll(allNW);
        conflictMap.put(1, conflictNW);

        // (5) set of directions that conflict with WN
        HashSet<Integer> conflictWN = new HashSet<Integer>();
        Integer[] confDirsWN = {0, 2, 5, 3, 6, 7};
        List allWN = Arrays.asList(confDirsWN);
        conflictWN.addAll(allWN);
        conflictMap.put(9, conflictWN);

        // (6) set of directions that conflict with NE
        HashSet<Integer> conflictNE = new HashSet<Integer>();
        Integer[] confDirsNE = {4, 5, 6, 8, 9, 11};
        List allNE = Arrays.asList(confDirsNE);
        conflictNE.addAll(allNE);
        conflictMap.put(2, conflictNE);

        // (7) set of directions that conflict with EN
        HashSet<Integer> conflictEN = new HashSet<Integer>();
        Integer[] confDirsEN = {6, 9};
        List allEN = Arrays.asList(confDirsEN);
        conflictEN.addAll(allEN);
        conflictMap.put(3, conflictEN);

        // (8) set of directions that conflict with ES
        HashSet<Integer> conflictES = new HashSet<Integer>();
        Integer[] confDirsES = {0, 2, 6, 7, 10, 11};
        List allES = Arrays.asList(confDirsES);
        conflictES.addAll(allES);
        conflictMap.put(4, conflictES);

        // (9) set of directions that conflict with SE
        HashSet<Integer> conflictSE = new HashSet<Integer>();
        Integer[] confDirsSE = {2, 11};
        List allSE = Arrays.asList(confDirsSE);
        conflictSE.addAll(allSE);
        conflictMap.put(8, conflictSE);

        // (10) set of directions that conflict with SW
        HashSet<Integer> conflictSW = new HashSet<Integer>();
        Integer[] confDirsSW = {0, 1, 4, 5, 9, 11};
        List allSW = Arrays.asList(confDirsSW);
        conflictSW.addAll(allSW);
        conflictMap.put(7, conflictSW);

        // (11) set of directions that conflict with WS
        Integer[] confDirsWS = {0, 4};
        List allWS = Arrays.asList(confDirsWS);
        HashSet<Integer> conflictWS = new HashSet<Integer>(allWS);
        conflictMap.put(10, conflictWS);


        // (12) set of directions that conflict with pedestrian NS/SN 1/3
        // all the vehicle turns that move through the west side are conflicting
        Integer[] confDirsPedLeft = {1, 5, 7, 9, 10, 11};
        List allPedLeft = Arrays.asList(confDirsPedLeft);
        HashSet<Integer> conflictPedLeft = new HashSet<Integer>(allPedLeft);
        conflictMap.put(12, conflictPedLeft);

        // (13) set of directions that conflict with pedestrian NS/SN 2/4
        // all the vehicle turns that move through the east side are conflicting
        Integer[] confDirsPedRight = {2, 3, 4, 5, 8, 11};
        List allPedRight = Arrays.asList(confDirsPedRight);
        HashSet<Integer> conflictPedRight = new HashSet<>(allPedRight);
        conflictMap.put(13, conflictPedRight);

        // (14) set of directions that conflict with pedestrian EW/WE 1/2
        HashSet<Integer> conflictPedTop = new HashSet<Integer>();
        // all the vehicle turns that move through the north side are conflicting
        Integer[] confDirsPedTop = {0, 1, 2, 3, 6, 9};
        List allPedTop = Arrays.asList(confDirsPedTop);
        conflictPedTop.addAll(allPedTop);
        conflictMap.put(14, conflictPedTop);

        // (15) set of directions that conflict with pedestrian EW/WE 3/4
        HashSet<Integer> conflictPedBottom = new HashSet<Integer>();
        // all the vehicle turns that move through the south side are conflicting
        Integer[] confDirsPedBottom = {0, 4, 6, 7, 8, 10};
        List allPedBottom = Arrays.asList(confDirsPedBottom);
        conflictPedBottom.addAll(allPedBottom);
        conflictMap.put(15, conflictPedBottom);
    }

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
    public Set<Set<Integer>> filterFeasiblePhaseSets(Set<Set<Integer>> possiblePhaseSets) {
        Set<Set<Integer>> feasiblePhaseSets = new HashSet<>();

        // Filter possiblePhases using conflictMap
        for (Set<Integer> possiblePhaseSet : possiblePhaseSets) {
            // if this phase has a conflict, ignore it
            // (1) check for conflict
            boolean hasConflict = false;

            Set<Integer> nonConflicting = new HashSet<>();
            Set<Integer> conflicts;

            for (Integer turn: possiblePhaseSet) {
                conflicts = new CopyOnWriteArraySet( conflictMap.get(turn) );

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
                // conflictPhases.add(possiblePhase);
            } else {
                feasiblePhaseSets.add(possiblePhaseSet);
            }

        }
        return feasiblePhaseSets;
    }

    public Set<Set<Phase>> convertIntToPhase(Set<Set<Integer>> feasibleIntsSets) {
        Set<Set<Phase>> feasiblePhaseSets = new HashSet<>();
        for (Set<Integer> intSet : feasibleIntsSets) {
            Set<Phase> curPhases = new HashSet<>();
            for (Integer inte : intSet) {
                curPhases.add(numToPhaseMap.get(inte));
            }
            feasiblePhaseSets.add(curPhases);
        }

        return feasiblePhaseSets;
    }


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
        for (PedIntersection pedInt : pedInts) {
            pedInt.updateTime(newTime);
        }
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "vehInt=" + vehInt.getLocation() + "";
    }

}
