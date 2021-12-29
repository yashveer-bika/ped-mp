package ped;

public class PedLink extends Link {
    public PedLink(Node start, Node end, double C, boolean entry) {
        super(start, end, C, entry);
    }

    public PedLink(Node start, Node end, double C) {
        super(start, end, C);
    }
}
