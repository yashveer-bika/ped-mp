package ped;


public class Crosswalk extends Phase {

    /**
     * TODO: update this JavaDoc to match def'n
     *
     * This creates a Crosswalk with two
     *
     *
     */

    public Crosswalk() {
        this.i = null;
        this.j = null;
        this.direction = null;
    }

    public Crosswalk(Link way1, Link way2) {
        assert way1.getStart() == way2.getDestination();
        assert way1.getDestination() == way2.getStart();

        this.i = way1;
        this.j = way2;
        // this.setId();
    }

    public Crosswalk(Link way1, Link way2, String direction) {
        assert way1.getStart() == way2.getDestination();
        assert way1.getDestination() == way2.getStart();

        this.i = way1;
        this.j = way2;
        this.direction = direction;
        this.setId();
    }

    public void setLinks(Link way1, Link way2) {
        this.i = way1;
        this.j = way2;
    }

    public void setDirection(String direction) {
        this.direction = direction;
        this.setId();
    }
    @Override
    public String toString() {
        return "Crosswalks{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}
