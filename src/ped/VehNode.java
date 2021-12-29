package ped;

import java.util.ArrayList;
import java.util.HashSet;

public class VehNode extends Node {
    protected ArrayList<VehLink> vehOutgoing;
    protected ArrayList<VehLink> vehIncoming;
    protected HashSet<Vehicle> outgoingVehicles;
    public Network engine; 

    public VehNode(int row, int col, int id, Network engine)
    {
        super(row, col, id);
        vehOutgoing = new ArrayList<VehLink>();
        vehIncoming = new ArrayList<VehLink>();
        outgoingVehicles = new HashSet<Vehicle>();
        this.engine = engine;

        if (engine.vehNodeGrid[row][col] != null) {
            throw new RuntimeException("trying to overwrite an existing VehNode");
        }
    }

    public VehNode(int id) {
        super(id);
    }
}
