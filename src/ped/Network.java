/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ped;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author micha
 */
public class Network 
{
    private Node[] nodes;
    private Link[] links;
    private Zone[] zones;

    

    public Network(Node[] nodes, Link[] links)
    {
        this.nodes = nodes;
        this.links = links;
        this.zones = zones;

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
            double t_ff = filein.nextDouble();
            double alpha = filein.nextDouble();
            double beta = filein.nextDouble();
            
            filein.nextLine();
            
            links[i] = new Link(start, end, t_ff, C);
            
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
    
    
    // public void dijkstras(Node r)
    // {
    //     /* **********
    //     Exercise 6(b)
    //     ********** */
        
    //     for(Node n : nodes)
    //     {
    //         n.cost = Integer.MAX_VALUE;
    //         n.predecessor = null;
    //     }
        
    //     r.cost = 0;
        
    //     HashSet<Node> Q = new HashSet<>();
    //     Q.add(r);
        
    //     /* **********
    //     Exercise 6(c)
    //     ********** */
        
    //     while(!Q.isEmpty())
    //     {
    //         Node u = null;
    //         double mincost = Double.MAX_VALUE;
            
    //         for(Node n : Q)
    //         {
    //             if(n.cost < mincost)
    //             {
    //                 mincost = n.cost;
    //                 u = n;
    //             }
    //         }
            
    //         Q.remove(u);
            
    //         for(Link l : u.getOutgoing())
    //         {
    //             Node v = l.getEnd();
                
    //             if(u.cost + l.getTravelTime() < v.cost)
    //             {
    //                 v.cost = u.cost + l.getTravelTime();
    //                 v.predecessor = u;
                    
    //                 if(v.isThruNode())
    //                 {
    //                     Q.add(v);
    //                 }
    //             }
    //         }
    //     }
    // }
    
    
    /* **********
    Exercise 6(d)
    ********** */
    
    public Path trace(Node r, Node s)
    {
        Node curr = s;
        
        Path output = new Path();
        
        while(curr != r && curr != null)
        {
            Link ij = findLink(curr.predecessor, curr);
            
            if(ij != null)
            {
                output.add(0, ij);
            }
            curr = curr.predecessor;
        }
        
        return output;
    }
    
    
    /* **********
    Exercise 7
    ********** */
    public double getTSTT()
    {
        double output = 0;
        
        for(Link l : links)
        {
            output += l.getFlow() * l.getTravelTime();
        }
        
        return output;
    }
    
    
    // public double getSPTT()
    // {
    //     double output = 0;
        
    //     for(Zone r : zones)
    //     {
    //         dijkstras(r);
            
    //         for(Zone s : zones)
    //         {
    //             output += r.getDemand(s) * s.cost;
    //         }
    //     }
        
    //     return output;
    // }
    
    public double getTotalTrips()
    {
        double output = 0;
        
        for(Zone r : zones)
        {
            output += r.getProductions();
        }
        
        return output;
    }

    // public double getAEC()
    // {
    //     return (getTSTT() - getSPTT()) / getTotalTrips();
    // }
    


    /* **********
    Exercise 8(a)
    ********** */
    public double calculateStepsize(int iteration)
    {
        return 1.0/iteration;
    }
    
    
    /* **********
    Exercise 8(b)
    ********** */
    public void calculateNewX(double stepsize)
    {
        for(Link l : links)
        {
            l.calculateNewX(stepsize);
        }
    }
    
    
    // /* **********
    // Exercise 8(c)
    // ********** */
    // public void calculateAON()
    // {
    //     for(Zone r : zones)
    //     {
    //         dijkstras(r);
            
    //         for(Zone s : zones)
    //         {
    //             Path pi_star = trace(r, s);
                
    //             pi_star.addHstar(r.getDemand(s));
    //         }
    //     }
    // }
    
    
    /* **********
    Exercise 8(d)
    ********** */
    // public void msa(int max_iteration)
    // {
    //     System.out.println("Iteration\tAEC");
        
        
    //     for(int iteration = 1; iteration <= max_iteration; iteration++)
    //     {
    //         calculateAON();
            
    //         double lambda = calculateStepsize(iteration);
            
    //         calculateNewX(lambda);
            
    //         System.out.println(iteration+"\t"+getAEC());
    //     }
    // }
    
    
    
    
    
    
}
