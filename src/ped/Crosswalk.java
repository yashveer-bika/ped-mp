package ped;


public class Crosswalk {

    /**
     * TODO: update this JavaDoc to match def'n
     *
     * This creates a Crosswalk with two
     *
     *
     */

    Link i;
    Link j;

    public Crosswalk() {
        this.i = null;
        this.j = null;
    }

    public Crosswalk(Link way1, Link way2) {
        assert way1.getSource() == way2.getDest();
        assert way1.getDest() == way2.getSource();

        this.i = way1;
        this.j = way2;
        // this.setId();
    }

    public Crosswalk(Link way1, Link way2, String direction) {
        assert way1.getSource() == way2.getDest();
        assert way1.getDest() == way2.getSource();

        this.i = way1;
        this.j = way2;
    }

    public void setLinks(Link way1, Link way2) {
        this.i = way1;
        this.j = way2;
    }

    @Override
    public String toString() {
        return "Crosswalks{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}
