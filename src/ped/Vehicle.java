package ped;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    static int cur_id = 0;
    int id;
    List<Node> path;
    int path_index;

    public Vehicle(List<Node> path) {
        this.path = path;
        id = cur_id;
        cur_id++;
        path_index = 0;
    }

    public Node moveVehicle() {
//        System.out.println("MOVING VEHICLE");
        if (path_index + 1 == path.size()) {
            return null;
        }
        Node n1 = getCurrentNode();
        path_index++;
        Node n2 = getCurrentNode();
        assert n1 != n2;
        return path.get(path_index);
    }

    public Node getNextNode(int steps) {
        if (path_index + steps >= path.size()) {
            return null;
        }
        return path.get(path_index + steps);
    }

    public Node getCurrentNode() {
        return getNextNode(0);
    }

    public List<Node> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Veh#" + id;
    }
}
