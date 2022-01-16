package ped;

import java.util.ArrayList;

// the possible vehicle directions are
// "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
// 12 possible
// the possible pedestrian directions are
// NS/SN 1/3, NS/SN 2/4, EW/WE 1/2, EW/WE 3/4
// 4 possible
// each phase is a 16-element binary vector
//

public class VecPhase {
    private Boolean[] phase;

    public VecPhase() {
        // phase = new Boolean[16];
        phase = new Boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        System.out.println(phase);
    }
}
