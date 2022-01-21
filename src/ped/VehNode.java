package ped;

import java.util.ArrayList;
import java.util.HashSet;

public class VehNode extends Node {
    protected ArrayList<VehLink> vehOutgoing;
    protected ArrayList<VehLink> vehIncoming;
    protected HashSet<Vehicle> outgoingVehicles;
    public OldNetwork engine;

    public VehNode(int row, int col, int id, OldNetwork engine)
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

    public ArrayList<VehLink>  getVehOutgoing() {
        return vehOutgoing;
    }

    public ArrayList<VehLink>  getVehIncoming() {
        return vehIncoming;
    }

    public void addVehOutgoing(VehLink l) {
        vehOutgoing.add(l);
    }



}
