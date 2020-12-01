package api;

import org.junit.jupiter.api.Test;


import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

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
            g.connect(rnd, rnd2, _rnd.nextDouble() * 100);
        }
        return g;
    }

    @Test
    void getNode() {
        DWGraph_DS g1 = init(10, 12);
        assertNull(g1.getNode(91));
        assertNull(g1.getNode(72));
        assertNull(g1.getNode(13));
        g1.removeNode(3);
        g1.removeNode(9);
        assertNull(g1.removeNode(22));
        assertNull(g1.getNode(3));
        assertNull(g1.getNode(9));
        g1.getNode(1).setInfo("true?");
        node_data temp = g1.getNode(1);
        node_data temp2 = g1.getNode(11);
        assertEquals("true?", temp.getInfo());
        assertNull(temp2);
    }

    @Test
    void getEdge() {
        DWGraph_DS g = init(10, 20);
        edge_data ed= g.getEdge(8,5);
        assertEquals(ed,g.removeEdge(8,5));
        assertNull(g.removeEdge(8,5));
    }

    @Test
    void addNode() {
        DWGraph_DS g1 = init(10, 20);
        //adding non existing nodes
        g1.addNode(new NodeData(19));
        assertEquals(g1.nodeSize(), 11);
        g1.addNode(new NodeData(23));
        assertEquals(g1.nodeSize(), 12);
        g1.addNode(new NodeData(15));
        assertEquals(g1.nodeSize(), 13);
        // adding existing nodes
        g1.addNode(new NodeData(5));
        assertEquals(g1.nodeSize(), 13);
        g1.addNode(new NodeData(8));
        assertEquals(g1.nodeSize(), 13);
        g1.addNode(new NodeData(0));
        assertEquals(g1.nodeSize(), 13);
    }

    @Test
    void connect() {
        DWGraph_DS g1 = init(10, 20);
        // connect existing nodes with no edges between them
        g1.connect(0, 1, 6);
        assertEquals(g1.edgeSize(), 21);
        g1.connect(2, 9, 6);
        assertEquals(g1.edgeSize(), 22);
        g1.connect(7, 8, 6);
        assertEquals(g1.edgeSize(), 23);
        // connect existing nodes with exiting edge between them
        g1.connect(6, 4, 6);
        assertEquals(g1.edgeSize(), 23);
        g1.connect(0, 7, 6);
        assertEquals(g1.edgeSize(), 23);
        g1.connect(5, 3, 6);
        assertEquals(g1.edgeSize(), 23);
        //connect non existing nodes
        g1.connect(52, 48, 6);
        assertEquals(g1.edgeSize(), 23);
        g1.connect(97, 52, 6);
        assertEquals(g1.edgeSize(), 23);
        g1.connect(14, 14, 6);
        assertEquals(g1.edgeSize(), 23);
        // connect a node to itself
        g1.connect(7, 7, 6);
        assertEquals(g1.edgeSize(), 23);
        g1.connect(5, 5, 6);
        assertEquals(g1.edgeSize(), 23);
    }

    @Test
    void getV() {
        DWGraph_DS g1 = init(10, 20);
        Collection<node_data> s = new HashSet<>(g1.nodeSize());
        for (int i = 0; i < 10; i++) {
            s.add(g1.getNode(i));
        }
        assertTrue(s.containsAll(g1.getV()));
        assertTrue(g1.getV().containsAll(s));
        g1.removeNode(5);
        assertTrue(s.containsAll(g1.getV()));
        assertFalse(g1.getV().containsAll(s));
    }

    @Test
    void getE() {
        DWGraph_DS g1 = init(10, 20);
        assertEquals(g1.getE(5).size(),5);
        g1.removeEdge(5,3);
        assertEquals(g1.getE(5).size(),4);
        g1.removeEdge(8,5);
        assertEquals(g1.getE(5).size(),4);
        g1.removeEdge(7,8);
        assertEquals(g1.getE(5).size(),4);
        g1.removeNode(5);
        assertNull(g1.getE(5));
    }

    @Test
    void removeNode() {
        DWGraph_DS g1 = init(10, 20);
        /// remove existing node
        assertEquals(g1.getNode(2), g1.removeNode(2)); //returns the right node
        assertEquals(g1.nodeSize(),9); //removed the node from the list of nodes
        assertEquals(g1.edgeSize(),15); //removed the node's edges from the edges list
        assertEquals(g1.getE(6).size(), 2); //didn't hurt the neighbors list of neighbors
        assertEquals(g1.getE(1).size(), 1);
        assertEquals(g1.getE(5).size(), 4);
        assertNull(g1.getNode(2)); //the list of nodes doesn't contains the node's key anymore
        /// remove another exiting node
        assertEquals(g1.getNode(7),g1.removeNode(7)); //returns the right node
        assertEquals(g1.nodeSize(),8); //removed the node from the list of nodes
        assertEquals(g1.edgeSize(),13); //removed the node's edges from the edges list
        assertNull(g1.getNode(10)); //the list of nodes doesn't contains the node's key anymore
        /// remove non exiting node
        assertEquals(g1.getNode(91), g1.removeNode(91));
        assertEquals(g1.nodeSize(),8);
        assertEquals(g1.edgeSize(),13);
        assertNull(g1.getNode(91));
        Collection<node_data> s = new HashSet<>(g1.nodeSize());
        for (int i = 0; i < 10; i++) {
            s.add(g1.getNode(i));
        }
        s.remove(2);
        s.remove(7);
        assertTrue(s.containsAll(g1.getV())); //the two nodes has been removed from the collections of the nodes
        }

    @Test
    void removeEdge() {
        DWGraph_DS g1 = init(10, 20);
        // remove an existing edge
        g1.removeEdge(8,1);
        assertEquals(g1.nodeSize(),10); //didn't hurt the number of nodes
        assertEquals(g1.edgeSize(),19); // removed the edge from the list of edges
        assertFalse(g1.getE(8).contains(g1.getNode(1)));
        assertFalse(g1.getE(1).contains(g1.getNode(8)));
        assertEquals(g1.getE(8).size(),1);
        assertEquals(g1.getE(1).size(),2);
        // remove another existing edge
        g1.removeEdge(9,6);
        assertEquals(g1.nodeSize(),10); //didn't hurt the number of nodes
        assertEquals(g1.edgeSize(),18); // removed the edge from the list of edges
        assertFalse(g1.getE(9).contains(g1.getNode(6))); //9 is not a neighbor of 6
        assertFalse(g1.getE(6).contains(g1.getNode(9))); //6 is not a neighbor of 9
        assertEquals(g1.getE(9).size(),2);
        assertEquals(g1.getE(6).size(),2);
        // remove a non existing edge
        g1.removeEdge(7,8);
        assertEquals(g1.nodeSize(),10); //didn't hurt the number of nodes
        assertEquals(g1.edgeSize(),18); // removed the edge from the list of edges
        // remove a non existing edge from a non existing node
        g1.removeEdge(91,21);
        assertEquals(g1.nodeSize(),10); //didn't hurt the number of nodes
        assertEquals(g1.edgeSize(),18); // removed the edge from the list of edges
    }

    @Test
    void nodeSize() {
        DWGraph_DS g = init(10, 5);
        int nON = g.nodeSize();
        assertEquals(10, g.nodeSize());
        g.removeNode(5);
        nON--;
        assertEquals(nON, g.nodeSize());
        g.removeNode(2509);//doesn't exist
        assertEquals(nON, g.nodeSize());
        g.removeNode(2);
        nON--;
        assertEquals(nON, g.nodeSize());
        g.removeNode(5);//should not exist
        assertEquals(nON, g.nodeSize());
    }

    @Test
    void edgeSize() {
        DWGraph_DS g = init(10, 20);
        int nOE = g.edgeSize();
        assertEquals(20, g.edgeSize());
        removeCheck(g, nOE);
    }

    @Test
    void getMC() {
        DWGraph_DS g = init(10, 20);
        int mC = g.getMC();
        g.removeEdge(5, 2);
        mC++;
        g.removeEdge(2, 5);
        mC = mC + g.getE(5).size()+3 ;//3 nodes are connected to 5
        g.removeNode(5);
        g.removeNode(5);
        assertEquals(mC, g.getMC());
        g.addNode(new NodeData(5));
        mC++;
        assertEquals(mC, g.getMC());
        g.addNode(new NodeData(5));
        assertEquals(mC, g.getMC());
    }

    ////////////////////////////////////////////////////////////////////////////
    private void removeCheck(DWGraph_DS g, int nOE) {

        g.removeEdge(5, g.getE(5).stream().findAny().get().getDest());
        g.removeEdge(4, 5);//doesn't exist
        g.removeEdge(236593265, 5);//doesn't exist
        nOE -= 1;
        assertEquals(nOE, g.edgeSize());
        nOE -= g.getE(5).size()+2;// there are 2 nodes connected to 5
        g.removeNode(5);
        assertEquals(nOE, g.edgeSize());
    }
}