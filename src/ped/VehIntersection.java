package ped;
import java.util.*;


public class VehIntersection extends VehNode {
    // TODO: implement an IntersectionControl class
    private Network engine;
    private Set<TurningMovement> vehicleTurns;
    private Set<TurningMovement> entryTurns;
    private Set<TurningMovement> exitTurns;

    public VehIntersection(int id, String nodeType, double x, double y, Network engine) {
        super(id, nodeType, x, y);
        vehicleTurns = new HashSet<>();
        entryTurns = new HashSet<>();
        exitTurns = new HashSet<>();
        this.engine = engine;
    }

    public Set<TurningMovement> getVehicleTurns() {
        return vehicleTurns;
    }

    public Set<TurningMovement> getEntryTurns() {
        return entryTurns;
    }

    public void generateEntryTurns() {
//        System.out.println("Intersection " + getId());
        Link in = getEntryLink();
//        System.out.println("in: " + in);
        // for (Link in : this.getIncomingLinks()) {
            for (Link out : this.getOutgoingLinks()) {
                // prevent u-turns
                // NOTE: adding u-turn logic will require a change of feasible states
                if (in.getSource() == out.getDest()) {
                    continue;
                }
//                if (in instanceof EntryLink || out instanceof ExitLink) {
//                    continue;
//                }
//                System.out.println("in: " + in);
//                System.out.println("out: " + out);
                entryTurns.add(new TurningMovement(in, out, engine, -1));
            }
 //       }
    }

    public void generateExitTurns() {
//        System.out.println("Intersection " + getId());
//        System.out.println("in: " + in);
        // for (Link in : this.getIncomingLinks()) {
        Link out = getExitLink();
        for (Link in : getIncomingLinks()) {
//                if (in instanceof EntryLink || out instanceof ExitLink) {
//                    continue;
//                }
//                System.out.println("in: " + in);
//                System.out.println("out: " + out);
            exitTurns.add(new TurningMovement(in, out, engine, -1));
        }
        //       }
    }


    public void generateVehicleTurns() {
        // Get the product between vehInt.getIncomingVehLinks() and vehInt.getOutgoingVehLinks()
        for (Link in : this.getIncomingLinks()) {
            for (Link out : this.getOutgoingLinks()) {
                // prevent u-turns
                // NOTE: adding u-turn logic will require a change of feasible states
                if (in.getSource() == out.getDest()) {
                    continue;
                }
//                if (in instanceof EntryLink || out instanceof ExitLink) {
//                    continue;
//                }
//                System.out.println("in: " + in);
//                System.out.println("out: " + out);
                vehicleTurns.add(new TurningMovement(in, out, engine, -1));
            }
        }
    }

    public Set<TurningMovement> getExitTurns() {
        return exitTurns;
    }

    @Override
    public String toString() {
        return "VehIntersection{" +
                "vehicleTurns=" + vehicleTurns +
                " " +
                '}';
    }
}