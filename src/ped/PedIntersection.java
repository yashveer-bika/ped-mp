package ped;
import java.util.*;


public class PedIntersection extends PedNode {
    static int curr_id = 900;
//    private Set<PedLink> pedLinks;
//    private Set<PedLink> incomingLinks;
//    private Set<PedLink> outgoingLinks;

    private Set<TurningMovement> pedestrianTurns;

    private Map<Link, List<Pedestrian>> pedestrians;

    public PedIntersection(double x, double y) {
        super(x, y);
        this.id = curr_id++;
        pedestrianTurns = new HashSet<>();
    }

    public PedIntersection(Location lcn) {
        this(lcn.getX(), lcn.getY());
    }

    public void generatePedestrianTurns() {
        // Get the product between vehInt.getIncomingVehLinks() and vehInt.getOutgoingVehLinks()
        for (Link in : this.getIncomingLinks()) {
            for (Link out : this.getOutgoingLinks()) {
                // prevent u-turns
                // NOTE: adding u-turn logic will require a change of feasible states
                if (in.getSource() == out.getDest()) {
                    continue;
                }
                pedestrianTurns.add(new TurningMovement(in, out));
            }
        }
    }

    public Set<TurningMovement> getPedestrianTurns() {
        return pedestrianTurns;
    }

    public void updateTime(double newTime) {
        for (TurningMovement t : pedestrianTurns) {
            t.updateTime(newTime);
        }
    }

    //    public PedIntersection(int id) {
//        super(id);
//    }

//    public void setPedLinks() {
//        pedLinks = new HashSet<PedLink>();
//        pedLinks.addAll(incomingLinks);
//        pedLinks.addAll(outgoingLinks);
//    }

//    public void setPedLinks(Set<PedLink> newLinks) {
//        pedLinks = newLinks;
//    }

//    public Set<PedLink> getPedLinks() {
//        return pedLinks;
//    }

//    public void setIncomingLinks(Set<PedLink> incomingLinks) {
//        this.incomingLinks = incomingLinks;
//    }
//
//    public Set<PedLink> getIncomingLinks() {
//        return incomingLinks;
//    }

//    public void setOutgoingLinks(Set<PedLink> outgoingLinks) {
//        this.outgoingLinks = outgoingLinks;
//    }

//    public Set<PedLink> getOutgoingLinks() {
//        return outgoingLinks;
//    }



    /*
    public void setControl(IntersectionControl c)
    {
        control = c;

        control.setNode(this);
    }

    public void addTurns() {
        for (PedLink incoming : getPedIncoming()) {
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

    //given an incoming lane, scans outgoing lanes for lanes that the incoming lane can turn into.
    public void addTurn(PedLink incoming, String incomingType, String outgoingType) {
        ArrayList<PedLink> outgoingTurns = new ArrayList<PedLink>();
        for (PedLink outgoing: getPedOutgoing()) {
            //a lane can either turn into its own incomingType or the specified outgoingType
            if (outgoing.getLaneType() == incomingType || outgoing.getLaneType() == outgoingType) {
                outgoingTurns.add(outgoing);
            }
        }

        if (outgoingTurns.size() > 0) {
            //adds the turns to the Intersection's PedTurns variable
            getPedTurns().put(incoming, outgoingTurns);
            //updates incoming lanes PedTurns variable
            for (PedLink outgoing : outgoingTurns) {
                incoming.getTurningDirections().add(outgoing.getDirection());
            }
        }
    }

    public Map<PedLink, List<PedLink>> getPedTurns() {
        return this.PedTurns;
    }

     */

    @Override
    public String toString() {
        return "PedIntersection{" +
                ", id=" + id +
                '}';
    }
}
