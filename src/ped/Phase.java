package ped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

A phase is a particular action that can occur at a traffic light.
For example: activated a crosswalk, or activating a particular turn is
    the same as activating a phase

direction: "NS", "NW", "NE", "EN", "ES", "EW", "SN", "SW", "SE", "WN", "WS", "WE"
             0     1     2     3    4      5     6     7    8     9     10    11
direction: "LEFT", "RIGHT", "TOP", "BOTTOM"
             12      13       14      15

 */
public class Phase {
    protected Link i, j;
    protected String direction;
    protected int id;
    private HashMap<Integer, String> numToDirectionMap;
    private HashMap<String, Integer> directionToNumMap;
    protected int queue_length;

    public Phase() {
        // create numToDirectionMap
        numToDirectionMap = new HashMap<>();
        numToDirectionMap.put(0, "NS");
        numToDirectionMap.put(1, "NW");
        numToDirectionMap.put(2, "NE");
        numToDirectionMap.put(3, "EN");
        numToDirectionMap.put(4, "ES");
        numToDirectionMap.put(5, "EW");
        numToDirectionMap.put(6, "SN");
        numToDirectionMap.put(7, "SW");
        numToDirectionMap.put(8, "SE");
        numToDirectionMap.put(9, "WN");
        numToDirectionMap.put(10, "WS");
        numToDirectionMap.put(11, "WE");
        numToDirectionMap.put(12, "LEFT"); // The crosswalk on the left side
        numToDirectionMap.put(13, "RIGHT"); // The crosswalk on the right side
        numToDirectionMap.put(14, "TOP"); // The crosswalk on the top side
        numToDirectionMap.put(15, "BOTTOM"); // The crosswalk on the bottom side

        // create directionToNumMap
        directionToNumMap = new HashMap<>();
        for(Map.Entry<Integer, String> entry : numToDirectionMap.entrySet()){
            directionToNumMap.put(entry.getValue(), entry.getKey());
        }
    }

    public void setId() {
        this.id = directionToNumMap.get(this.direction);
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return direction;
        /*
        return "Turn{" +
                "i=" + i +
                ", j=" + j +
                ", direction='" + direction + '\'' +
                '}';

         */
    }

    public void activate() {
        // TODO: turn links on???
    }

    public void deactivate() {
        // TODO: turn links off???
    }
}
