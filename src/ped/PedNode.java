package ped;

public class PedNode extends Node {
    static int curr_id = 500;


    public PedNode(int id, String type, double x, double y) {
        super(id, x, y);
        this.type = type;
    }

    public PedNode(String type, double x, double y) {
        super(x, y);
        this.id = curr_id++;
        this.type = type;
    }

//    public PedNode(int id) {
//        super(id);
//    }

    public PedNode(String type, Location loc) {
        this(type, loc.getX(), loc.getY());
    }

    public PedNode(int id, String type, Location loc) {
        this(id, type, loc.getX(), loc.getY());
    }
}
