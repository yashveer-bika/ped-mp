package ped;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import util.PowerSet;

public class Intersection {
    private VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private HashSet<Link> allLinks;
    private Set<Set<Turn>> phases; // a set of a set of turns that can run at once
    private Set<Turn> vehicleTurns;
    private Set<Turn> pedestrianTurns;
    private Set<VecPhase> vecPhases;


    // private Set<Turn> allTurns; // DEPRECATED

    private HashMap<String, Set<String>> conflictingVehicleDirections;
    private HashMap<Integer, Set<Integer>> conflictMap; // includes veh and ped
    private HashMap<String, Set<String>> vehPedConflictMap;
    private HashMap<Integer, String> numToDirectionMap;
    private HashMap<String, Integer> directionToNumMap;


    public Intersection(VehIntersection vehInt, ArrayList<PedIntersection> pedInts) {
        this.vehInt = vehInt;
        this.pedInts = pedInts;
        vehInt.generateVehicleTurns();
        this.vehicleTurns = vehInt.getVehicleTurns();
        this.pedestrianTurns = new HashSet<Turn>();
        this.conflictMap = new HashMap<Integer, Set<Integer>>();
        

        this.allLinks = new HashSet<Link>();

        // get all the pedestrian links
        for (PedIntersection pedInt : pedInts) {
            // pedLinks
            allLinks.addAll(pedInt.getPedLinks());
        }
        // get all vehicle links
        allLinks.addAll(vehInt.getVehLinks());

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

        directionToNumMap = new HashMap<>();
        for(Map.Entry<Integer, String> entry : numToDirectionMap.entrySet()){
            directionToNumMap.put(entry.getValue(), entry.getKey());
        }


    }

    /*
    public void generateVehicleTurns() {
        // Get the product between vehInt.getIncomingVehLinks() and vehInt.getOutgoingVehLinks()
        for (Link in : vehInt.getIncomingLinks()) {
            for (Link out : vehInt.getOutgoingLinks()) {
                vehicleTurns.add(new Turn(in, out));
            }
        }
        System.out.println(vehicleTurns);
    }
     */

    public void generateVehPedConflictMap() {
        // the possible vehicle directions are
        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
        // 12 possible (0-11)
        // the possible pedestrian directions are
        // NS/SN 1/3, NS/SN 2/4, EW/WE 1/2, EW/WE 3/4
        // 4 possible (12-15)
        // each phase is a 16-element binary vector
        //
        // vehPedConflictMap = new HashMap<String, Set<String>>();


        // the possible vehicle directions are
        // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
        // conflictMap = new HashMap<Integer, Set<Integer>>();

        // (0) set of directions that conflict with NS
        HashSet<Integer> conflictNS = new HashSet<Integer>();
        Integer[] confDirsNS = {5, 11, 4, 10, 9};
        List allNS = Arrays.asList(confDirsNS);
        conflictNS.addAll(allNS);
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
        HashSet<Integer> conflictWS = new HashSet<Integer>();
        Integer[] confDirsWS = {0, 4};
        List allWS = Arrays.asList(confDirsWS);
        conflictWS.addAll(allWS);
        conflictMap.put(10, conflictWS);


        // (12) set of directions that conflict with pedestrian NS/SN 1/3
        HashSet<Integer> conflictPedLeft = new HashSet<Integer>();
        // all the vehicle turns that move through the west side are conflicting
        Integer[] confDirsPedLeft = {1, 5, 7, 9, 10, 11};
        List allPedLeft = Arrays.asList(confDirsPedLeft);
        conflictPedLeft.addAll(allPedLeft);
        conflictMap.put(12, conflictPedLeft);

        // (13) set of directions that conflict with pedestrian NS/SN 2/4
        HashSet<Integer> conflictPedRight = new HashSet<Integer>();
        // all the vehicle turns that move through the east side are conflicting
        Integer[] confDirsPedRight = {2, 3, 4, 5, 8, 11};
        List allPedRight = Arrays.asList(confDirsPedRight);
        conflictPedRight.addAll(allPedRight);
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

    public void generateVehicleConflictMap(){
        // the possible vehicle directions are
        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
        conflictingVehicleDirections = new HashMap<String, Set<String>>();

        // set of directions that conflict with NS
        HashSet<String> conflictNS = new HashSet<String>();
        String[] confDirsNS = {"EW", "WE", "ES", "WS", "WN"};
        List allNS = Arrays.asList(confDirsNS);
        conflictNS.addAll(allNS);
        conflictingVehicleDirections.put("NS", conflictNS);

        // set of directions that conflict with SN
        HashSet<String> conflictSN = new HashSet<String>();
        String[] confDirsSN = {"EW", "WE", "EN", "WN", "ES"};
        List allSN = Arrays.asList(confDirsSN);
        conflictSN.addAll(allSN);
        conflictingVehicleDirections.put("SN", conflictSN);

        // set of directions that conflict with EW
        HashSet<String> conflictEW = new HashSet<String>();
        String[] confDirsEW = {"NS", "SN", "NW", "SW", "NE"};
        List allEW = Arrays.asList(confDirsEW);
        conflictEW.addAll(allEW);
        conflictingVehicleDirections.put("EW", conflictEW);

        // set of directions that conflict with WE
        HashSet<String> conflictWE = new HashSet<String>();
        String[] confDirsWE = {"NS", "SN", "SE", "SW", "NE"};
        List allWE = Arrays.asList(confDirsWE);
        conflictWE.addAll(allWE);
        conflictingVehicleDirections.put("WE", conflictWE);

        // set of directions that conflict with NW
        HashSet<String> conflictNW = new HashSet<String>();
        String[] confDirsNW = {"EW", "SW"};
        List allNW = Arrays.asList(confDirsNW);
        conflictNW.addAll(allNW);
        conflictingVehicleDirections.put("NW", conflictNW);

        // set of directions that conflict with WN
        HashSet<String> conflictWN = new HashSet<String>();
        String[] confDirsWN = {"NS", "NE", "EW", "EN", "SN", "SW"};
        List allWN = Arrays.asList(confDirsWN);
        conflictWN.addAll(allWN);
        conflictingVehicleDirections.put("WN", conflictWN);

        // set of directions that conflict with NE
        HashSet<String> conflictNE = new HashSet<String>();
        String[] confDirsNE = {"ES", "EW", "SN", "SE", "WN", "WE"};
        List allNE = Arrays.asList(confDirsNE);
        conflictNE.addAll(allNE);
        conflictingVehicleDirections.put("NE", conflictNE);

        // set of directions that conflict with EN
        HashSet<String> conflictEN = new HashSet<String>();
        String[] confDirsEN = {"SN", "WN"};
        List allEN = Arrays.asList(confDirsEN);
        conflictEN.addAll(allEN);
        conflictingVehicleDirections.put("EN", conflictEN);

        // set of directions that conflict with ES
        HashSet<String> conflictES = new HashSet<String>();
        String[] confDirsES = {"NS", "NE", "SN", "SW", "WS", "WE"};
        List allES = Arrays.asList(confDirsES);
        conflictES.addAll(allES);
        conflictingVehicleDirections.put("ES", conflictES);

        // set of directions that conflict with SE
        HashSet<String> conflictSE = new HashSet<String>();
        String[] confDirsSE = {"NE", "WE"};
        List allSE = Arrays.asList(confDirsSE);
        conflictSE.addAll(allSE);
        conflictingVehicleDirections.put("SE", conflictSE);

        // set of directions that conflict with SW
        HashSet<String> conflictSW = new HashSet<String>();
        String[] confDirsSW = {"NS", "NW", "ES", "EW", "WN", "WE"};
        List allSW = Arrays.asList(confDirsSW);
        conflictSW.addAll(allSW);
        conflictingVehicleDirections.put("SW", conflictSW);

        // set of directions that conflict with WS
        HashSet<String> conflictWS = new HashSet<String>();
        String[] confDirsWS = {"NS", "ES"};
        List allWS = Arrays.asList(confDirsWS);
        conflictWS.addAll(allWS);
        conflictingVehicleDirections.put("WS", conflictWS);

        System.out.println(conflictingVehicleDirections);
    }

    // HELPER FOR generatePotentialPhases()
    // makes a powerSet and filters out conflicting sets as it goes

    public static <T> Set<Set<T>> filteredPowerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : filteredPowerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            // FILTER IS HERE
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public boolean isConflicting(Turn requiredTurn, Turn potentialTurn) {
        String requiredDir = requiredTurn.getDirection();
        String potentialDir = potentialTurn.getDirection();

        Set<String> conflictDirs = conflictingVehicleDirections.get(requiredDir);
        return conflictDirs.contains(potentialDir);
    }

    public boolean isConflictPhase(Set<Turn> phase) {

        return false; // TODO: update
    }

    public void generatePhases() {
        generateVehPedConflictMap();

        // 'turns' here refers to the integer corresponding to each movement
        //         at the intersection
        Set<Integer> turns = new HashSet<>();
        turns = new HashSet<>();
        for (Turn curTurn : this.vehicleTurns) {
            String curDir = curTurn.getDirection();
            turns.add(directionToNumMap.get(curDir));
        }

        for (Turn curTurn : this.vehicleTurns) {
            String curDir = curTurn.getDirection();
            turns.add(directionToNumMap.get(curDir));
        }


        Set<Set<Integer>> possiblePhases = PowerSet.powerSet(turns);

        System.out.println(possiblePhases.size());

        // TODO: verify that code below here works

        Set<Set<Integer>> feasiblePhases = new HashSet<>();
        Set<Set<Integer>> conflictPhases = new HashSet<>();

        // Filter possiblePhases using conflictMap
        for (Set<Integer> possiblePhase : possiblePhases) {
            // if this phase has a conflict, ignore it
            // (1) check for conflict
            boolean hasConflict = false;

            Set<Integer> nonConflicting = new HashSet<>();
            Set<Integer> conflicts;

            /*
            System.out.println("possiblePhase");
            possiblePhase = new HashSet<Integer>();
            possiblePhase.add(0);
            possiblePhase.add(5);
            possiblePhase.add(7);
            System.out.println(possiblePhase);
            */

            for (Integer turn: possiblePhase) {
                conflicts = new CopyOnWriteArraySet( conflictMap.get(turn) );

                /*
                System.out.println("turn");
                System.out.println(turn);
                System.out.println("potential conflicts");
                System.out.println(conflicts);
                System.out.println("non-conflicting");
                System.out.println(nonConflicting);
                 */

                // Do set intersection of conflicts and nonconflicting turns
                conflicts.retainAll(nonConflicting);
                // System.out.println("conflicts");
                // System.out.println(conflicts);

                // if there is no conflict, our current turn is nonconflicting with
                // previous turns
                if (conflicts.size() == 0) {
                    // System.out.println("A");
                    nonConflicting.add(turn);
                } else { // we have a conflict, must end
                    // System.out.println("B");
                    hasConflict = true;
                    break;
                }
            }



            if (hasConflict) {
                continue;
                // conflictPhases.add(possiblePhase);
            } else {
                feasiblePhases.add(possiblePhase);
            }

            // System.out.println(feasiblePhases.size());
            // break;

        }
        System.out.println("Number of feasible phases");
        System.out.println(feasiblePhases.size());
        System.out.println("Feasible phases");
        for (Set<Integer> phase : feasiblePhases) {
            System.out.println(phase);
        }





    }
}
