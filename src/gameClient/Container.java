package gameClient;

import api.dw_graph_algorithms;

/**
 * this class represents an object containing a pokemon,agent and the distant of the agent from the pokemon
 */
public class Container implements  Comparable<Container>{
    private CL_Pokemon pok;
    private CL_Agent agent;
    private double dist;
    private dw_graph_algorithms algo;


    public Container(CL_Agent agn, CL_Pokemon pok, dw_graph_algorithms algo) {
        this.agent = agn;
        this.pok = pok;
        this.algo = algo;
        this.dist = algo.shortestPathDist(agn.getSrcNode(), pok.get_edge().getSrc());
        if(dist==-1)
            dist=Integer.MAX_VALUE;
    }

    public double getDist() {
        return dist;
    }

    public CL_Pokemon getPok() {
        return pok;
    }

    public CL_Agent getAgent() {
        return agent;
    }

    @Override
    public int compareTo(Container o) {
        return Double.compare(this.getDist(), o.getDist());
    }
}


