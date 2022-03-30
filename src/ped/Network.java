package ped;

import util.Angle;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Network {
    private Set<Intersection> intersectionSet;
    private Set<Link> linkSet;
    private Set<Node> nodeSet;
    private Set<Integer> nodeIds;
    // private int[][] grid;
    private VehIntersection[][] vehIntGrid;
    private Intersection[][] intersectionGrid;

    private HashMap<Intersection, Set<Intersection>> intersectionGraph;
    private HashMap<Integer, VehIntersection> vehInts;
    private HashMap<VehIntersection, Integer> vehInt_to_id;
    private HashMap<Integer, Link> entryLinks;
    private Set<PedNode> pedNodes;
    // the doubles are the angles w.r.t to the vehInt corresponding to the PedNode
    private HashMap<PedNode, Set<Double>> pedNodesRoadAngles;
    private HashMap<PedNode, Set<Link>> pedNodeRoads;

    private double networkTime; // total network time (time in simulation)
    private double cycleTime; // time within a cycle of the simulation
    private double toleranceTime; // pedestrian tolerance time
    private boolean ped; // says whether we load the network with pedestrians



    public Network(File nodesFile, File linksFile, boolean ped) {
        this.pedNodeRoads = new HashMap<>();
        this.pedNodesRoadAngles = new HashMap<>();
        this.intersectionSet = new HashSet<>();
        this.linkSet = new HashSet<>();
        this.nodeSet = new HashSet<>();
        this.nodeIds = new HashSet<>();
        this.vehInts = new HashMap<>();
        this.entryLinks = new HashMap<>();
        this.intersectionGraph = new HashMap<>();
        this.pedNodes = new HashSet<>();
        this.ped = ped;

        // load vehicle intersections
        this.loadNodes_constructor(nodesFile);

//        // print out vehicle int grid
//        for (VehIntersection[] vehIntRow: vehIntGrid) {
//            for (VehIntersection cell : vehIntRow) {
//                System.out.print(cell + " ");
//            }
//            System.out.println();
//        }

        // load vehicle links (to make complete vehicle network)
        // creates vehicle intersections
        this.loadLinks_constructor(linksFile);

        // create pedIntersection(s) around each vehicle intersection
        // creates crosswalks within an intersection
        // creates Intersection objects
        if (ped) {
            loadPedIntersections_constructor();
        }
        else {
            loadIntersections_constructor();
        }

        // create intersection graph
        this.createIntersectionGraph_constructor();

        if (ped) {
            this.loadSidewalks_constructor();
        }


        // reverse vehInts to make vehInt_to_id
//        vehInt_to_id = new HashMap<>();
//        for(Map.Entry<Integer, VehIntersection> entry : vehInts.entrySet()){
//            vehInt_to_id.put(entry.getValue(), entry.getKey());
//        }

        this.finishLoading();
    }

    /** CONSTRUCTOR HELPERS **/

    public void finishLoading() {
        for (Intersection i : this.getIntersectionSet()) {
            i.finishLoading();
        }
    }

    public Set<PedNode> getPedNodes() {
        return pedNodes;
    }

    private void loadNodes_constructor(File nodesFile) {
        try {
            String[] header = {};
            Scanner myReader = new Scanner(nodesFile);
            ArrayList<String[]> node_rows = new ArrayList();

            // read nodes from nodesFile
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // things we ignore
                if (data.charAt(0) == '9' && data.charAt(1) == '0') {
                    continue;
                }
                // at header
                if (data.charAt(0) == 'i') {
//                    System.out.println("HEADER");
                    header = data.split("\t");
                }
                // normal node
                else {
                    String[] node_data = data.split("\t");
                    node_rows.add(node_data);
                }
            }
            myReader.close();

//            // print out the data
//            System.out.println(Arrays.toString(header));
//            for (String[] row : link_rows) {
//                System.out.println(Arrays.toString(row));
//            }

            // search link_rows to find the minimum and maximum lat and long
            // then initialize the grid accordingly
            // max_lat - min_lat cross max_long - min_long
            // shift values by the min, so (min_lat, min_long) is (0,0)

            double max_long = Double.MIN_VALUE;
            double max_lat = Double.MIN_VALUE;
            double min_long = Double.MAX_VALUE;
            double min_lat = Double.MAX_VALUE;
            double cur_lat;
            double cur_long;

            for (String[] row : node_rows) {
                // System.out.println("Row length: " + row.length);
                // System.out.println("Row[0]: " + row[0]);

                cur_long = Double.parseDouble(row[2]);
                if (cur_long > max_long) {
                    max_long = cur_long;
                }
                if (cur_long < min_long) {
                    min_long = cur_long;
                }
//                System.out.print("Cur long: " + cur_long);

                cur_lat = Double.parseDouble(row[3]);
//                System.out.print("  Cur lat: " + cur_lat + "\n");
                if (cur_lat > max_lat) {
                    max_lat = cur_lat;
                }
                if (cur_lat < min_lat) {
                    min_lat = cur_lat;
                }
            }
//            System.out.println(min_long);
//            System.out.println(max_long);
//            System.out.println(min_lat);
//            System.out.println(max_lat);

            // initialize the grids to the correct size.
//            System.out.println("Lat range: " + (max_lat - min_lat + 1));
//            System.out.println("Long range: " + (max_long - min_long + 1));

            this.vehIntGrid = new VehIntersection[(int) (max_lat - min_lat + 1)][(int) (max_long - min_long + 1)];
            this.intersectionGrid = new Intersection[(int) (max_lat - min_lat + 1)][(int) (max_long - min_long + 1)];

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
            for (String[] node_data : node_rows) {
                /*
                0: id
                1: type
                2: longitude
                3: latitude
                4: elevation
                 */

                int nodeId = Integer.parseInt(node_data[0]);
                // int nodeType = Integer.parseInt(link_data[1]);
                double longitude = Double.parseDouble(node_data[2]);
                double latitude = Double.parseDouble(node_data[3]);
                double elevation = Double.parseDouble(node_data[4]);

                // make node

                node = new VehIntersection(nodeId);
                // set up entry link for the new node
                Link entry_link = new Link(null, node, Double.MAX_VALUE, true);
                assert nodeId == node.getId();
                assert nodeId == entry_link.getDestination().getId();

                node.addEntryLink(entry_link);
                this.linkSet.add(entry_link);
                this.entryLinks.put(nodeId, entry_link);
                this.vehInts.put(nodeId, node);
                // put into temp grid to see location
                double rowPos = (latitude) - min_lat; // shift by min_lat in case we have negative lat
                double colPos = (longitude) - min_long; // shift by min_long in case we have negative long
                // System.out.println("id: " + nodeId + " latitude: " + latitude + " longitude: " + longitude);
                // System.out.println(rowPos);
                node.setRowPosition((int) rowPos);
                node.setColPosition((int) colPos);
                node.setLocation(new Location(longitude, latitude));

                this.nodeSet.add(node);

//                this.grid[rowPos][colPos] = nodeId;
                this.vehIntGrid[(int) rowPos][(int) colPos] = node;
                this.intersectionGrid[(int) rowPos][(int) colPos] = null;
            }
            // print grid
            // /*
//            System.out.println("------GRID------");
//            for (int[] row : this.grid) {
//                System.out.println(Arrays.toString(row));
//            }
//            System.out.println("----------------");

            // */
            // vehicleGraph.put(node, new HashSet<>());

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void loadLinks_constructor(File linksFile) {
        try {
            String[] header = {};
            Scanner myReader = new Scanner(linksFile);
            ArrayList<String[]> link_rows = new ArrayList();

            // read links from linksFile
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // header
                if (data.charAt(0) == 'i') {
//                    System.out.println("HEADER");
                    header = data.split("\t");
                }
                // normal link
                else {
                    String[] link_data = data.split("\t");
                    link_rows.add(link_data);
                }
            }
            myReader.close();

//            // print link data
//            System.out.println(Arrays.toString(header));
//            for (String[] row : link_rows) {
//                 System.out.println(Arrays.toString(row));
//            }

            VehIntersection startNode = null;
            VehIntersection endNode = null;

            // add the links and connect them up properly
            for (String[] link_data : link_rows) {
                /*
                HEADER for SF data
                [id, type, source, dest, length (ft), ffspd (mph), w (mph), capacity, num_lanes]
                 */
                /*
                HEADER for PQ3 data
                [id, type, source, dest, length (mi), ffspd (mph), capacity(veh/hr), num_lanes]
                 */

                int srcId = Integer.parseInt(link_data[2]);
                int destId = Integer.parseInt(link_data[3]);
                int capacity = Integer.parseInt(link_data[6]);


                startNode = vehInts.get(srcId);
                endNode = vehInts.get(destId);
                // vehicleGraph.get(startNode).add((VehIntersection) endNode);T
                Location src = startNode.getLocation();
                Location dest = endNode.getLocation();


                // TODO: do something about the direction code
                String direction = null;
                double angle = Location.angle(src, dest);


                VehLink link = new VehLink(startNode, endNode, capacity, direction, angle);
                assert link.getStart() == startNode;
                assert link.getDestination() == endNode;

                this.linkSet.add(link);
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
    }

    private void createIntersectionGraph_constructor() {
        // create intersection graph
        for (Intersection intersection_ : this.intersectionSet) {
            Set<Intersection> neighbors = new HashSet<>();
            VehIntersection vehInt = intersection_.getVehInt();
            for (Link outVehLink : vehInt.getOutgoingLinks()) {
//                System.out.println(outVehLink);
                Node outVehInt = outVehLink.getDestination();
                assert vehInt == outVehLink.getStart();
//                int id_ = vehInt_to_id.get(outVehInt);
//                System.out.println(id_);

                int rowPos = outVehInt.getRowPosition();
                int colPos = outVehInt.getColPosition();
                Intersection int_ =  intersectionGrid[rowPos][colPos];
                if (int_ == null) {

                } else {
                    neighbors.add(int_);
                }
            }

            for (Link inVehLink : vehInt.getIncomingLinks()) {
//                System.out.println(outVehLink);
                Node inVehInt = inVehLink.getStart();
                assert vehInt == inVehLink.getDestination();
//                int id_ = vehInt_to_id.get(outVehInt);
//                System.out.println(id_);
                int rowPos = inVehInt.getRowPosition();
                int colPos = inVehInt.getColPosition();
                Intersection int_ =  intersectionGrid[rowPos][colPos];
                if (int_ == null) {

                } else {
                    neighbors.add(int_);
                }
            }
            // System.out.println(neighbors);
            intersectionGraph.put(intersection_, neighbors);
        }
        // print out all the neighbors
        // System.out.println(intersectionGraph.values());

    }

    private void loadPedIntersections_constructor() {
        int crosswalk_count = 0;
        assert this.pedNodes.size() == 0 : "Should have no pedNodes before loading the pedInts";
        double distanceFromVehLink = 0.25;

        // iterate over vehInts to make the pedIntersections around each vehInt
        for (Integer key : this.vehInts.keySet()) {
            VehIntersection vehInt = this.vehInts.get(key);
            ArrayList<PedIntersection> pedInts = new ArrayList<>();
            Set<PedNode> pedNodes = new HashSet<>();
            Set<Crosswalk> crosswalks = new HashSet<>();
            // TODO: set the controller of the intersection (null for pseudo-intersections)
            Location vehIntLoc = vehInt.getLocation();

            HashMap<Link, Double> vehLinkAngles = getAngles(vehInt);
            List<Double> angles_ = new ArrayList();
            List<Link> vehLinks = new ArrayList();
            // HashMap<Link, Double> linkAngles = getAngles(vehInt);
            angles_.addAll(vehLinkAngles.values());
            vehLinks.addAll(vehLinkAngles.keySet());

            // get the neighbors
            Set<Node> neighs = new HashSet<>();
            for (Link l : vehInt.getIncomingLinks()) {
                neighs.add( l.getStart() );
            }
            for (Link l : vehInt.getOutgoingLinks()) {
                neighs.add( l.getDestination() );
            }
            int num_neighs = neighs.size();

            // based on the num_neighs (forks),
            // determine the pedNode locations
            // if we only have 1 neighboring intersection
            // no pedInts are needed but we want some pedNodes to allow sidewalk connections
            if (num_neighs == 1) {
                // find the single link and get the link's angle
                Link singleLink = null;
                double angle = -69.0 ; //
                for (Link l : vehInt.getIncomingLinks()) {
                    if (!l.getIfEntry()) {
                        singleLink = l;
                        angle = (singleLink.getAngle() + Math.PI);
                    }
                }
                for (Link l : vehInt.getOutgoingLinks()) {
                    if (!l.getIfEntry()) {
                        singleLink = l;
                        angle = singleLink.getAngle();
                    }
                }
                Location loc1 = vehIntLoc.spawnNewLocation(distanceFromVehLink,  angle + Math.PI / 2);
                PedNode pednode1 = new PedNode(loc1);
                this.pedNodeRoads.put(pednode1, new HashSet<>(Arrays.asList(singleLink)));
                this.pedNodesRoadAngles.put(pednode1, new HashSet<>(Arrays.asList(angle)));

                Location loc2 = vehIntLoc.spawnNewLocation(distanceFromVehLink,  angle - Math.PI / 2);
                PedNode pednode2 = new PedNode(loc2);
                this.pedNodeRoads.put(pednode2, new HashSet<>(Arrays.asList(singleLink)));
                this.pedNodesRoadAngles.put(pednode2, new HashSet<>(Arrays.asList(angle)));

                pedNodes.add(pednode1);
                pedNodes.add(pednode2);

                this.pedNodes.add(pednode1);
                this.pedNodes.add(pednode2);
            }

            else if (num_neighs == 2) {
                // find the 2 links
                Set<Double> two_angles = new HashSet<>();
                Set<Link> two_links = new HashSet<>();
                double tempAngle; //
                for (Link l : vehInt.getIncomingLinks()) {
                    if (!l.getIfEntry()) {
                        tempAngle = (l.getAngle() + Math.PI);
                        two_angles.add(tempAngle);
                        two_links.add(l);
                    }
                }
                for (Link l : vehInt.getOutgoingLinks()) {
                    if (!l.getIfEntry()) {
                        tempAngle = l.getAngle();
                        two_angles.add(tempAngle);
                        two_links.add(l);

                    }
                }
                assert two_angles.size() == 2;
                List<Double> two_angles_l = new ArrayList<>();
                two_angles_l.addAll(two_angles);
                Collections.sort(two_angles_l);
//                System.out.println("Two Links: " + two_angles);
                // get the bisecting angle on both sides (angle1, angle2)
                double angle1 = (two_angles_l.get(1) + two_angles_l.get(0))/2;
                double angle2 = angle1 + Math.PI;
                Location loc1 = vehIntLoc.spawnNewLocation(distanceFromVehLink, angle1);
                PedNode ped1 = new PedNode(loc1);
                Location loc2 = vehIntLoc.spawnNewLocation(distanceFromVehLink, angle2);
                PedNode ped2 = new PedNode(loc2);

                this.pedNodeRoads.put(ped1, new HashSet<Link>(two_links));
                this.pedNodeRoads.put(ped2, new HashSet<Link>(two_links));
                this.pedNodesRoadAngles.put(ped1, two_angles);
                this.pedNodesRoadAngles.put(ped2, two_angles);
                pedNodes.add(ped1);
                pedNodes.add(ped2);
                this.pedNodes.add(ped1);
                this.pedNodes.add(ped2);
            }

            else {
                // CASE: num_neighs == 3,4,5
                // we make vehIntersections since 3 neighbors means 3 roads
                // let's analyze the angles of the vehLinks with relation to vehInt

                double max_angle = maxAngle(vehInt);
                double threshold_angle = thresholdAngle(num_neighs);
                int num_ped_ints;
                // extra pedInt case
                if (max_angle > threshold_angle) {
                    List<PedIntersection> pedIntList = new ArrayList<>();
                    num_ped_ints = num_neighs + 1;
                    Location locTmp;
                    PedIntersection pedIntTmp;

                    PedIntersection ped1;
                    PedIntersection ped2;

                    locTmp = vehIntLoc.spawnPedIntLoc(angles_.get(0), angles_.get(0 + 1),
                            distanceFromVehLink);
                    ped1 = new PedIntersection(locTmp);
                    this.pedNodeRoads.put(ped1,
                            new HashSet<>(Arrays.asList(vehLinks.get(0), vehLinks.get(0 + 1))));

                    this.pedNodesRoadAngles.put(ped1,
                            new HashSet<>(Arrays.asList(angles_.get(0), angles_.get(0 + 1))));
                    pedInts.add(ped1);

                    pedIntList.add(ped1);
                    int i = 0;
                    // spawn locations based on the angles
                    for (i = 1; i < angles_.size() - 1; i++) {
                        locTmp = vehIntLoc.spawnPedIntLoc(angles_.get(i), angles_.get(i + 1),
                                distanceFromVehLink);
                        ped2 = new PedIntersection(locTmp);
                        this.pedNodeRoads.put(ped2,
                                new HashSet<>(Arrays.asList(vehLinks.get(i), vehLinks.get(i + 1))));

                        this.pedNodesRoadAngles.put(ped2,
                                new HashSet<>(Arrays.asList(angles_.get(i), angles_.get(i + 1))));
                        pedInts.add(ped2);
                        pedIntList.add(ped2);


                        // reset the 1 and 2
                        ped1 = ped2;
                        ped2 = null;
                    }
                    // Build the last 2 pedIntersections
                    // the idea is that if you extend the 2 links through the intersection
                    // the angles on both sides are equal, so you can use the 1 links angle with
                    // the opposing link's extension's angle to spawn a new location
                    // TODO: put a drawing explanation in Docs
                    double ang1 = angles_.get(i) + Math.PI;
                    double ang2 = angles_.get(0) + Math.PI;

                    // build ped2
                    locTmp = vehIntLoc.spawnPedIntLoc(angles_.get(i), ang2, distanceFromVehLink);
                    ped2 = new PedIntersection(locTmp);
                    this.pedNodeRoads.put(ped2,
                            new HashSet<>(Arrays.asList( vehLinks.get(i) )));
                    this.pedNodesRoadAngles.put(ped2,
                            new HashSet<>(Arrays.asList(angles_.get(i), angles_.get(0))));
                    pedInts.add(ped2);
                    pedIntList.add(ped2);

                    // build ped1
                    locTmp = vehIntLoc.spawnPedIntLoc(ang1, angles_.get(0), distanceFromVehLink );
                    ped1 = new PedIntersection(locTmp);
                    this.pedNodeRoads.put(ped1,
                            new HashSet<>(Arrays.asList( vehLinks.get(0) )));
                    this.pedNodesRoadAngles.put(ped1,
                            new HashSet<>(Arrays.asList(angles_.get(i), angles_.get(0))));
                    pedInts.add(ped1);
                    pedIntList.add(ped1);


                    // connect these 2 pedInts with a sidewalk
                    double sidewalk_capacity = Double.MAX_VALUE;
                    PedLink sidewalk = new PedLink(ped1, ped2, true, sidewalk_capacity);
                    PedLink sidewalk_ = new PedLink(ped2, ped1, true, sidewalk_capacity);
                    this.linkSet.add(sidewalk);
                    this.linkSet.add(sidewalk_);
                    // System.out.println("\tPed Int list: " + pedIntList);

                    // make crosswalks
                    for (int j = 0; j < pedIntList.size() - 2 ; j++) {
                        PedLink cross1 = new PedLink(pedIntList.get(j), pedIntList.get(j+1), Double.MAX_VALUE);
                        PedLink cross2 = new PedLink(pedIntList.get(j+1), pedIntList.get(j), Double.MAX_VALUE);
                        crosswalks.add(new Crosswalk(cross1, cross2));
                        crosswalk_count += 1;
                        this.linkSet.add(cross1);
                        this.linkSet.add(cross2);
                    }
                    PedLink cross1 = new PedLink(pedIntList.get(0), pedIntList.get(pedIntList.size() - 1), Double.MAX_VALUE);
                    PedLink cross2 = new PedLink(pedIntList.get(pedIntList.size() - 1), pedIntList.get(0),  Double.MAX_VALUE);
                    crosswalks.add(new Crosswalk(cross1, cross2));
                    crosswalk_count += 1;
                    this.linkSet.add(cross1);
                    this.linkSet.add(cross2);

                }
                // standard case
                else {
                    List<PedIntersection> pedIntList = new ArrayList<>();

                    PedIntersection pedIntTmp;
                    num_ped_ints = num_neighs;
                    int i;
                    for (i = 0; i < angles_.size() - 1; i++) {
                        Location locTmp = vehIntLoc.spawnPedIntLoc(angles_.get(i), angles_.get(i+1), distanceFromVehLink);
                        pedIntTmp = new PedIntersection(locTmp);
                        this.pedNodeRoads.put(pedIntTmp,
                                new HashSet<>(Arrays.asList(vehLinks.get(i), vehLinks.get(i+1))));
                        this.pedNodesRoadAngles.put(pedIntTmp,
                                new HashSet<>(Arrays.asList(angles_.get(i), angles_.get(i+1))));
                        pedInts.add(pedIntTmp);
                        pedIntList.add(pedIntTmp);
                    }
                    Location locTmp = vehIntLoc.spawnPedIntLoc(angles_.get(i), angles_.get(0), distanceFromVehLink);
                    pedIntTmp = new PedIntersection(locTmp);
                    this.pedNodeRoads.put(pedIntTmp,
                            new HashSet<>(Arrays.asList(vehLinks.get(i), vehLinks.get(0))));
                    this.pedNodesRoadAngles.put(pedIntTmp,
                            new HashSet<>(Arrays.asList(angles_.get(i), angles_.get(0))));
                    pedInts.add(pedIntTmp);
                    pedIntList.add(pedIntTmp);

                    // make crosswalks
                    for (int j = 0; j < pedIntList.size() - 1 ; j++) {
                        PedLink cross1 = new PedLink(pedIntList.get(j), pedIntList.get(j+1), Double.MAX_VALUE);
                        PedLink cross2 = new PedLink(pedIntList.get(j+1), pedIntList.get(j), Double.MAX_VALUE);
                        crosswalks.add(new Crosswalk(cross1, cross2));
                        crosswalk_count += 1;
                        this.linkSet.add(cross1);
                        this.linkSet.add(cross2);
                    }
                    PedLink cross1 = new PedLink(pedIntList.get(0), pedIntList.get(pedIntList.size() - 1), Double.MAX_VALUE);
                    PedLink cross2 = new PedLink(pedIntList.get(pedIntList.size() - 1), pedIntList.get(0),  Double.MAX_VALUE);
                    crosswalks.add(new Crosswalk(cross1, cross2));
                    crosswalk_count += 1;
                    this.linkSet.add(cross1);
                    this.linkSet.add(cross2);
                }


                pedNodes.addAll(pedInts);
                this.pedNodes.addAll(pedInts);

            }

            
            int colPos = vehInt.getColPosition();
            int rowPos = vehInt.getRowPosition();

//            System.out.println("Crosswalks");
//            for (Crosswalk c : crosswalks) {
//                System.out.println(c);
//            }
            Intersection int_sec = new Intersection(vehInt, pedInts, crosswalks, pedNodes);
            intersectionGrid[rowPos][colPos] = int_sec;
            intersectionSet.add(int_sec);
            // crosswalk_count += crosswalks.size();

        }

        // add entry links for every pedNode
        for (Node ped_i : this.pedNodes) {
            new PedLink(null, (PedNode) ped_i, Double.MAX_VALUE, true);
        }

        this.nodeSet.addAll(this.pedNodes);
//        System.out.println("# of crosswalks: " + crosswalk_count);
    }

    private void loadIntersections_constructor() {
        // iterate over vehInts to make the pedIntersections around each vehInt
        for (Integer key : this.vehInts.keySet()) {
            VehIntersection vehInt = this.vehInts.get(key);
            int colPos = vehInt.getColPosition();
            int rowPos = vehInt.getRowPosition();
            Intersection int_sec = new Intersection(vehInt);
            intersectionGrid[rowPos][colPos] = int_sec;
            intersectionSet.add(int_sec);
        }
    }


//    // TODO: network level calculate conflicts within each intersection
//    private void generateConflicts() {
//        // for each Intersection i, call i.generateConflicts()
//        for (Intersection i : intersectionSet) {
//            i.setVehPedConflicts();
//            i.generateV2Vconflicts();
//        }
//    }

//    public void drawAllNodes() {
//        System.out.println("-------------------------------------------");
//        System.out.println("------------ DRAWING ALL NODES ------------");
//        System.out.println("nodes: " + this.nodeSet);
//        System.out.println("pedNodes: " + this.pedNodes);
//
//        JFrame frame =new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(new G(this.pedNodes, this.linkSet));
//        frame.setSize(400,400);
//        frame.setLocation(200,200);
//        frame.setVisible(true);
//    }

    // helper function
    // function to sort hashmap by values
    public HashMap<Link, Double> sortByValue(HashMap<Link, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Link, Double> > list =
                new LinkedList<Map.Entry<Link, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Link, Double> >() {
            public int compare(Map.Entry<Link, Double> o1,
                               Map.Entry<Link, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Link, Double> temp = new LinkedHashMap<Link, Double>();
        for (Map.Entry<Link, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    private HashMap<Link, Double> getAngles(VehIntersection vehInt) {
        HashMap<Double, Link> angleToLink = new HashMap();
        // this prevents redundant angles if we have two links connected to
        // the same node
        for (Link l : vehInt.getIncomingLinks()) {
            double temp = (l.getAngle()) - Math.PI;
            temp = Angle.bound(temp);
            angleToLink.put(temp, l);
        }
        for (Link l : vehInt.getOutgoingLinks()) {
            double temp = l.getAngle();
            temp = Angle.bound(temp);
            angleToLink.put(temp, l);
        }

        HashMap<Link, Double> linkToAngle = new HashMap<>();
        for(Map.Entry<Double, Link> entry : angleToLink.entrySet()){
            linkToAngle.put(entry.getValue(), entry.getKey());
        }

        // List<Double> angles = new ArrayList<>();
        // angles.addAll(angleToLink.keySet());
        return this.sortByValue(linkToAngle);
    }


    /***
     * Find the maximum angle between the adjacent roads around this
     * vehicle intersection
     * @param vehInt
     * @return
     */
    private double maxAngle(VehIntersection vehInt) {
        HashMap<Double, Link> angleToLink = new HashMap();
        for (Link l : vehInt.getIncomingLinks()) {
            double temp = (l.getAngle()) - Math.PI;
            temp = Angle.bound(temp);
            angleToLink.put(temp  , l);
        }
        for (Link l : vehInt.getOutgoingLinks()) {
            angleToLink.put(l.getAngle(), l);
        }

        List<Double> angles = new ArrayList<>();
        angles.addAll(angleToLink.keySet());
        // System.out.println("angleToLink: " + angleToLink);

        Collections.sort(angles);
        // System.out.println("angles: " + angles);


        List<Double> jointAngles = new ArrayList<>();
        List<Set<Link>> assocLinks = new ArrayList<>();
        double first_angle = (angles.get(0) - angles.get(angles.size()-1));
        first_angle = Angle.bound(first_angle);

        jointAngles.add( first_angle );
        for (int i=1 ; i < angles.size() ; i++) {
            double temp_angle = (angles.get(i) - angles.get(i-1)) ;
            temp_angle = Angle.bound(temp_angle);
            jointAngles.add( temp_angle );
        }
        // System.out.println("jointAngles: " + jointAngles);

        return Collections.max(jointAngles);
    }

    /***
     *
     * @param n the number of roads, not links, directly around a vehicle intersection
     * @return
     */
    private double thresholdAngle(int n) {
        double largest_possible_angle = 2*Math.PI;
        double smallest_largest_angle = (2*Math.PI) / n;
        double minimum_angular_gap = Math.PI / 6;
        double threshold_angle = largest_possible_angle - n*minimum_angular_gap - smallest_largest_angle;
        return threshold_angle;
    }


    // NOTE: The sidewalks only connect to adjacent traffic intersections
    // it doesn't connect sidewalks within a single intersection
    private void loadSidewalks_constructor() {
        // iterate over all the intersections
        // note the vehInt and the neighboring intersections
        // get the angles to neighbors using getAngles(vehInt)
        // iterate over the pedNodes

        assert (this.intersectionSet == intersectionGraph.keySet());

        for (Intersection inter : this.intersectionSet) {
            Set<Intersection> neighs = intersectionGraph.get(inter);
            VehIntersection vehInt = inter.getVehInt();
            Location curLoc = vehInt.getLocation();

            for (PedNode srcPed : inter.getPedNodes()) {
                Location srcPedLoc = srcPed.getLocation();
                Set<Node> validNeighs = getValidNeighs(srcPed, vehInt);

                for (Intersection nei : neighs) {
                    if (!validNeighs.contains(nei.getVehInt())) {
                        continue;
                    }
                    // get neighs pedNodes
                    Set<PedNode> pnodes = nei.getPedNodes();

                    // get angle from vehInt to nei
                    double vehLinkAngle = Location.angle( curLoc, nei.getVehInt().getLocation() );

                    // find nearest neighbor on the angle
                    List<PedNode> potentialDests = new ArrayList<>();
                    for (PedNode destPed : pnodes) {
                        double pedLinkAngle = Location.angle( srcPedLoc, destPed.getLocation() );
                        pedLinkAngle = Angle.bound(pedLinkAngle);
                        if (Angle.closeEnough(pedLinkAngle, vehLinkAngle)) {
                            potentialDests.add(destPed);
                        }
                    }

                    if (potentialDests.size() == 0) {
                        continue;
                    }

                    PedNode nearestDest = potentialDests.get(0);
                    double minDist = srcPedLoc.euclideanDist(nearestDest.getLocation());
                    double tmpDist;
                    for (int i = 1; i < potentialDests.size() ; i++) {
                        PedNode tmpDest = potentialDests.get(i);
                        tmpDist = srcPedLoc.euclideanDist(tmpDest.getLocation());
                        if (tmpDist < minDist) {
                            minDist = tmpDist;
                            nearestDest = tmpDest;
                        }
                    }
                    double sidewalkCapacity = Double.MAX_VALUE;
                    PedLink side_walk = new PedLink(srcPed, nearestDest, true, sidewalkCapacity);
                    this.linkSet.add(side_walk);
                }
            }
        }

    }

    private Set<Node> getValidNeighs(PedNode srcPed, VehIntersection n) {
        Set<Link> validLinks = this.pedNodeRoads.get(srcPed);
        Set<Node> validNeighs = new HashSet<>();
        for (Link l : validLinks) {
            Node n1 = l.getStart();
            Node n2 = l.getDestination();
            validNeighs.add(n1);
            validNeighs.add(n2);
        }
        validNeighs.remove(n);
        return validNeighs;
    }

    /** SETTERS AND GETTERS **/

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

    /*
        input:
            od file, (origin, destination, demand)
            assigned traffic ()
     */
    public void calculateTurningProportions() {
        /*
        Big pi = all the paths : List<Path>
        Little pi = a single path : Path
        (i,j) in pi = a single link from a path : Link
        path proportions alpha_pi : Map<Path, Double>
         */
        /*
        r = origin_node;
        s = dest_node;
        for (Node n : getAllVehLinks ) {
            Set<Node> neigh = n.getOutgoingNodes();

        }
         */

    }

}
