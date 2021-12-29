package ped;

import java.util.*;

public class MaxPressure extends IntersectionControl {

    public MaxPressure(Intersection node)
    {
        super(node);
    }

    public boolean hasConflictRegions()
    {
        return false;
    }

    public void reset() { }

    // TODO: fix this logic

    public boolean canMove(Link i, Link j)
    {
        // return i.getSource() != j.getDest();
        return true;
    }

    // NOTE: Helper
    public Phase choosePhase() {
        return new Phase();
    }

    public int step()
    {
        Phase phase = choosePhase();

        /*
        List<Vehicle> moved = new ArrayList<>();

        Node node = getNode();

        int waiting = 0;

        // move vehicles to centroid connectors
        for(Link i : node.getIncoming())
        {
            Iterable<Vehicle> sending;

            if(i instanceof CentroidConnector)
            {
                sending = i.getVehicles();
            }
            else
            {
                // t.i is MPLink
                sending = ((MPLink)i).getLastCell().getOccupants();
            }

            for(Vehicle v : sending)
            {
                if(v.getNextLink() == null || v.getNextLink().isCentroidConnector())
                {
                    moved.add(v);
                }
                waiting++;
            }
        }


        int exiting = 0;



        if(phase != null)
        {

            // move vehicles according to selected phase
            for(Turn t : phase.getTurns())
            {
                double usable = (Simulator.dt - phase.getRedTime()) / Simulator.dt;

                int max_y = (int)Math.round(usable * Math.min(t.i.getCapacityPerTimestep(), t.j.getCapacityPerTimestep()));

                Iterable<Vehicle> sending;




                if(t.i instanceof CentroidConnector)
                {
                    sending = t.i.getVehicles();
                }
                else
                {
                    // t.i is MPLink
                    sending = ((MPLink)t.i).getLastCell().getOccupants();
                }

                for(Vehicle v : sending)
                {


                    if(max_y > 0 && v.getNextLink() == t.j)
                    {
                        moved.add(v);
                        max_y--;
                    }


                }


            }
        }


        for(Vehicle v : moved)
        {
            Link j = v.getNextLink();
            Link i = v.getCurrLink();

            i.removeVehicle(v);

            if(j == null)
            {
                exiting++;
                v.exited();
            }
            else
            {
                j.addVehicle(v);
            }
        }

        return exiting;

         */
        return 0;
    }

    public void initialize()
    {
        // need to create phases here
        /*
        turns = new ArrayList<>();
        Intersection node = (Intersection) getNode();

        for(Link i : node.getIncoming())
        {
            for(Link j : node.getOutgoing())
            {
                if(canMove(i, j, DriverType.HV))
                {
                    turns.add(new MPTurn(i, j));
                }
            }
        }

        Set<Turn> matched = new HashSet<Turn>();

        Map<Link, Map<Link, TurningMovement>> conflicts = ConflictFactory.generate(node);


        phases =  new ArrayList<>();

        // look for compatible combinations of 2 turns
        // then add turns as feasible

        for(int i = 0; i < turns.size()-1; i++)
        {
            for(int j = i+1; j < turns.size(); j++)
            {
                if(!hasConflicts(turns.get(i), turns.get(j), conflicts))
                {
                    List<Turn> allowed = new ArrayList<>();
                    allowed.add(turns.get(i));
                    allowed.add(turns.get(j));

                    outer:for(Turn t : turns)
                    {
                        if(allowed.contains(t))
                        {
                            continue;
                        }

                        for(Turn t2 : allowed)
                        {
                            if(hasConflicts(t, t2, conflicts))
                            {
                                continue outer;
                            }
                        }

                        allowed.add(t);
                    }

                    for(Turn t : allowed)
                    {
                        matched.add(t);
                    }
                    Phase p = new Phase(0, allowed, Simulator.dt-LOST_TIME, 0, LOST_TIME);
                    phases.add(p);
                }
            }
        }


        for(Turn t : turns)
        {
            if(!matched.contains(t))
            {
                Phase p = new Phase(0, new Turn[]{t}, Simulator.dt-LOST_TIME, 0, LOST_TIME);
                phases.add(p);
            }
        }



        // remove duplicate phases
        List<Phase> duplicates = new ArrayList<>();
        for(int i = 0; i < phases.size()-1; i++)
        {
            for(int j = i+1; j < phases.size(); j++)
            {
                if(phases.get(i).equals(phases.get(j)))
                {
                    duplicates.add(phases.get(j));
                }
            }
        }

        for(Phase p : duplicates)
        {
            phases.remove(p);
        }


        */
    }

}
