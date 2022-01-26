package ped;

import ped.Link;
import ped.Node;

public class Crosswalk extends Phase {

    /**
     * TODO: update this JavaDoc to match def'n
     *
     * This creates a Crosswalk with two
     *
     *
     * @param  way1  an absolute URL giving the base location of the image
     * @param  way2 the location of the image, relative to the url argument
     */
    public Crosswalk(Link way1, Link way2, String direction) {
        assert way1.getStart() == way2.getDestination();
        assert way1.getDestination() == way2.getStart();

        this.i = way1;
        this.j = way2;
        this.direction = direction;
        this.setId();
    }
}
