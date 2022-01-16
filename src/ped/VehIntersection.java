package ped;
import java.util.*;


public class VehIntersection extends VehNode {
    // TODO: implement an IntersectionControl class

    public static boolean PRINT_STATUS = false;

    private Map<VehLink, List<VehLink>> vehTurns;

    //

    private Set<VehLink> vehLinks;
    private Set<VehLink> incomingVehLinks;
    private Set<VehLink> outgoingVehLinks;
    private Set<Turn> vehicleTurns;




    private HashSet<PedNode> pedIntersections;
    // private HashSet<Pedestrian> pedestrians;
    private HashSet<Crosswalk> crosswalks;
    private HashMap<Vehicle, Vehicle> conflictingVehicles;

    // public Map<ConflictPoint, IloNumVar[]> deltas; // NOTE: some Cplex thing
    public Network engine;

    public VehIntersection(int id) {
        super(id);
        vehicleTurns = new HashSet<>();
    }

    public void setVehLinks() {
        vehLinks = new HashSet<VehLink>();
        vehLinks.addAll(incomingVehLinks);
        vehLinks.addAll(outgoingVehLinks);

    }

    public void setVehLinks(Set<VehLink> newlinks) {
        vehLinks = newlinks;
    }

    public Set<VehLink> getVehLinks() {
        return vehLinks;
    }

    public void setIncomingLinks(Set<VehLink> incomingVehLinks) {
        this.incomingVehLinks = incomingVehLinks;
    }

    public Set<VehLink> getIncomingLinks() {
        return incomingVehLinks;
    }

    public void setOutgoingLinks(Set<VehLink> outgoingVehLinks) {
        this.outgoingVehLinks = outgoingVehLinks;
    }

    public Set<VehLink> getOutgoingLinks() {
        return outgoingVehLinks;
    }

    public Set<Turn> getVehicleTurns() {
        return vehicleTurns;
    }

    public void generateVehicleTurns() {
        // Get the product between vehInt.getIncomingVehLinks() and vehInt.getOutgoingVehLinks()
        for (Link in : this.getIncomingLinks()) {
            for (Link out : this.getOutgoingLinks()) {
                vehicleTurns.add(new Turn(in, out));
            }
        }
        // System.out.println(vehicleTurns);
    }


    /*
    public void addTurns() {
        for (VehLink incoming : getVehIncoming()) {
            if (incoming.getLaneType() == "iS1") {
                addTurn(incoming, "iS1", "iE1");
            }

            if (incoming.getLaneType() == "iS2") {
                addTurn(incoming, "iS2", "iW2");
            }

            if (incoming.getLaneType() == "iW1") {
                addTurn(incoming, "iW1", "iS1");
            }

            if (incoming.getLaneType() == "iW2") {
                addTurn(incoming, "iW2", "iN2");
            }

            if (incoming.getLaneType() == "iE1") {
                addTurn(incoming, "iE1", "iN1");
            }

            if (incoming.getLaneType() == "iE2") {
                addTurn(incoming, "iE2", "iS2");
            }

            if (incoming.getLaneType() == "iN1") {
                addTurn(incoming, "iN1", "iW1");
            }

            if (incoming.getLaneType() == "iN2") {
                addTurn(incoming, "iN2", "iE2");
            }
        }
    }


     */
    /*
    //given an incoming lane, scans outgoing lanes for lanes that the incoming lane can turn into.
    public void addTurn(VehLink incoming, String incomingType, String outgoingType) {
        ArrayList<VehLink> outgoingTurns = new ArrayList<VehLink>();
        for (VehLink outgoing: getVehOutgoing()) {
            //a lane can either turn into its own incomingType or the specified outgoingType
            if (outgoing.getLaneType() == incomingType || outgoing.getLaneType() == outgoingType) {
                outgoingTurns.add(outgoing);
            }
        }

        if (outgoingTurns.size() > 0) {
            //adds the turns to the Intersection's VehTurns variable
            getVehTurns().put(incoming, outgoingTurns);
            //updates incoming lanes VehTurns variable
            for (VehLink outgoing : outgoingTurns) {
                incoming.getTurningDirections().add(outgoing.getDirection());
            }
        }
    }

    public Map<VehLink, List<VehLink>> getVehTurns() {
        return this.vehTurns;
    }

     */

}
