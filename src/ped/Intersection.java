package ped;

import java.util.*;


public class Intersection {
    private VehIntersection vehInt;
    private ArrayList<PedIntersection> pedInts;
    private HashSet<Link> allLinks;
    private Set<Set<Turn>> phases; // a set of a set of turns that can run at once
    private Set<Turn> allTurns;

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

    // TODO: convert set of all turns into phases
    public void generatePhases() {
        generateTurns();

        // each element in pedInts is a PedIntersection

        // TODO: generate the conflicting phase directions

        // {Turn1, 2, 3, 4, 5}
        // Turn1 (NE),

        // the possible directions are
        // NS, NW, NE, EN, ES, EW, SN, SW, SE, WN, WS, WE
        // "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
        HashMap<String, Set<String>> conflictingDirections = new HashMap<String, Set<String>>();

        // set of directions that conflict with NS
        HashSet<String> conflictNS = new HashSet<String>();
        String[] confDirsNS = {"ES", "EW", "WN", "WS", "WE"};
        List allNS = Arrays.asList(confDirsNS);
        conflictNS.addAll(allNS);
        conflictingDirections.put("NS", conflictNS);

        // set of directions that conflict with NW
        HashSet<String> conflictNW = new HashSet<String>();
        String[] confDirsNW = {"EW", "SW"};
        List allNW = Arrays.asList(confDirsNW);
        conflictNW.addAll(allNW);
        conflictingDirections.put("NW", conflictNW);

        // set of directions that conflict with NE
        HashSet<String> conflictNE = new HashSet<String>();
        String[] confDirsNE = {"NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"};
        List allNE = Arrays.asList(confDirsNE);
        conflictNE.addAll(allNE);
        conflictingDirections.put("NE", conflictNE);




        // conflictingDirections.add(")

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
