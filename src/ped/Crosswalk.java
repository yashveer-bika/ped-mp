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
     * @param  way1_ the location of the image, relative to the url argument
     */
    public Crosswalk(Link way1, Link way1_, String direction) {
        assert way1.getStart() == way1_.getdestination();
        assert way1.getDestinationNode() == way1_.getStart();

        this.i = way1;
        this.j = way1_;
        this.direction = direction;
        setId();
    }
}
