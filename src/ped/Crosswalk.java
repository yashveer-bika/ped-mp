package ped;

import ped.Link;
import ped.Node;

public class Crosswalk extends PedLink {
    public Crosswalk(Node start, Node end, double C, boolean entry) {
        super(start, end, C, entry);
    }

    public Crosswalk(Node start, Node end, double C) {
        super(start, end, C);
    }
}
