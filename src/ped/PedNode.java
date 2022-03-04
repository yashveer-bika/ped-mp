package ped;

public class PedNode extends Node {
    static int curr_id = 500;

    public PedNode() {
        super();
        this.id = curr_id++;
    }
    public PedNode(int id) {
        super(id);
    }

    public PedNode(Location loc) {
        super();
        this.setLocation(loc);
        this.id = curr_id++;
    }
}
