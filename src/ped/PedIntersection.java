package ped;
import java.util.*;


public class PedIntersection extends PedNode {
    static int curr_id = 900;
    private Network engine;
//    private Set<PedLink> pedLinks;
//    private Set<PedLink> incomingLinks;
//    private Set<PedLink> outgoingLinks;

    private Set<TurningMovement> pedestrianTurns;
    private Set<TurningMovement> entryTurns;
    private Set<TurningMovement> exitTurns;

//    private Map<Link, List<Pedestrian>> pedestrians;

    public PedIntersection(double x, double y) {
        super("pedInt", x, y);
        this.id = curr_id++;
        pedestrianTurns = new HashSet<>();
        entryTurns = new HashSet<>();
        exitTurns = new HashSet<>();
    }

    public PedIntersection(Location lcn) {
        this(lcn.getX(), lcn.getY());
    }

    public void generateEntryTurns() {
        Link in = getEntryLink();
        double count = 0;
        for (Link out : this.getOutgoingLinks()) {
            // prevent u-turns
            // NOTE: adding u-turn logic will require a change of feasible states
            if (in.getSource() == out.getDest()) {
                continue;
            }
            count += 1;
        }
        double tp = 1.0 / count;

        for (Link out : this.getOutgoingLinks()) {
            // prevent u-turns
            // NOTE: adding u-turn logic will require a change of feasible states
            if (in.getSource() == out.getDest()) {
                continue;
            }
            entryTurns.add(new TurningMovement(in, out, engine, tp));
        }
        //       }
    }

    public void generateExitTurns() {
        Link out = getExitLink();
        double count = getIncomingLinks().size();
        double tp = 1.0 / count;

        for (Link in : getIncomingLinks()) {
            exitTurns.add(new TurningMovement(in, out, engine, tp));
        }
    }

    public void generatePedestrianTurns() {
//        System.out.println("Generating ped turns bitch");
//        System.out.println("incoming links: " + getIncomingLinks());
//        System.out.println("outgoing links: " + getOutgoingLinks());
//        System.out.println("all links: " + getAllLinks());


        // Get the product between incoming sidewalks and outgoing crosswalks
        for (Link in : this.getIncomingLinks()) {
            if (in.isSidewalk()) {
                double count = 0;
                for (Link out : this.getOutgoingLinks()) {
                    if (!out.isCrosswalk()) {
                        continue;
                    }
                    // prevent u-turns
                    // NOTE: adding u-turn logic will require a change of feasible states
                    if (in.getSource() == out.getDest()) {
                        continue;
                    }
                    count += 1;
                }

                double tp = 1.0 / count;

                for (Link out : this.getOutgoingLinks()) {
                    if (!out.isCrosswalk()) {
                        continue;
                    }
                    // prevent u-turns
                    // NOTE: adding u-turn logic will require a change of feasible states
                    if (in.getSource() == out.getDest()) {
                        continue;
                    }

                    pedestrianTurns.add(new TurningMovement(in, out, engine, tp));
                }
            }
        }
    }

    public Set<TurningMovement> getPedestrianTurns() {
        return pedestrianTurns;
    }

    public Set<TurningMovement> getEntryTurns() {
        return entryTurns;
    }

    public Set<TurningMovement> getExitTurns() {
        return exitTurns;
    }

    @Override
    public String toString() {
        return "PedIntersection{" +
                ", id=" + id +
                '}';
    }
}
