package ped;

import java.util.HashSet;
import java.util.Set;

public class Phase implements Comparable<Phase> {
    private Set<TurningMovement> turningMovements;

    public Phase() {
        this.turningMovements = new HashSet<>();
    }

    public Phase(Set<TurningMovement> turningMovements) {
        this.turningMovements = turningMovements;
        // TODO: make the Set<Set<TurningMovement>> to define each conflict region
        // our code allows diverging turns, but only so much capacity can go through each region

    }

    public int size() {
        return this.turningMovements.size();
    }

    public Set<TurningMovement> getTurningMovements() {
        return turningMovements;
    }

    public String toString() {
        return turningMovements.toString();
    }

    private static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
        return setB.containsAll(setA);
    }

    public boolean isSubset(Phase rhs) {
        return turningMovements.containsAll(rhs.getTurningMovements());
    }

    public boolean equals(Phase rhs) {
        return turningMovements.equals(rhs.getTurningMovements());
    }

    @Override
    public int compareTo(Phase rhs) {
        if (this.size() == rhs.size()) {
            return 0;
        } else if (this.size() < rhs.size()) {
            return -1;
        } else {
            return 1;
        }
    }
}
