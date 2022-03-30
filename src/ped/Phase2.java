package ped;

import java.util.Set;

public class Phase2 {
    Set<TurningMovement> turningMovements;


    public Phase2(Set<TurningMovement> turningMovements) {
        this.turningMovements = turningMovements;
    }

    public String toString() {
        return turningMovements.toString();
    }
}
