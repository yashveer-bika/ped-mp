//package ped;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestVehicle {
//    Node n1 = new Node(1, 0, 0);
//    Node n2 = new Node(2, 1, 0);
//    Node n3 = new Node(3, 2, -1);
//    Node n4 = new Node(4, 2, 1);
//    Node n5 = new Node(5, 3, 0);
//
//    ArrayList<Node> path1 = new ArrayList<Node>() {
//        {
//            add(n1);
//            add(n2);
//            add(n3);
//            add(n4);
//            add(n5);
//        }
//    };
//
//    ArrayList<Node> path2 = new ArrayList<Node>() {
//        {
//            add(n1);
//            add(n3);
//            add(n3);
//            add(n5);
//            add(n4);
//        }
//    };
//
//    Vehicle v1 = new Vehicle(path1);
//    Vehicle v2 = new Vehicle(path2);
//
//    void testGetNextNode() {
//        System.out.println("Test get next node");
//        assert v1.getNextNode(0).getId() == 1;
//        assert v1.getNextNode(1).getId() == 2;
//        assert v1.getNextNode(2).getId() == 3;
//        assert v1.getNextNode(3).getId() == 4;
//        assert v1.getNextNode(4).getId() == 5;
//        try {
//            v1.getNextNode(5).getId();
//            assert 1 == 2; // we should not reach here if the getNextNode is correct
//        } catch (NullPointerException e) {
//
//        }
//    }
//}
