package api;


import java.io.Serializable;
import java.util.*;

////////////////////////////////////////////main class////////////////////////////////////////////////

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer, node_data> graph;
    private int numOfEdges, ModeCount;

    DWGraph_DS() {
        graph = new HashMap<>();
        numOfEdges = 0;
        ModeCount = 0;
    }

    DWGraph_DS(DWGraph_DS other) {
        graph = new HashMap<>();
        for (node_data cur : other.getV()) { //adding all other's nodes to our graph
            this.addNode(new NodeData(cur.getKey()));
        }
        for (node_data k : other.getV()) {
            for (int nei : ((NodeData) other.graph.get(k.getKey())).neighbors.keySet()) {
                this.connect(k.getKey(), nei, ((NodeData) k).neighbors.get(nei).getWeight());
            }
        }
        this.numOfEdges = other.edgeSize();
        this.ModeCount = other.getMC();
    }


    @Override
    public node_data getNode(int key) {
        return graph.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if (!((NodeData) graph.get(src)).neighbors.containsKey(dest)) return null;
        numOfEdges++;
        return ((NodeData) graph.get(src)).neighbors.get(dest);
    }

    @Override
    public void addNode(node_data n) {
        graph.put(n.getKey(), n);
        ModeCount++;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (src == dest) return;
        if (graph.get(src) == null || graph.get(dest) == null || w < 0) return;
        ((NodeData) graph.get(src)).neighbors.put(dest, new edgeData(src, dest, w));
        ((NodeData) graph.get(dest)).cToMe.add(src);
        ModeCount++;

    }

    @Override
    public Collection<node_data> getV() {
        return graph.values();
    }


    @Override
    public Collection<edge_data> getE(int node_id) {
        return ((NodeData) graph.get(node_id)).neighbors.values();
    }

    @Override
    public node_data removeNode(int key) {
        NodeData node = (NodeData) graph.get(key);
        for (Integer nodes : node.cToMe) {
            ((NodeData) graph.get(nodes)).neighbors.remove(key);
        }
        ModeCount++;
        return node;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data ed = new edgeData((edgeData) ((NodeData) graph.get(src)).neighbors.get(dest));
        ((NodeData) graph.get(src)).neighbors.remove(dest);
        numOfEdges--;
        ModeCount++;
        return ed;
    }

    @Override
    public int nodeSize() {
        return graph.size();
    }

    @Override
    public int edgeSize() {
        return numOfEdges;
    }

    @Override
    public int getMC() {
        return ModeCount;
    }
}


    class edgeData implements edge_data, Serializable {
        private int src, dest;
        private transient int tag;
        private double w;
        private transient String info;

        edgeData(int keySrc, int keyDest, double w) {
            this.src = keySrc;
            this.dest = keyDest;
            this.w = w;
            this.tag = 0;
            this.info = "";
        }

        edgeData(edgeData other) {
            this.src = other.src;
            this.dest = other.dest;
            this.info = other.info;
            this.tag = other.tag;
            this.w = other.w;
        }

        @Override
        public int getSrc() {
            return this.src;
        }

        @Override
        public int getDest() {
            return this.dest;
        }

        @Override
        public double getWeight() {
            return this.w;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }


        ////////////////////////////////////////////edgeLocation////////////////////////////////////////////////

        class edgeLocation implements edge_location { //TODO: create this

            @Override
            public edge_data getEdge() {
                return null;
            }

            @Override
            public double getRatio() {
                return 0;
            }
        }
    }


