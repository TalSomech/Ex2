package api;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {
    private static DWGraph_DS init(int nON, int nOE) {
        Random _rnd = new Random(1);
        int rnd;
        int rnd2;
        DWGraph_DS g = new DWGraph_DS();
        for (int i = 0; i < nON; i++) {
            g.addNode(new NodeData(i));
        }
        while (g.edgeSize() < nOE) {
            rnd = Math.abs((_rnd.nextInt()) % nON);
            rnd2 = Math.abs((_rnd.nextInt()) % nON);
            g.connect(rnd, rnd2, _rnd.nextDouble() * 10);
        }
        return g;
    }

    @Test
    void getGraph() {
        DWGraph_DS g= init(10,20);
        DWGraph_Algo g1= new DWGraph_Algo(g);
        g.removeNode(1);
        assertNull(g1.getGraph().getNode(1));
        g.addNode(new NodeData(91));
        assertNotNull(g1.getGraph().getNode(91));
    }

    @Test
    void copy() {
        DWGraph_DS g1= init(10,20);
        DWGraph_Algo g= new DWGraph_Algo(g1);
        DWGraph_DS h =(DWGraph_DS) g.copy();
        DWGraph_Algo H=new DWGraph_Algo(h);
        //check if the the nodes has been copied by reference or by value
        assertFalse(g1.getNode(5)==h.getNode(5));
        assertFalse(g1.getNode(9)==h.getNode(9));
        assertFalse(g1.getNode(4)==h.getNode(4));
        // this function is checking deep copy by implementing equals in each class
        assertTrue(H.equals(g));
        H.getGraph().removeNode(5);
        assertNotNull(g1.getNode(5));
        H.getGraph().addNode(new NodeData(12));
        assertNull(g1.getNode(12));
        H.getGraph().removeEdge(1,6);
        H.getGraph().connect(4,0,6);
        g1.removeNode(9);
        assertNotNull(H.getGraph().getNode(9));
        g1.addNode(new NodeData(54));
        assertNull(H.getGraph().getNode(54));
        g1.removeEdge(3,2);
        g1.connect(4,9,6);
    }

    @Test
    void isConnected() {
        DWGraph_DS g1= init(10,20);
        DWGraph_Algo g= new DWGraph_Algo(g1);
        assertFalse(g.isConnected());
        g1.connect(7,0,2);
        g1.connect(4,8,2);
        assertTrue(g.isConnected());
        //removing a node with one neighbor
        g.getGraph().removeNode(8);
        g1.connect(4,5,2);
        assertTrue(g.isConnected());
        // removing a edges from the middle
        g.getGraph().removeEdge(5,9);
        assertFalse(g.isConnected());
        //connecting the graph again
        g.getGraph().connect(4,9,5);
        assertTrue(g.isConnected());
        //removing all nodes
        for (int i = 0; i < 10; i++) {
            g.getGraph().removeNode(i);
        }
        assertTrue(g.isConnected());
        g.getGraph().removeNode(44);
        assertTrue(g.isConnected());
    }

    @Test
    void shortestPathDist() {
        DWGraph_DS g1= init(5,10);
        DWGraph_Algo g= new DWGraph_Algo(g1);
        assertEquals(g.shortestPathDist(2,0),11.07,0.01);
        g1.connect(2,0,12);
        assertEquals(g.shortestPathDist(2,0),11.07,0.01);
        assertEquals(g.shortestPathDist(4,3),1.90,0.01);
        g1.removeEdge(3,2);
        assertEquals(g.shortestPathDist(3,2),-1,0.01);
        assertEquals(g.shortestPathDist(3,3),0,0.01);
    }

    @Test
    void shortestPath() {
        DWGraph_DS g1= init(5,10);
        DWGraph_Algo g= new DWGraph_Algo(g1);
        List<node_data> res= new ArrayList<>();
        res.add(g.getGraph().getNode(2));
        res.add(g.getGraph().getNode(1));
        res.add(g.getGraph().getNode(4));
        res.add(g.getGraph().getNode(0));
        List<node_data> sec=g.shortestPath(2,0);
        assertEquals(g.shortestPath(2,0),res);
        res.clear();
        res.add(g.getGraph().getNode(4));
        res.add(g.getGraph().getNode(0));
        res.add(g.getGraph().getNode(3));
        assertEquals(g.shortestPath(4,3),res);
        res.clear();
        g1.removeEdge(3,2);
        assertEquals(g.shortestPath(3,2),res); //not connected
        assertEquals(g.shortestPath(0,0),res); //no path between a node to itself
    }

    @Test
    void saveNload() {
        DWGraph_DS g1= init(10,20);
        DWGraph_Algo g= new DWGraph_Algo(g1);
        g.save("graph.json");
        DWGraph_Algo k= new DWGraph_Algo(new DWGraph_DS());
        k.load("graph.json");
        //this function go over all og the graph and check if the
        //other graph and this graph are equals by:
        //number of nodes, number of edges, list of nodes by:
        //key, list of neighbors by key, and list of edges by key and weight
        assertEquals(g,k);
        k.getGraph().removeNode(5);
        assertNotEquals(g,k);
        assertFalse(k.load("myfile.json"));
        assertNotEquals(g,k);
    }
}