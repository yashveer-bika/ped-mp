package ped;

public class PedLink extends Link {
    protected boolean isSidewalk = false;

    public PedLink(PedNode start, PedNode end, double C, boolean entry) {
        super(start, end, C, entry);
    }

    public PedLink(PedNode start, PedNode end, boolean isSidewalk, double C) {
        super(start, end, C);
        this.isSidewalk = isSidewalk;
    }

    public PedLink(PedNode start, PedNode end, double C) {
        super(start, end, C);
    }

    public PedLink(PedNode start, PedNode end, double C, String direction) {
        super(start, end, C, direction);
    }

    public boolean getIsSidewalk() {
        return isSidewalk;
    }
}
