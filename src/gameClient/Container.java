package gameClient;

import api.dw_graph_algorithms;

import java.util.Comparator;

public class Container{
    private CL_Pokemon pok;
    private CL_Agent agent;
    private double dist;
    private dw_graph_algorithms algo;


    public Container(CL_Agent agn, CL_Pokemon pok, dw_graph_algorithms algo) {
        this.agent = agn;
        this.pok = pok;
        this.algo = algo;
        this.dist = algo.shortestPathDist(agn.getSrcNode(), pok.get_edge().getSrc());
        //this.dist ;
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

}


