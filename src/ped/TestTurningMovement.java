package ped;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestTurningMovement {
    Node n1 = new Node(1, 0, 0);
    Node n2 = new Node(2, 1, 0);
    Node n3 = new Node(3, 2, -1);
    Node n4 = new Node(4, 2, 1);

    Link l1 = new PointQueue(12, n1, n2, 120, 2, 800, 1);
    Link l2 = new PointQueue(23, n2, n3, 120, 2, 800, 1);

    TurningMovement tm = new TurningMovement(l1, l2);

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

    Vehicle v1 = new Vehicle(path1);
    Vehicle v2 = new Vehicle(path2);

    public TestTurningMovement() {
        l1.addVehicle(v1);
        l1.addVehicle(v2);
    }

    void testGetVehicles() {
        System.out.println("TESTING TurningMovement.getVehicles()");
        List<Vehicle> expectedVehicles = new ArrayList<>();
        expectedVehicles.add(v1);
//        System.out.println("Link i vehs: " + tm.getIncomingLink().getVehs());
//        System.out.println("TM vehs: " + tm.getVehicles());
//        System.out.println("expectedVehicles: " + expectedVehicles);
        assert expectedVehicles.equals(tm.getVehicles());

    }

    void testGetQueueLength() {
        System.out.println("TESTING TurningMovement.getVehicles()");
        List<Vehicle> expectedVehicles = new ArrayList<>();
        expectedVehicles.add(v1);
//        System.out.println("Link i vehs: " + tm.getIncomingLink().getVehs());
//        System.out.println("TM vehs: " + tm.getVehicles());
//        System.out.println("expectedVehicles: " + expectedVehicles);
        assert expectedVehicles.equals(tm.getVehicles());

    }
}
