package ped;

import java.util.ArrayList;
import java.util.List;

public class TestPointQueue {
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    Node n3 = new Node(3);
    Node n4 = new Node(4);
    Node n5 = new Node(5);

    Link l1 = new PointQueue(12, n1, n2, 120, 2, 800, 1);
    Link l2 = new PointQueue(23, n2, n3, 120, 2, 800, 1);

    ArrayList<Node> path1 = new ArrayList<Node>() {
        {
            add(n1);
            add(n2);
            add(n3);
        }
    };
    ArrayList<Node> path2 = new ArrayList<Node>() {
        {
            add(n1);
            add(n2);
            add(n4);
        }
    };
    ArrayList<Node> path3 = new ArrayList<Node>() {
        {
            add(n5);
            add(n2);
            add(n4);
        }
    };

    ArrayList<Node> path4 = new ArrayList<Node>() {
        {
            add(n1);
            add(n5);
            add(n4);
        }
    };

    Vehicle v1 = new Vehicle(path1);
    Vehicle v2 = new Vehicle(path2);
//    Vehicle v3 = new Vehicle(path3);

    public TestPointQueue() {
        l1.addVehicle(v1);
        l1.addVehicle(v2);
    }

    void testGetVehicles() {
        System.out.println("Testing PointQueue.getVehs()");
        List<Vehicle> vehs = new ArrayList<>();
        vehs.add(v1);
        vehs.add(v2);

        assert vehs.containsAll(l1.getVehs());
        assert l1.getVehs().containsAll(vehs);
    }
}
