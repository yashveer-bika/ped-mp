package ped;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Network {
    private Set<Intersection> intersectionSet;
    private Set<Link> linkSet;
    private Set<Node> nodeSet;
    // private Set<VehIntersection> vehIntersectionSet;
    private Set<Integer> nodeIds;
    private int[][] grid;
    private VehIntersection[][] vehIntGrid;
    private Intersection[][] intersectionGrid;

    private HashMap<Intersection, Set<Intersection>> intersectionGraph;
    // private Set<Link> linkSet;
    private HashMap<Integer, VehIntersection> vehInts;
    private HashMap<VehIntersection, Integer> vehInt_to_id;

    private HashMap<Integer, Link> entryLinks;

    /*
    public Network(Set<Intersection> intersectionSet, Set<Link> linkSet, Set<Node> nodeSet) {
        this.intersectionSet = intersectionSet;
        this.linkSet = linkSet;
        this.nodeSet = nodeSet;
    }
     */

    // TODO: make sure this works on Sioux Falls Network

//    public Network(File networkFile) {
//        this.intersectionSet = new HashSet<>();
//        this.linkSet = new HashSet<>();
//        this.nodeSet = new HashSet<>();
//        this.nodeIds = new HashSet<>();
//        this.nodes = new HashMap<>();
//        this.entryLinks = new HashMap<>();
//
//
//        try {
//            String[] header = {};
//            Scanner myReader = new Scanner(networkFile);
//            ArrayList<String[]> link_rows = new ArrayList();
//
//            // read nodes/links from net.txt
//            while (myReader.hasNextLine()) {
//                String data = myReader.nextLine();
//                // things we ignore
//                if (data == "" || data.charAt(0) == '<') {
//                    continue;
//                }
//                if (data.charAt(0) == '~') {
//                    System.out.println("HEADER");
//                    header = data.split("\t");
//                    continue;
//                }
//                if (data.charAt(0) == '\t') { // normal link
//                    String[] link_data = data.split("\t");
//                    link_rows.add(link_data);
//                }
//            }
//            myReader.close();
//
//            /*
//            // print header
//            System.out.println("Header length: ");
//            int i=0;
//            for (String val : header) {
//                System.out.println(val);
//                i++;
//            }
//            System.out.println("i = " + i);
//             */
//
//            /*
//            0: ~
//            1: Init node
//            2: Term node
//            3: Capacity
//            4: Length
//            5: Free Flow Time
//            6: B
//            7: Power
//            8: Speed limit
//            9: Toll
//            10: Type
//            11: ;
//             */
//
//            Node startNode = null;
//            Node endNode = null;
//            // for each row, add the appropriate nodes, links and set them up properly
//            for (String[] link_data : link_rows) {
//                // make init node (if not already made)
//                // make term node (if not already made)
//                // make link (if not already made)
//                // set capacity of link
//                // set length of link
//                // set free flow time of link
//                // WHAT IS B?
//                // what is power?
//                // what is speed limit?
//                // what is toll?
//                // what is type?
//
//                /*
//                0: ~
//                1: Init node
//                2: Term node
//                3: Capacity
//                4: Length
//                5: Free Flow Time
//                6: B
//                7: Power
//                8: Speed limit
//                9: Toll
//                10: Type
//                11: ;
//                 */
//
//                int startNodeId = Integer.parseInt(link_data[1]);
//                int endNodeId = Integer.parseInt(link_data[2]);
//                double linkCapacity = Double.parseDouble(link_data[3]);
//                int linkLength = Integer.parseInt(link_data[4]);
//                int linkFFT = Integer.parseInt(link_data[5]);
//                double linkB = Double.parseDouble(link_data[6]);
//                int linkPower = Integer.parseInt(link_data[7]);
//                int linkSpeedLim = Integer.parseInt(link_data[8]);
//                int linkToll = Integer.parseInt(link_data[9]);
//                int linkType = Integer.parseInt(link_data[10]);
//
//                // make init node (if not already made)
//                startNode = nodes.get(startNodeId);
//                if (startNode == null) {
//                    startNode = new VehIntersection(startNodeId);
//                    // set up entry link for the new node
//                    Link entry_link = new Link(null, startNode, Double.MAX_VALUE, true);
//                    linkSet.add(entry_link);
//                    entryLinks.put(startNodeId, entry_link);
//                    nodes.put(startNodeId, startNode);
//                    // create the 4 PedIntersection around this VehIntersection
//                    // connect the pedIntersection with crosswalks
//
//                }
//
//                // make term node (if not already made)
//                endNode = nodes.get(endNodeId);
//                if (endNode == null) {
//                    endNode = new VehIntersection(endNodeId);
//                    // set up entry link for the new node
//                    Link entry_link = new Link(null, endNode, Double.MAX_VALUE, true);
//                    linkSet.add(entry_link);
//                    entryLinks.put(endNodeId, entry_link);
//                    nodes.put(endNodeId, endNode);
//                }
//
//                // make link, TODO: Q: how do I setup direction?
//                // and set capacity of link
//                Link newLink = new Link(startNode, endNode, linkCapacity);
//                linkSet.add(newLink);
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//    }
//

    public Network() {

    }

    public void loadNetworkData(File nodesFile, File linksFile) {
        this.intersectionSet = new HashSet<>();
        this.linkSet = new HashSet<>();
        this.nodeSet = new HashSet<>();
        this.nodeIds = new HashSet<>();
        this.vehInts = new HashMap<>();
        this.entryLinks = new HashMap<>();
        // this.vehIntersectionSet = new HashSet<>();

        this.grid = new int[8][8]; // NOTE: this is hardcoded for the SF data
        this.vehIntGrid = new VehIntersection[8][8];
        this.intersectionGrid = new Intersection[8][8];


        this.intersectionGraph = new HashMap<>();

        // load nodes
        try {
            String[] header = {};
            Scanner myReader = new Scanner(nodesFile);
            ArrayList<String[]> link_rows = new ArrayList();

            // read nodes/links from net.txt
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // things we ignore
                if (data.charAt(0) == '9' && data.charAt(1) == '0') {
                    continue;
                }
                if (data.charAt(0) == 'i') {
                    System.out.println("HEADER");
                    header = data.split("\t");
                    continue;
                }
                else { // normal link
                    String[] link_data = data.split("\t");
                    link_rows.add(link_data);
                }
            }
            myReader.close();

            System.out.println(Arrays.toString(header));

            // System.out.println(link_rows);


            /*
            // print header
            System.out.println("Header length: ");
            int i=0;
            for (String val : header) {
                System.out.println(val);
                i++;
            }
            System.out.println("i = " + i);
             */

            /*
            0: ~
            1: Init node
            2: Term node
            3: Capacity
            4: Length
            5: Free Flow Time
            6: B
            7: Power
            8: Speed limit
            9: Toll
            10: Type
            11: ;
             */

            VehIntersection node = null;
            // Node endNode = null;
            // for each row, add the appropriate nodes
            for (String[] link_data : link_rows) {
                /*
                0: id
                1: type
                2: longitude
                3: latitude
                4: elevation
                 */

                int nodeId = Integer.parseInt(link_data[0]);
                // int nodeType = Integer.parseInt(link_data[1]);
                double longitude = Double.parseDouble(link_data[2]);
                double latitude = Double.parseDouble(link_data[3]);
                double elevation = Double.parseDouble(link_data[4]);

                // make node

                node = new VehIntersection(nodeId);
                // set up entry link for the new node
                Link entry_link = new Link(null, node, Double.MAX_VALUE, true);
                assert nodeId == node.getId();
                assert nodeId == entry_link.getDestination().getId();

                node.addIncomingLink(entry_link);
                linkSet.add(entry_link);
                entryLinks.put(nodeId, entry_link);
                this.vehInts.put(nodeId, node);
                // put into temp grid to see location
                int rowPos = (int) (latitude / 10);
                int colPos = (int) (longitude / 10);
                // System.out.println("id: " + nodeId + " latitude: " + latitude + " longitude: " + longitude);
                // System.out.println(rowPos);
                node.setRowPosition(rowPos);
                node.setColPosition(colPos);
                grid[rowPos][colPos] = nodeId;
                vehIntGrid[rowPos][colPos] = node;
                intersectionGrid[rowPos][colPos] = null;
            }
            // print grid
            // /*
            System.out.println("------GRID------");
            for (int[] row : grid) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println("----------------");

            // */
            // vehicleGraph.put(node, new HashSet<>());

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // load links
        try {
            String[] header = {};
            Scanner myReader = new Scanner(linksFile);
            ArrayList<String[]> link_rows = new ArrayList();

            // read links from net.txt
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // things we ignore
                if (data.charAt(0) == 'i') {
                    System.out.println("HEADER");
                    header = data.split("\t");
                    continue;
                }
                else { // normal link
                    String[] link_data = data.split("\t");
                    link_rows.add(link_data);
                }
            }
            myReader.close();

            // System.out.println(Arrays.toString(header));

            for (String[] row : link_rows) {
                // System.out.println(Arrays.toString(row));
            }

            VehIntersection startNode = null;
            VehIntersection endNode = null;
            // for add the links and connect them up properly
            for (String[] link_data : link_rows) {
                /*
                HEADER
                [id, type, source, dest, length (ft), ffspd (mph), w (mph), capacity, num_lanes]
                 */

                int srcId = Integer.parseInt(link_data[2]);
                int destId = Integer.parseInt(link_data[3]);
                int capacity = Integer.parseInt(link_data[7]);


                startNode = vehInts.get(srcId);
                endNode = vehInts.get(destId);
                // vehicleGraph.get(startNode).add((VehIntersection) endNode);
                int src_row = startNode.getRowPosition();
                int src_col = startNode.getColPosition();
                int dest_row = endNode.getRowPosition();
                int dest_col = endNode.getColPosition();

                String direction = null;

                if (src_row == dest_row && src_col < dest_col) {
                    direction = "WE";
                } else if (src_row == dest_row && src_col > dest_col) {
                    direction = "EW";
                } else if (src_row < dest_row && src_col == dest_col) {
                    direction = "NS";
                } else if (src_row > dest_row && src_col == dest_col) {
                    direction = "SN";
                } else { // ignore "skew" roads (for now)
                    // prints out skew cases
                    // System.out.println("srcID: " + srcId + " destId: " + destId);
                    continue;
                }
                // System.out.println("srcNode lat/long: " + startNode.getRowPosition() + " " + startNode.getColPosition());
                // System.out.println("endNode lat/long: " + endNode.getRowPosition() + " " + endNode.getColPosition());
                VehLink link = new VehLink(startNode, endNode, capacity, direction);
                assert link.getStart() == startNode;
                assert link.getDestination() == endNode;

                linkSet.add(link);
                // set incoming, outgoing links
                endNode.addIncomingLink(link);
                startNode.addOutgoingLink(link);

                // Print out incoming links for each end node
//                System.out.print(endNode.getId() + ": ");
//                System.out.println(endNode.getIncomingLinks());


                // Print out outgoing links for each start node
//                System.out.print(startNode.getId() + ": ");
//                System.out.println(startNode.getOutgoingLinks());

            }
            // print grid
            /*
            for (int[] row : grid) {
                System.out.println(Arrays.toString(row));
            }
             */
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // iterate over vehInts to make the pedIntersections around each vehInt
        for (Integer key : this.vehInts.keySet()) {
            VehIntersection vehInt = this.vehInts.get(key);
            // vehInt.setVehLinks(); // make sure that all my incoming and outgoings are
            // Create the 4 PedIntersection objects around this VehIntersection
            PedIntersection ped1 = new PedIntersection();
            PedIntersection ped2 = new PedIntersection();
            PedIntersection ped3 = new PedIntersection();
            PedIntersection ped4 = new PedIntersection();
            ArrayList<PedIntersection> pedInts = new ArrayList<>();
            pedInts.add(ped1);
            pedInts.add(ped2);
            pedInts.add(ped3);
            pedInts.add(ped4);

            // add entry links
            PedLink entryPl1 = new PedLink(null, ped1, Double.MAX_VALUE, true);
            PedLink entryPl2 = new PedLink(null, ped2, Double.MAX_VALUE, true);
            PedLink entryPl3 = new PedLink(null, ped3, Double.MAX_VALUE, true);
            PedLink entryPl4 = new PedLink(null, ped4, Double.MAX_VALUE, true);
            // tell the intersections they have the connection

            // connect the pedIntersection with crosswalks
            Crosswalk cTop = null;
            Crosswalk cLeft = null;
            Crosswalk cRight = null;
            Crosswalk cBottom = null;
            Set<Crosswalk> crosswalks = new HashSet<>();

            // see if a road crosses the crosswalk
            // if so, we need a crosswalk object
            // else, we make sidewalks (NOTE: maybe put sidewalk logic here?)
            for (Link link : vehInt.getOutgoingLinks() ) {
                assert vehInt == link.getStart();

                String dir = link.getDirection();
                if (dir == "SN" && cTop == null) {
                    // make 2 pedLinks
                    // NOTE: update the capacity value for pedestrians
                    PedLink way1 = new PedLink(ped1, ped2, Double.MAX_VALUE, true);
                    PedLink way2 = new PedLink(ped2, ped1, Double.MAX_VALUE, true);
                    cTop = new Crosswalk(way1, way2, "TOP");
                    crosswalks.add(cTop);
                }
                if (dir == "NS" && cBottom == null) {
                    // make 2 pedLinks
                    // NOTE: update the capacity value for pedestrians
                    PedLink way1 = new PedLink(ped3, ped4, Double.MAX_VALUE, true);
                    PedLink way2 = new PedLink(ped4, ped3, Double.MAX_VALUE, true);
                    cBottom = new Crosswalk(way1, way2, "BOTTOM");
                    crosswalks.add(cBottom);
                }
                if (dir == "EW" && cLeft == null) {
                    // make 2 pedLinks
                    // NOTE: update the capacity value for pedestrians
                    PedLink way1 = new PedLink(ped1, ped3, Double.MAX_VALUE, true);
                    PedLink way2 = new PedLink(ped3, ped1, Double.MAX_VALUE, true);
                    cLeft = new Crosswalk(way1, way2, "LEFT");
                    crosswalks.add(cLeft);
                }
                if (dir == "WE" && cRight == null) {
                    // make 2 pedLinks
                    // NOTE: update the capacity value for pedestrians
                    PedLink way1 = new PedLink(ped2, ped4, Double.MAX_VALUE, true);
                    PedLink way2 = new PedLink(ped4, ped2, Double.MAX_VALUE, true);
                    cRight = new Crosswalk(way1, way2, "RIGHT");
                    crosswalks.add(cRight);
                }
            }

            int colPos = vehInt.getColPosition();
            int rowPos = vehInt.getRowPosition();

            // set pedestrian links for vehIn
//            System.out.println("crosswalks");
//            System.out.println(crosswalks);
            Intersection int_sec = new Intersection(vehInt, pedInts, crosswalks);
            intersectionGrid[rowPos][colPos] = int_sec;
            intersectionSet.add(int_sec);
        }


        // reverse vehInts to make vehInt_to_id
        vehInt_to_id = new HashMap<>();
        for(Map.Entry<Integer, VehIntersection> entry : vehInts.entrySet()){
            vehInt_to_id.put(entry.getValue(), entry.getKey());
        }

        // print vehInt_to_id
//        System.out.print("vehInt_to_id");
//        for(Map.Entry<VehIntersection, Integer> entry : vehInt_to_id.entrySet()){
//            System.out.println(entry.getValue() + " " + entry.getKey());
//        }


        // TODO: fix this loop
        // TODO: fix the outgoing links issue
//        vehInt id
//        2
//        7 0 (link)
//        6 3 (link)
        // create a graph of Intersections
//        System.out.println(this.intersectionSet.size());
//        for (Intersection int_sec : this.intersectionSet) {
//            System.out.println(int_sec);
//        }

//        System.out.println("-----------------------------------");
//        System.out.println(vehInt_to_id);
//        System.out.println("-----------------------------------");


        for (Intersection intersection_ : this.intersectionSet) {
            Set<Intersection> neighbors = new HashSet<>();
            VehIntersection vehInt = intersection_.getVehInt();
//            System.out.println("outgoing VehLinks");
//            System.out.println(vehInt.getOutgoingLinks());
//            System.out.println( "vehInt id" );
//            System.out.println( vehInt_to_id.get(vehInt) );
//            System.out.println("Number of neighbors: " + vehInt.getOutgoingLinks().size());
//            System.out.println("Outgoing links: " + vehInt.getOutgoingLinks());
            for (Link outVehLink : vehInt.getOutgoingLinks()) {
//                System.out.println(outVehLink);
                Node outVehInt = outVehLink.getDestination();
                assert vehInt == outVehLink.getStart();
//                int id_ = vehInt_to_id.get(outVehInt);
//                System.out.println(id_);
                int rowPos = outVehInt.getRowPosition();
                int colPos = outVehInt.getColPosition();
                neighbors.add(intersectionGrid[rowPos][colPos]);
            }
            // System.out.println(neighbors);
            intersectionGraph.put(intersection_, neighbors);
        }
        // print out all the neighbors
        // System.out.println(intersectionGraph.values());

        System.out.println("NUMBER OF LINKS BEFORE ADDING SIDEWALKS");
        System.out.println(this.linkSet.size());

        // initialize "visited" hash map
        Set<Intersection> visitedInt = new HashSet<>();

        // create sidewalks??
        // TODO: test that sidewalks are made
        for (Intersection int_sec : intersectionSet) {
            VehIntersection vehInt = int_sec.getVehInt();
            int src_col = vehInt.getColPosition();
            int src_row = vehInt.getRowPosition();
            ArrayList<PedIntersection> srcPedInts = int_sec.getPedInts();
//            System.out.println("Neighbors");
//            System.out.println(intersectionGraph.get(int_sec));

            for (Intersection neighbor : intersectionGraph.get(int_sec)  ) {

                // use a "visited" list to prevent redundant search at nodes
                if (visitedInt.contains(neighbor)) {
                    continue;
                }
                VehIntersection vehInt2 = neighbor.getVehInt();
                int dest_col = vehInt2.getColPosition();
                int dest_row = vehInt2.getRowPosition();

                System.out.println(src_row + " : " + src_col);
                System.out.println(dest_row + " : " + dest_col);

                // get the direction based on the rows and cols
                String direction = "SKEW";
                if (src_row == dest_row && src_col < dest_col) {
                    direction = "WE";
                } else if (src_row == dest_row && src_col > dest_col) {
                    direction = "EW";
                } else if (src_row < dest_row && src_col == dest_col) {
                    direction = "NS";
                } else if (src_row > dest_row && src_col == dest_col) {
                    direction = "SN";
                } else { // fail on "skew" roads (for now)
                    // throw new IllegalArgumentException();
                }

                System.out.println("Direction");
                System.out.println(direction);

                ArrayList<PedIntersection> destPedInts = neighbor.getPedInts();
                PedLink sidewalk1;
                PedLink sidewalk1_;
                PedLink sidewalk2;
                PedLink sidewalk2_;
                // src Top + dest Bottom
                if (direction == "SN") {
                    sidewalk1 = new PedLink(srcPedInts.get(0), destPedInts.get(2), true, Double.MAX_VALUE);
                    sidewalk1_ = new PedLink(destPedInts.get(2), srcPedInts.get(0), true, Double.MAX_VALUE);
                    sidewalk2 = new PedLink(srcPedInts.get(1), destPedInts.get(3), true, Double.MAX_VALUE);
                    sidewalk2_ = new PedLink(destPedInts.get(3), srcPedInts.get(1), true, Double.MAX_VALUE);
                }
                // src Bottom + dest Top
                else if (direction == "NS") {
                    sidewalk1 = new PedLink(srcPedInts.get(2), destPedInts.get(0), true, Double.MAX_VALUE);
                    sidewalk1_ = new PedLink(destPedInts.get(0), srcPedInts.get(2), true, Double.MAX_VALUE);
                    sidewalk2 = new PedLink(srcPedInts.get(3), destPedInts.get(1), true, Double.MAX_VALUE);
                    sidewalk2_ = new PedLink(destPedInts.get(1), srcPedInts.get(3), true, Double.MAX_VALUE);
                }
                // src Left + dest Right
                else if (direction == "WE") {
                    sidewalk1 = new PedLink(srcPedInts.get(0), destPedInts.get(1), true, Double.MAX_VALUE);
                    sidewalk1_ = new PedLink(destPedInts.get(1), srcPedInts.get(0), true, Double.MAX_VALUE);
                    sidewalk2 = new PedLink(srcPedInts.get(2), destPedInts.get(3), true, Double.MAX_VALUE);
                    sidewalk2_ = new PedLink(destPedInts.get(3), srcPedInts.get(2), true, Double.MAX_VALUE);
                }
                // src Right + dest Left
                else if (direction == "EW") {
                    sidewalk1 = new PedLink(srcPedInts.get(1), destPedInts.get(0), true, Double.MAX_VALUE);
                    sidewalk1_ = new PedLink(destPedInts.get(0), srcPedInts.get(1), true, Double.MAX_VALUE);
                    sidewalk2 = new PedLink(srcPedInts.get(3), destPedInts.get(2), true, Double.MAX_VALUE);
                    sidewalk2_ = new PedLink(destPedInts.get(2), srcPedInts.get(3), true, Double.MAX_VALUE);
                }
                else { // HOW WOULD YOU GET HERE? if SS, NN, EE, WW direction
                    continue;
                    // throw new IllegalArgumentException();
                }

//                System.out.println(sidewalk1);
//                System.out.println("Size after adding 1 sidewalk" + this.linkSet.size());
//                System.out.println(sidewalk1_);
//                System.out.println(sidewalk2);
//                System.out.println(sidewalk2_);

                this.linkSet.add(sidewalk1);
                this.linkSet.add(sidewalk1_);
                this.linkSet.add(sidewalk2);
                this.linkSet.add(sidewalk2_);
            }

            visitedInt.add(int_sec);
        }

        System.out.println("NUMBER OF LINKS AFTER ADDING SIDEWALKS");
        System.out.println(this.linkSet.size());

    }





    // get node from nodeSet
    // linear runtime
    public Node getNode(int id) {
        for (Node node : nodeSet) {
            if (id == node.getId()) {
                return node;
            }
        }
        return null;
    }

    public Set<Intersection> getIntersectionSet() {
        return intersectionSet;
    }

    public Set<Integer> getNodeIds() {
        return nodeIds;
    }

    public Set<Node> getNodeSet() {
        return nodeSet;
    }

    public Set<Link> getLinkSet() {
        return this.linkSet;
    }

    public HashMap<Integer, VehIntersection> getNodes() {
        return vehInts;
    }

    public void addDemand() {
        // set demand values for each node by adding
        // peds/vehicles into the entry links
    }

    /**
     * method: runSimulation(int time_steps)
     * purpose: run through a simulation with over 'time_steps' number of discrete time steps
     * parameters:
     */

    /**
     * PURPOSE: run all the operations of the simulation for one time step
     * parameters:
     *
     * pseudocode
     *  for each intersection
     *      use max pressure to find best phase set at the current time
     *      activate the phase chosen the by controller
     *      move vehicles / pedestrians according to the phase
     *
     */
    public void incrementTime() {

    }

}
