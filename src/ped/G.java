package ped;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class G extends JPanel {
    // int[] coordinates = {0,0, 1,0, 2, -1, 2, 1, 3, 0, 4, 0};
    int[] coordinates = {3,3, 4,3, 5, 2, 5, 4, 6, 3, 7, 3};
    int mar = 50;
    Set<Node> nodeSet = new HashSet<>();
    Set<Link> linkSet = new HashSet<>();

    public G(Set<Node> nodeSet) {
        super();
        this.nodeSet = nodeSet;
        // this.linkSet = linkSet;
    }

    public G(Set<Node> nodeSet, Set<Link> linkSet) {
        super();
        this.nodeSet = nodeSet;
        this.linkSet = linkSet;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g1 = (Graphics2D) g;
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        // g1.draw(new Line2D.Double(mar, mar, mar, height - mar));
        // g1.draw(new Line2D.Double(mar, height - mar, width - mar, height - mar));
        // double x = (double) (width - 2 * mar) / (coordinates.length - 1);
        // double scale = (double) (height - 2 * mar) / getMax();
        g1.setPaint(Color.BLUE);

//        for (int i = 0; i < coordinates.length; i = i + 2) {
//            double x1 = coordinates[i]*100;
//            double y1 = coordinates[i+1]*100;
//            g1.fill(new Ellipse2D.Double(x1, y1, 10, 10));
//        }

        ArrayList<Double> min_maxs = getMinMax();

        // TODO: map all points to fit on screen

        for (Node n : this.nodeSet) {
            Location loc = n.getLocation();
            double x1 = loc.getX()*100;
            double y1 = loc.getY()*100;

            // scale and shift the point

            g1.fill(new Ellipse2D.Double(x1 + 100, y1 + 200, 10, 10));
        }

        // TODO: draw all links
        for (Link l : this.linkSet) {
            Node n1 = l.getStart();
            Node n2 = l.getDestination();


            // non entry links
            if (!l.getIfEntry()) {
                Location loc_n1 = n1.getLocation();
                Location loc_n2 = n2.getLocation();
                System.out.println("Location 1: " + loc_n1);
                System.out.println("Location 2: " + loc_n2);
                // Shape line =
                g1.draw( new Line2D.Double(loc_n1.getX(), loc_n1.getY(), loc_n2.getX(), loc_n2.getY()) );
            }
        }


        // g1.draw(new Line2D.Double(mar, mar, mar, height - mar));



    }

    private ArrayList<Double> getMinMax() {
        ArrayList<Double> minXmaxXminYmaxY = new ArrayList<>();
        // min_x, max_x, min_y, max_y
        double min_x = Double.MAX_VALUE;
        double min_y = Double.MAX_VALUE;
        double max_x = Double.MIN_VALUE;
        double max_y = Double.MIN_VALUE;
        double x,y;
        for (Node n : this.nodeSet) {
            Location loc = n.getLocation();
            x = loc.getX();
            y = loc.getY();
            min_x = Math.min(min_x, x);
            min_y = Math.min(min_y, y);
            max_x = Math.min(max_x, x);
            max_y = Math.min(max_y, y);
        }
//        System.out.println("Max x : " + max_x);
//        System.out.println("Max y : " + max_y);
//        System.out.println("Min x : " + min_x);
//        System.out.println("Min y : " + min_y);
        minXmaxXminYmaxY.add(0, min_x);
        minXmaxXminYmaxY.add(1, max_x);
        minXmaxXminYmaxY.add(2, min_y);
        minXmaxXminYmaxY.add(3, max_y);

        return minXmaxXminYmaxY;
    }
}