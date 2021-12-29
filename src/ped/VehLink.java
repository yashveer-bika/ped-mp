package ped;

public class VehLink extends Link {
    public VehLink(Node start, Node end, double C, boolean entry) {
        super(start, end, C, entry);
    }

    public VehLink(Node start, Node end, double C) {
        super(start, end, C);
    }
}
