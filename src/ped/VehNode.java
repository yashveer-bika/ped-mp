package ped;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VehNode extends Node {
//    protected Set<VehLink> vehOutgoing;
//    protected Set<VehLink> vehIncoming;
//    protected HashSet<Vehicle> outgoingVehicles;
//    public OldNetwork engine;


    public VehNode(int id, String type, double x, double y) {
        super(id, type, x, y);
//        vehOutgoing = new HashSet<>();
//        vehIncoming = new HashSet<>();
    }
    public VehNode(int id, double x, double y) {
        super(id, x, y);
//        vehOutgoing = new HashSet<>();
//        vehIncoming = new HashSet<>();
    }

    public VehNode(int id, int row, int col) {
        super(id, row, col);
    }

//    public Set<VehLink>  getVehOutgoing() {
//        return vehOutgoing;
//    }
//
//    public Set<VehLink>  getVehIncoming() {
//        return vehIncoming;
//    }
//
//    public void addVehOutgoing(VehLink l) {
//        vehOutgoing.add(l);
//    }
//
//    public void addVehIncoming(VehLink l) {
//        vehIncoming.add(l);
//    }



}
