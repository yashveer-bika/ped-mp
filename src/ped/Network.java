/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.io.File;
import java.io.IOException;
// import java.util.*;
import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;



// TODO: USE Set<...> instead of ...[]

/**
 *
 * @author micha
 */
public class Network 
{
    private VehNode[] vehicleNodes;
    private PedNode[] pedestrianNodes;
    private Node[] nodes;
    private Link[] links;
    private Zone[] zones;
    public HashMap<String, Integer> allSignals; // Integer 1 = green, Integer 0 = red
    public double T = 0;
    public static double timeStep = 15;
    // public Set<VehNode> vehNodes = new HashSet<VehNode>();
    // public Set<PedNode> pedNodes = new HashSet<>();
    public List<Intersection> intersections = new ArrayList<>();
    // public Set<PedTurnNode> pedTurnNodes = new HashSet<>(); // Q: what's the point of this??
    public PedNode[][] pedNodeGrid;
    public VehNode[][] vehNodeGrid;

    public Network() {

    }

    public Network(Node[] nodes, Link[] links)
    {
        this.nodes = nodes;
        this.links = links;
        this.zones = zones;

        this.pedNodeGrid = new PedNode[][]{{}, {}};
        this.vehNodeGrid = new VehNode[][]{{}, {}};


        // initialize signals for each node
        for (Node node : this.nodes) {
            for (Link incoming_link : node.getIncoming()) {
                for (Link outgoing_link : node.getOutgoing()) {
                    // Link[] pair = {incoming_link, outgoing_link};
                    String pair = incoming_link.toString() + "::" + outgoing_link.toString();
                    node.signals.put(pair, 0);
                     // System.out.println( node.signals.get(pair) );
                }
            }
        }



    }


    public void printSignal() {
        for (Node node : this.nodes) {
            for (Link incoming_link : node.getIncoming()) {
                for (Link outgoing_link : node.getOutgoing()) {
                    String pair = incoming_link.toString() + "::" + outgoing_link.toString();
                    // node.signals.put(pair, 0);
                    System.out.println( node.signals.get(pair) );
                }
            }
        }
    }

    public void setSignal(Link incoming, Link outgoing, int new_signal) {
        if (incoming.getEnd().getId() == outgoing.getStart().getId()) {
            Node node = incoming.getEnd();
            String key = incoming.toString() + "::" + outgoing.toString();
            node.signals.put(key, new_signal);
        } else {
            // TODO: throw error
        }

    }

    public Link[] getLinks()
    {
        return links;
    }
    
    public Node[] getNodes()
    {
        return nodes;
    }
    
    public Zone[] getZones()
    {
        return zones;
    }

    public HashMap<String, Integer> getAllSignals() {
        return allSignals;
    }

    public String showFlow() {
        return "Network{" +
                "links=" + Arrays.toString(links) +
                '}';
    }

    public void readNetwork(File netFile) throws IOException
    {
        /* **********
        Exercise 5(b)
        ********** */
        
        
        /* **********
        Exercise 5(c)
        ********** */
        
        
        
        /* **********
        Exercise 5(d)
        ********** */
        
        int firstThruNode = 1;
        
        
        Scanner filein = new Scanner(netFile);
        
        String line;
        
        do
        {
            line = filein.nextLine();
            
            if(line.indexOf("<NUMBER OF ZONES>") >= 0)
            {
                zones = new Zone[Integer.parseInt(line.substring(line.indexOf('>')+1).trim())];
            }
            else if(line.indexOf("<NUMBER OF NODES>") >= 0)
            {
                nodes = new Node[Integer.parseInt(line.substring(line.indexOf('>')+1).trim())];
            }
            else if(line.indexOf("<NUMBER OF LINKS>") >= 0)
            {
                links = new Link[Integer.parseInt(line.substring(line.indexOf('>')+1).trim())];
            }
            else if(line.indexOf("<FIRST THRU NODE>") >= 0)
            {
                firstThruNode = Integer.parseInt(line.substring(line.indexOf('>')+1).trim());
            }
        }
        while(!line.trim().equals("<END OF METADATA>"));
        
        
        for(int i = 0; i < zones.length; i++)
        {
            zones[i] = new Zone(i+1);
        }
        
        for(int i = 0; i < nodes.length; i++)
        {
            if(i < zones.length)
            {
                nodes[i] = zones[i];
                
                if(i+1 < firstThruNode)
                {
                    zones[i].setThruNode(false);
                }
            }
            else
            {
                nodes[i] = new Node(i+1);
            }
        }
        
        
        while(!filein.hasNextInt())
        {
            filein.nextLine();
        }
        
        
        for(int i = 0; i < links.length; i++)
        {
            Node start = nodes[filein.nextInt()-1];
            Node end = nodes[filein.nextInt()-1];
            double C = filein.nextDouble();
            filein.nextDouble();
            double alpha = filein.nextDouble();
            double beta = filein.nextDouble();
            
            filein.nextLine();
            
            links[i] = new Link(start, end, C);
            
        }
        
        
        filein.close();
        
        
        
        
    }
    
    public void readTrips(File tripsFile) throws IOException
    {

        
        
        /* **********
        Exercise 5(d)
        ********** */
        
        
        
        Scanner filein = new Scanner(tripsFile);
        String line;
        
        do
        {
            line = filein.nextLine();
        }
        while(!line.trim().equals("<END OF METADATA>"));
        
        Zone r = null;
        
        while(filein.hasNext())
        {
            if(filein.hasNextInt())
            {
                Zone s = zones[filein.nextInt()-1];
                filein.next();
                String next = filein.next();
                double d = Double.parseDouble(next.substring(0, next.length()-1));
                
                r.addDemand(s, d);
            }
            else if(filein.next().equals("Origin"))
            {
                r = zones[filein.nextInt()-1];
            }
            
            
        }
        
        filein.close();
        
    }
    
    /* **********
    Exercise 5(e)
    ********** */
    
    public Node findNode(int id)
    {
        if(id <= 0 || id > nodes.length)
        {
            return null;
            
        }
        return nodes[id-1];
    }
    
    public Link findLink(Node i, Node j)
    {
        if(i == null)
        {
            return null;
        }
        
        for(Link l : i.getOutgoing())
        {
            if(l.getEnd() == j)
            {
                return l;
            }
        }
        
        return null;
    }

}
