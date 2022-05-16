package ped;

import util.Angle;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Network {
    private Set<Intersection> intersectionSet;
    private Set<Link> linkSet;
    private Set<Node> nodeSet;
    private Intersection[][] intersectionGrid;

    private HashMap<Intersection, Set<Intersection>> intersectionGraph;
    private HashMap<Integer, VehIntersection> vehInts;
    private HashMap<Integer, Link> entryLinks;
    private Set<PedNode> pedNodes;
    private Set<VehNode> vehNodes;

    // the doubles are the angles w.r.t to the vehInt corresponding to the PedNode
    private HashMap<PedNode, Set<Double>> pedNodesRoadAngles;
    private HashMap<PedNode, Set<Link>> pedNodeRoads;

    private Map<Node, Map<Node, List<ArrayList<Node>>>> vehiclePaths;
    private Set<Vehicle> vehicles;

    private String controllerType;

    public double networkTime; // total network time (time in simulation)
    public double cycleTime; // time within a cycle of the simulation
    public double toleranceTime; // pedestrian tolerance time
    private boolean ped; // says whether we load the network with pedestrians




    public Network(File nodesFile, File linksFile, boolean ped, String controllerType) {
        this.vehicles = new HashSet<>();
        this.vehiclePaths = new HashMap<>();
        this.controllerType = controllerType;
//        this.turningMovements = new HashMap<>();
        this.pedNodeRoads = new HashMap<>();
        this.pedNodesRoadAngles = new HashMap<>();
        this.intersectionSet = new HashSet<>();
        this.linkSet = new HashSet<>();
        this.nodeSet = new HashSet<>();
        this.vehInts = new HashMap<>();
        this.entryLinks = new HashMap<>();
        this.intersectionGraph = new HashMap<>();
        this.pedNodes = new HashSet<>();
        this.ped = ped;
        this.vehNodes = new HashSet<>();

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
            loadPedIntersections_constructor(controllerType);
        }
        else {
            loadIntersections_constructor(controllerType);
        }

        // create intersection graph
        this.createIntersectionGraph_constructor();

        if (ped) {
            this.loadSidewalks_constructor();
        }

        setVehiclePaths();

//        printVehiclePaths();

        this.finishLoading();
    }

    /** CONSTRUCTOR HELPERS **/

    public void finishLoading() {
        for (Intersection i : this.getIntersectionSet()) {
            i.finishLoading();
        }
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

            double max_long = Double.NEGATIVE_INFINITY;
            double max_lat = Double.NEGATIVE_INFINITY;
            double min_long = Double.POSITIVE_INFINITY;
            double min_lat = Double.POSITIVE_INFINITY;
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
                // EntryLink(int id, Node source, Node dest)

                // we use a default entry link for now,
                // TODO: maybe we update this to be same id as node???
                int default_entry_link_id = -999;
                Link entry_link = new EntryLink(default_entry_link_id, node);


                assert nodeId == node.getId();
                assert nodeId == entry_link.getDest().getId();

                node.addEntryLink(entry_link);
                // this.linkSet.add(entry_link);
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

                vehNodes.add(node);
                this.nodeSet.add(node);

//                this.grid[rowPos][colPos] = nodeId;
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
                int id = Integer.parseInt(link_data[0]);
                String type = link_data[1];
                int srcId = Integer.parseInt(link_data[2]);
                int destId = Integer.parseInt(link_data[3]);

                double length = Double.parseDouble(link_data[4]);
                double ffspd = Double.parseDouble(link_data[5]);

                int capacityPerLane = Integer.parseInt(link_data[6]);

                int numLanes = Integer.parseInt(link_data[7]);


                startNode = vehInts.get(srcId);
                endNode = vehInts.get(destId);
                // vehicleGraph.get(startNode).add((VehIntersection) endNode);T
                Location src = startNode.getLocation();
                Location dest = endNode.getLocation();


                // TODO: do something about the direction code
                String direction = null;
                double angle = Location.angle(src, dest);

                // Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
                Link link = new PointQueue(id, startNode, endNode, length, ffspd, capacityPerLane, numLanes);
                // TODO: add angle of link

                assert link.getSource() == startNode;
                assert link.getDest() == endNode;

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
                Node outVehInt = outVehLink.getDest();
                assert vehInt == outVehLink.getSource();
//                int id_ = vehInt_to_id.get(outVehInt);
//                System.out.println(id_);

                int rowPos = outVehInt.getRowPosition();
                int colPos = outVehInt.getColPosition();
                Intersection int_ =  intersectionGrid[rowPos][colPos];
                if (int_ != null) {
                    neighbors.add(int_);
                }
            }

            for (Link inVehLink : vehInt.getIncomingLinks()) {
//                System.out.println(outVehLink);
                Node inVehInt = inVehLink.getSource();
                assert vehInt == inVehLink.getDest();
//                int id_ = vehInt_to_id.get(outVehInt);
//                System.out.println(id_);
                int rowPos = inVehInt.getRowPosition();
                int colPos = inVehInt.getColPosition();
                Intersection int_ =  intersectionGrid[rowPos][colPos];
                if (int_ != null) {
                    neighbors.add(int_);
                }
            }
            // System.out.println(neighbors);
            intersectionGraph.put(intersection_, neighbors);
        }
        // print out all the neighbors
        // System.out.println(intersectionGraph.values());

    }

    private void loadPedIntersections_constructor(String controllerType) {
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
            angles_.addAll(vehLinkAngles.values());
            vehLinks.addAll(vehLinkAngles.keySet());

            // get the neighbors
            Set<Node> neighs = new HashSet<>();
            for (Link l : vehInt.getIncomingLinks()) {
                neighs.add( l.getSource() );
            }
            for (Link l : vehInt.getOutgoingLinks()) {
                neighs.add( l.getDest() );
            }
            int num_forks = neighs.size();

            // based on the num_forks (forks),
            // determine the pedNode locations
            // if we only have 1 neighboring intersection
            // no pedInts are needed but we want some pedNodes to allow sidewalk connections
            if (num_forks == 1) {
                // find the single link and get the link's angle
                Link singleLink = null;
                double angle = -69.0 ; //
                for (Link l : vehInt.getIncomingLinks()) {
                    if (!l.isEntry()) {
                        singleLink = l;
                        angle = (singleLink.getAngle() + Math.PI);
                    }
                }
                for (Link l : vehInt.getOutgoingLinks()) {
                    if (!l.isEntry()) {
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

            else if (num_forks == 2) {
                // find the 2 links
                Set<Double> two_angles = new HashSet<>();
                Set<Link> two_links = new HashSet<>();
                double tempAngle; //
                for (Link l : vehInt.getIncomingLinks()) {
                    if (!l.isEntry()) {
                        tempAngle = (l.getAngle() + Math.PI);
                        two_angles.add(tempAngle);
                        two_links.add(l);
                    }
                }
                for (Link l : vehInt.getOutgoingLinks()) {
                    if (!l.isEntry()) {
                        tempAngle = l.getAngle();
                        two_angles.add(tempAngle);
                        two_links.add(l);

                    }
                }
                assert two_angles.size() == 2;
                List<Double> two_angles_l = new ArrayList<>(two_angles);
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
                // CASE: num_forks == 3,4,5
                // we make vehIntersections since 3 neighbors means 3 roads
                // let's analyze the angles of the vehLinks with relation to vehInt

                double max_angle = maxAngle(vehInt);
                double threshold_angle = thresholdAngle(num_forks);
                int num_ped_ints;
                // extra pedInt case
                if (max_angle > threshold_angle) {
                    List<PedIntersection> pedIntList = new ArrayList<>();
                    num_ped_ints = num_forks + 1;
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
                    int i;
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
//                    PedLink sidewalk = new PedLink(ped1, ped2, true, sidewalk_capacity);
//                    PedLink sidewalk_ = new PedLink(ped2, ped1, true, sidewalk_capacity);

                    // Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
                    // TODO: write an id generator for sidewalks
                    int id = -9;
                    // TODO: write a length function
                    double length = 0;
                    double ffspd = 0;
                    int capacityPerLane = 0;
                    int numLanes = 0;
                    Link sidewalk = new PointQueue(id, ped1, ped2, length, ffspd, capacityPerLane, numLanes);
                    Link sidewalk_ = new PointQueue(id, ped2, ped1, length, ffspd, capacityPerLane, numLanes);
                    // TODO: tell the code this is a sidewalk

                    this.linkSet.add(sidewalk);
                    this.linkSet.add(sidewalk_);
                    // System.out.println("\tPed Int list: " + pedIntList);

                    // make crosswalks
                    for (int j = 0; j < pedIntList.size() - 2 ; j++) {
                        // Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
                        // TODO: update the parameters id, length, ffspd, ..., correctly
                        id = 0;
                        length = 0;
                        ffspd = 0;
                        capacityPerLane = 0;
                        numLanes = 0;

                        Link cross1 = new PointQueue(id, pedIntList.get(j), pedIntList.get(j+1), length, ffspd, capacityPerLane, numLanes);
                        Link cross2 = new PointQueue(id, pedIntList.get(j+1), pedIntList.get(j), length, ffspd, capacityPerLane, numLanes);
                        crosswalks.add(new Crosswalk(cross1, cross2));
                        crosswalk_count += 1;
                        this.linkSet.add(cross1);
                        this.linkSet.add(cross2);
                    }
                    Link cross1 = new PointQueue(id, pedIntList.get(0), pedIntList.get(pedIntList.size() - 1), length, ffspd, capacityPerLane, numLanes);
                    Link cross2 = new PointQueue(id, pedIntList.get(pedIntList.size() - 1), pedIntList.get(0), length, ffspd, capacityPerLane, numLanes);
                    crosswalks.add(new Crosswalk(cross1, cross2));
                    crosswalk_count += 1;
                    this.linkSet.add(cross1);
                    this.linkSet.add(cross2);

                }
                // standard case
                else {
                    List<PedIntersection> pedIntList = new ArrayList<>();

                    PedIntersection pedIntTmp;
                    num_ped_ints = num_forks;
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
                        // TODO: change parameters
                        int id = 0;
                        int length = 0;
                        int ffspd = 0;
                        int capacityPerLane = 0;
                        int numLanes = 0;
                        Link cross1 = new PointQueue(id, pedIntList.get(j), pedIntList.get(j+1), length, ffspd, capacityPerLane, numLanes);
                        Link cross2 = new PointQueue(id, pedIntList.get(j+1), pedIntList.get(j), length, ffspd, capacityPerLane, numLanes);

                        crosswalks.add(new Crosswalk(cross1, cross2));
                        crosswalk_count += 1;
                        this.linkSet.add(cross1);
                        this.linkSet.add(cross2);
                    }
                    // TODO: change parameters
                    int id = 0;
                    int length = 0;
                    int ffspd = 0;
                    int capacityPerLane = 0;
                    int numLanes = 0;
                    Link cross1 = new PointQueue(id, pedIntList.get(0), pedIntList.get(pedIntList.size() - 1), length, ffspd, capacityPerLane, numLanes);
                    Link cross2 = new PointQueue(id, pedIntList.get(pedIntList.size() - 1), pedIntList.get(0), length, ffspd, capacityPerLane, numLanes);
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
            Intersection int_sec = new Intersection(vehInt, pedInts, crosswalks, pedNodes, controllerType);
            intersectionGrid[rowPos][colPos] = int_sec;
            intersectionSet.add(int_sec);
            // crosswalk_count += crosswalks.size();

        }

        // add entry links for every pedNode
        for (Node ped_i : this.pedNodes) {
            // TODO: set entry link id
            // Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes
            int id = 0;
            new EntryLink(id, ped_i);
        }

        this.nodeSet.addAll(this.pedNodes);
//        System.out.println("# of crosswalks: " + crosswalk_count);
    }

    private void loadIntersections_constructor(String controllerType) {
        // iterate over vehInts to make the pedIntersections around each vehInt
        for (Integer key : this.vehInts.keySet()) {
            VehIntersection vehInt = this.vehInts.get(key);
            int colPos = vehInt.getColPosition();
            int rowPos = vehInt.getRowPosition();
            Intersection int_sec = new Intersection(vehInt, controllerType);
            intersectionGrid[rowPos][colPos] = int_sec;
            intersectionSet.add(int_sec);
        }
    }



    public void setVehiclePaths() {
        double pathCount = 0;
        double pathLength = 0;
        vehiclePaths = new HashMap<>();
        for (Node source : vehNodes) {
            vehiclePaths.put(source, new HashMap<>());
            for (VehNode dest: vehNodes) {
//                //don't want to calculate paths for centroids that are adjacent to each other.
//                if (Math.abs(dest.getRowPosition() - source.getRowPosition()) < 2 && Math.abs(dest.getColPosition() - source.getColPosition()) < 2) {
//                    continue;
//                }
                if (source == dest) {
                    continue;
                }

                HashSet<ArrayList<Node>> paths = getAllShortestVehPaths(source, dest);
                if (paths == null) {
                    continue;
                }

                vehiclePaths.get(source).put(dest, new ArrayList<>());

                for (ArrayList<Node> path : paths) {

                    pathLength += path.size();
                    pathCount++;
                    vehiclePaths.get(source).get(dest).add(path);
                }
            }
        }
//        System.out.println("total paths: " + pathCount);
//        System.out.println("average vehpath length: " + pathLength/pathCount);
    }

    //returns a set of all possible shortest paths between source and dest
    public HashSet<ArrayList<Node>> getAllShortestVehPaths(Node source, Node dest) {
        //runs modified BFS to set all node's parents
        modifiedBFS(source, dest);

        // if dest has no parents, there is no path
        if (dest.getParents().size() == 0) {
            return null;
        }

        HashSet<ArrayList<Node>> shortestPaths = new HashSet<>();

        //one stack stores nodes, one stack stores paths
        Stack<Node> nodeStack = new Stack<>();
        Stack<ArrayList<Node>> pathStack = new Stack<>();

        pathStack.push(new ArrayList<>());
        pathStack.peek().add(dest);
        nodeStack.push(dest);

        while (!nodeStack.empty()) {
            Node current = nodeStack.pop();
            ArrayList<Node> path = pathStack.peek();

            if (current == source) {
                shortestPaths.add(pathStack.pop());
            } else {
                //if we are at a split, then pop the current path and push clones on with their respective parent nodes.
                if (current.getParents().size() > 1) {
                    path = pathStack.pop();
                    for (Node n : current.getParents()) {
                        //copies list, but doesn't copy the objects in the list - pointers remain the same.
                        ArrayList<Node> clonedPath = (ArrayList<Node>) path.clone();
                        clonedPath.add( n);

                        nodeStack.push( n);
                        pathStack.push(clonedPath);
                    }
                } else {
//                    System.out.println("current.getParents(): " + current.getParents());
//                    System.out.println("current.getId(): " + current.getId());
                    Node parent = current.getParents().get(0);
                    path.add(parent);
                    nodeStack.push(parent);
                }
            }
        }

        for (ArrayList<Node> path : shortestPaths) {
            Collections.reverse(path);
        }

        return shortestPaths;
    }



    //BFS algorithm used to find multiple shortest paths - each node has an array of parents with the same cost as each other
    public void modifiedBFS(Node source, Node dest) {
        for (Node n : vehNodes) {
            n.setCost(Double.POSITIVE_INFINITY);
            n.setExplored(false);
            n.setParents(new ArrayList<>());
        }

        source.setCost(0);
        source.getParents().add(source);

        LinkedList<Node> queue = new LinkedList<>();
        queue.add(source);

        while (queue.size() > 0) {
            Node current = queue.remove();
            if (current == dest) {
                break;
            }

            HashSet<Node> nextNodes = new HashSet<>();
            for (Link l : current.getOutgoingLinks()) {
                nextNodes.add(l.getDest());
            }

            for (Node next : nextNodes) {
                if (!next.isExplored()) {
                    next.setCost(current.getCost() + 1);
                    next.getParents().add(current);
                    next.setExplored(true);
                    queue.add(next);
                } else if (next.getCost() == current.getCost() + 1) {
                    next.getParents().add(current);
                }
            }

        }
    }


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
        HashMap angleToLink = new HashMap();
        for (Link l : vehInt.getIncomingLinks()) {
            double temp = (l.getAngle()) - Math.PI;
            temp = Angle.bound(temp);
            angleToLink.put(temp  , l);
        }
        for (Link l : vehInt.getOutgoingLinks()) {
            angleToLink.put(l.getAngle(), l);
        }

        List<Double> angles = new ArrayList<>(angleToLink.keySet());
        // System.out.println("angleToLink: " + angleToLink);

        Collections.sort(angles);
        // System.out.println("angles: " + angles);

        List<Double> jointAngles = new ArrayList<>();
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
     * @param n the number of roads, not links, directly around a vehicle intersection
     * @return
     */
    private double thresholdAngle(int n) {
        double largest_possible_angle = 2*Math.PI;
        double smallest_largest_angle = (2*Math.PI) / n;
        double minimum_angular_gap = Math.PI / 6;
        return largest_possible_angle - n*minimum_angular_gap - smallest_largest_angle;
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
                    //  Link(int id, Node source, Node dest, double length, double ffspd, double capacityPerLane, int numLanes)
                    // TODO: make correct parameters
                    int id = 0;
                    double length = 0;
                    double ffspd = 0;
                    int capacityPerLane = Integer.MAX_VALUE;
                    int numLanes = 0;
                    Link side_walk = new PointQueue(id, srcPed, nearestDest, length, ffspd, capacityPerLane, numLanes);
                    this.linkSet.add(side_walk);
                }
            }
        }

    }

    private Set<Node> getValidNeighs(PedNode srcPed, VehIntersection n) {
        Set<Link> validLinks = this.pedNodeRoads.get(srcPed);
        Set<Node> validNeighs = new HashSet<>();
        for (Link l : validLinks) {
            Node n1 = l.getSource();
            Node n2 = l.getDest();
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

    public Set<Node> getNodeSet() {
        return nodeSet;
    }

    public Set<Link> getLinkSet() {
        return this.linkSet;
    }

    public HashMap<Integer, VehIntersection> getNodes() {
        return vehInts;
    }



    public void createVehicle(Node origin, Node dest) {
        // make round(n) vehicles that have a path to dest
//        System.out.println("Origin: "+ origin);
//        System.out.println("Dest: "+ dest);
        List<ArrayList<Node>> paths = vehiclePaths.get(origin).get(dest);
        // if no path exists, we do nothing
        if (paths != null) {
            // printVehiclePaths();
            // select a random path
            Random ran = new Random(123);
            List<Node> random_path = paths.get(ran.nextInt(paths.size()));
            Vehicle v = new Vehicle(random_path);
            vehicles.add(v);

            Node next_step = v.getNextNode(1);
//            assert nodeSet.contains(next_step);
            Link l = origin.getOutgoingLink(next_step);
//            assert linkSet.contains(l);
//            int prev_size = l.getVehs().size();
//            System.out.println("\tlink: " + l);
            l.addVehicle(v);
//            int updated_size = l.getVehs().size();
//            System.out.println("\tupdated: " + l.getVehs());
//            assert updated_size == prev_size + 1;
        }
    }


//    public void addDemandToNodes(Map<Node, double[]> static_demand, double simTime, double timeStepSize) {
//        // set demand values for each node by adding
//        // peds/vehicles into the entry links
//
//        for (Node n : static_demand.keySet()) {
//            double[] start_time_end_time_rate = static_demand.get(n);
//            double start_time = start_time_end_time_rate[0];
//            double end_time = start_time_end_time_rate[1];
//            if (start_time <= simTime && simTime <= end_time) {
//                double rate = start_time_end_time_rate[2]; // in vehicles / hour
//                // convert to vehicles / timeStepSize (in seconds)
//                rate = rate / 60 / 60 * timeStepSize;
//                // TODO:  demand into the node
//                n.addDemand(rate);
//
//                // TODO: split demand in node into each turning movement???
//                // a turning movement is a pair of links, which is equivalent to 3 nodes
//                // the middle node is the same as 'n'.
//            }
//        }
//    }

//    // TODO: split demand to turns
//    public void splitDemandToLinks() {
//        for (Intersection i : getIntersectionSet()) {
//            i.getAllTurningMovements();
//            Set<Node> allNodes = i.getAllNodes();
////            for (TurningMovement tm : i.getAllTurningMovements()) {
////                System.out.println(tm.get);
////            }
//        }
//    }

//    public void printNodeDemands() {
//        for (Node n : nodeSet) {
//            System.out.println(n.getId() + ": " + n.getCurDemand());
//        }
//    }

    /**
     * method: runSimulation(int time_steps)
     * purpose: run through a simulation with over 'time_steps' number of discrete time steps
     * parameters:
     ** /

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
     ** /
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

    // Map<Integer, Map<Integer, List<ArrayList<Integer>>>> vehiclePaths
//    public void printVehiclePaths() {
//        System.out.println("src_id\tdest_id\tpath");
//        for (Integer src_id : vehiclePaths.keySet()) {
//            for (Integer dest_id : vehiclePaths.get(src_id).keySet()) {
//                for (ArrayList<Integer> path : vehiclePaths.get(src_id).get(dest_id)) {
//                    String out_string = src_id + "\t" + dest_id + "\t" + path.toString();
//                    System.out.println(out_string);
//                }
//            }
//        }
//    }

    public Node getNode(Integer id) {
        for (Node n : getNodeSet()) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }


}
