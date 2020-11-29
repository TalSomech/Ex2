package api;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class DWGraph_DS implements directed_weighted_graph {
    private HashMap<Integer, node_data> graph;
    private int numOfEdges, ModeCount;

    DWGraph_DS() {
        graph = new HashMap<>();
        numOfEdges = 0;
        ModeCount = 10000;
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

    public class NodeData implements node_data {
        private int key, tag;
        private String info;
        double weight;
        private HashMap<Integer, edge_data> neighbors;
        private ArrayList<Integer> cToMe;
        private boolean isVis;
        private node_data pred;
        public NodeData(int key) {
            this.key = key;
            this.info = "";
            this.tag = -1;
            neighbors = new HashMap<>();
            cToMe = new ArrayList<>();
        }

        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public geo_location getLocation() { //TODO: create this
            return null;
        }

        @Override
        public void setLocation(geo_location p) { //TODO: create this

        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public void setWeight(double w) {
            this.weight = w;
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

        public boolean isVis() {
            return isVis;
        }

        public void setVis(boolean vis) {
            isVis = vis;
        }

        public node_data getPred() {
            return pred;
        }

        public void setPred(node_data pred) {
            this.pred = pred;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override
        public int compareTo(node_data o) {
            return Double.compare(this.getWeight(), o.getWeight());
        }

        @Override
        public void setTag(int t) {
            this.tag = t;
        }

        class GeoLocation implements geo_location { //TODO: create this

            @Override
            public double x() {
                return 0;
            }

            @Override
            public double y() {
                return 0;
            }

            @Override
            public double z() {
                return 0;
            }

            @Override
            public double distance(geo_location g) {
                return 0;
            }
        }
    }

    class edgeData implements edge_data {
        private int keySrc, keyDest, tag;
        private double weight;
        private String info;

        edgeData(int keySrc, int keyDest, double w) {
            this.keySrc = keySrc;
            this.keyDest = keyDest;
            this.weight = w;
            this.tag = 0;
            this.info = "";
        }

        edgeData(edgeData other) {
            this.keySrc = other.keySrc;
            this.keyDest = other.keyDest;
            this.info = other.info;
            this.tag = other.tag;
            this.weight = other.weight;
        }

        @Override
        public int getSrc() {
            return this.keySrc;
        }

        @Override
        public int getDest() {
            return this.keyDest;
        }

        @Override
        public double getWeight() {
            return this.weight;
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
}

