package ped;

import java.util.*;


public class Intersection {
    private VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private HashSet<Link> allLinks;
    private Set<Set<Turn>> phases; // a set of a set of turns that can run at once
    private Set<Turn> allTurns;
    private HashMap<String, Set<String>> conflictingDirections;

    public Intersection(VehIntersection vehInt, ArrayList<PedIntersection> pedInts) {
        this.vehInt = vehInt;
        this.pedInts = pedInts;
        this.allTurns = new HashSet<Turn>();

        this.allLinks = new HashSet<Link>();

        // get all the pedestrian links
        for (PedIntersection pedInt : pedInts) {
            // pedLinks
            allLinks.addAll(pedInt.getPedLinks());
        }

        // get all vehicle links
        allLinks.addAll(vehInt.getVehLinks());



    }

    public void generateTurns() {
        // Get the product between vehInt.getIncomingVehLinks() and vehInt.getOutgoingVehLinks()
        for (Link in : vehInt.getIncomingLinks()) {
            for (Link out : vehInt.getOutgoingLinks()) {
                allTurns.add(new Turn(in, out));
            }
        }
        System.out.println(allTurns);

        // Get the product between pedInt.getIncomingVehLinks() and pedInt.getOutgoingVehLinks()
        for (PedIntersection pedInt : pedInts) {
            for (Link in : pedInt.getIncomingLinks()) {
                for (Link out : pedInt.getOutgoingLinks()) {
                    if (in.getStart() != out.getDestinationNode()) { // NOTE: THIS MEANS NO U-TURNS
                        allTurns.add(new Turn(in, out));
                    }
                }
            }
        }

        System.out.println(allTurns);
    }

    public void generateConflictMap(){
        // the possible directions are
        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
        conflictingDirections = new HashMap<String, Set<String>>();

        // set of directions that conflict with NS
        HashSet<String> conflictNS = new HashSet<String>();
        String[] confDirsNS = {"EW", "WE", "ES", "WS", "WN"};
        List allNS = Arrays.asList(confDirsNS);
        conflictNS.addAll(allNS);
        conflictingDirections.put("NS", conflictNS);

        // set of directions that conflict with SN
        HashSet<String> conflictSN = new HashSet<String>();
        String[] confDirsSN = {"EW", "WE", "EN", "WN", "ES"};
        List allSN = Arrays.asList(confDirsSN);
        conflictSN.addAll(allSN);
        conflictingDirections.put("SN", conflictSN);

        // set of directions that conflict with EW
        HashSet<String> conflictEW = new HashSet<String>();
        String[] confDirsEW = {"NS", "SN", "NW", "SW", "NE"};
        List allEW = Arrays.asList(confDirsEW);
        conflictEW.addAll(allEW);
        conflictingDirections.put("EW", conflictEW);

        // set of directions that conflict with WE
        HashSet<String> conflictWE = new HashSet<String>();
        String[] confDirsWE = {"NS", "SN", "SE", "SW", "NE"};
        List allWE = Arrays.asList(confDirsWE);
        conflictWE.addAll(allWE);
        conflictingDirections.put("WE", conflictWE);

        // set of directions that conflict with NW
        HashSet<String> conflictNW = new HashSet<String>();
        String[] confDirsNW = {"EW", "SW"};
        List allNW = Arrays.asList(confDirsNW);
        conflictNW.addAll(allNW);
        conflictingDirections.put("NW", conflictNW);

        // set of directions that conflict with WN
        HashSet<String> conflictWN = new HashSet<String>();
        String[] confDirsWN = {"NS", "NE", "EW", "EN", "SN", "SW"};
        List allWN = Arrays.asList(confDirsWN);
        conflictWN.addAll(allWN);
        conflictingDirections.put("WN", conflictWN);

        // set of directions that conflict with NE
        HashSet<String> conflictNE = new HashSet<String>();
        String[] confDirsNE = {"ES", "EW", "SN", "SE", "WN", "WE"};
        List allNE = Arrays.asList(confDirsNE);
        conflictNE.addAll(allNE);
        conflictingDirections.put("NE", conflictNE);

        // set of directions that conflict with EN
        HashSet<String> conflictEN = new HashSet<String>();
        String[] confDirsEN = {"SN", "WN"};
        List allEN = Arrays.asList(confDirsEN);
        conflictEN.addAll(allEN);
        conflictingDirections.put("EN", conflictEN);

        // set of directions that conflict with ES
        HashSet<String> conflictES = new HashSet<String>();
        String[] confDirsES = {"NS", "NE", "SN", "SW", "WS", "WE"};
        List allES = Arrays.asList(confDirsES);
        conflictES.addAll(allES);
        conflictingDirections.put("ES", conflictES);

        // set of directions that conflict with SE
        HashSet<String> conflictSE = new HashSet<String>();
        String[] confDirsSE = {"NE", "WE"};
        List allSE = Arrays.asList(confDirsSE);
        conflictSE.addAll(allSE);
        conflictingDirections.put("SE", conflictSE);

        // set of directions that conflict with SW
        HashSet<String> conflictSW = new HashSet<String>();
        String[] confDirsSW = {"NS", "NW", "ES", "EW", "WN", "WE"};
        List allSW = Arrays.asList(confDirsSW);
        conflictSW.addAll(allSW);
        conflictingDirections.put("SW", conflictSW);

        // set of directions that conflict with WS
        HashSet<String> conflictWS = new HashSet<String>();
        String[] confDirsWS = {"NS", "ES"};
        List allWS = Arrays.asList(confDirsWS);
        conflictWS.addAll(allWS);
        conflictingDirections.put("WS", conflictWS);

        System.out.println(conflictingDirections);
    }

    // TODO: convert set of all turns into phases
    public void generatePhases() {
        generateTurns();
        generateConflictMap();

        // each element in pedInts is a PedIntersection


        // NAIVE SOLUTION:
        // Iterate over every link (all N links) and compare with every other link
        // TODO: incomplete

        HashMap<Link, Set<Link>> conflictingLinks = new HashMap<Link, Set<Link>>();
        for (Link link1 : allLinks) {
            for (Link link2 : allLinks) {
                // see if the links are conflicting

            }
        }



    }
}
