package ped;

public class PedNode extends Node {
    static int curr_id = 500;

    public PedNode(double x, double y) {
        super(x, y);
        this.id = curr_id++;
    }
//    public PedNode(int id) {
//        super(id);
//    }

    public PedNode(Location loc) {
        this(loc.getX(), loc.getY());
    }
}
