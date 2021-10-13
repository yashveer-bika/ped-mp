/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ped;

/**
 *
 * @author micha
 */
public class Main 
{
    public static void main(String[] args)
    {
        System.out.println("Hello world");

        // TODO: create a single intersection representation
        // Have 8 links, 1 in 1 out from each side of the intersection

        Node middle = new Node(1);
        Node top = new Node(2);
        Node bottom = new Node(3);
        Node left = new Node(4);
        Node right = new Node(5);

        Node[] nodes = {middle, top, bottom, left, right};

        // parameters for travel time calculation. 
        // t_ff is the free flow time, C is the capacity, 
        // alpha and beta are the calibration parameters in the BPR function
        Link top_to_middle = new Link(top, middle, 10, 2580, 0.15, 4);
        Link bottom_to_middle = new Link(bottom, middle, 12, 1900, 0.35, 2);
        Link left_to_middle = new Link(left, middle, 12, 1900, 0.35, 2);
        Link middle_to_top = new Link(middle, top, 12, 1900, 0.35, 2);
        Link middle_to_right = new Link(middle, right, 12, 1900, 0.35, 2);
        Link middle_to_bottom = new Link(middle, bottom, 12, 1900, 0.35, 2);

        Link[] links = {top_to_middle, bottom_to_middle, left_to_middle, middle_to_top, middle_to_right, middle_to_bottom};

        Network University_10th_Ave = new Network(nodes, links);
        University_10th_Ave.getLinks();

        System.out.println(top_to_middle.getFlow());

        // make a simple network
        // 1 intersection
        // 4 roads
    }
}