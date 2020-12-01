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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DWGraph_DS that = (DWGraph_DS) o;
//        return numOfEdges == that.numOfEdges &&
//                ModeCount == that.ModeCount &&
//                Objects.equals(graph, that.graph);
//    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof directed_weighted_graph)) {

            return false;
        }
        if(this.nodeSize()!=((directed_weighted_graph) obj).nodeSize()||this.edgeSize()!=((directed_weighted_graph) obj).edgeSize())
            return false;
        Iterator<node_data> it =((directed_weighted_graph) obj).getV().iterator();
        for (node_data nd:this.getV()) {
            if(!(nd).equals(it.next()))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, numOfEdges, ModeCount);
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
        if (n==null||getNode(n.getKey())!=null) return;
        graph.put(n.getKey(), n);
        ModeCount++;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (src == dest) return;
        if (graph.get(src) == null || graph.get(dest) == null || w < 0||((NodeData) graph.get(src)).neighbors.containsKey(dest)) return;
        ((NodeData) graph.get(src)).neighbors.put(dest, new edgeData(src, dest, w));
        ((NodeData) graph.get(dest)).cToMe.add(src);
        ModeCount++;
        numOfEdges++;
    }

    @Override
    public Collection<node_data> getV() {
        return graph.values();
    }


    @Override
    public Collection<edge_data> getE(int node_id) {
        if (!graph.containsKey(node_id)) return null;
        return ((NodeData) graph.get(node_id)).neighbors.values();
    }

    @Override
    public node_data removeNode(int key) {
        NodeData node = (NodeData) graph.get(key);
        if (node == null)
            return null;
        int dest;
        int src;
        while(node.cToMe.size()!=0){
            src=node.cToMe.stream().findFirst().get();
            removeEdge(src,key);
        }
        while(getE(key).size()!=0) {
         dest=getE(key).stream().findFirst().get().getDest();
         removeEdge(key,dest);
        }
        graph.remove(key);
        ModeCount++;
        return node;

    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if(graph.get(src)==null||graph.get(dest)==null)
            return null;
        if(!((NodeData)graph.get(src)).neighbors.containsKey(dest))
        return null;
        edge_data ed=((NodeData) graph.get(src)).neighbors.get(dest);
        ((NodeData) graph.get(src)).neighbors.remove(dest);
        ((NodeData)getNode(dest)).cToMe.remove((Integer)src);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        edgeData edgeData = (edgeData) o;
        return src == edgeData.src &&
                dest == edgeData.dest &&
                Double.compare(edgeData.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest, tag, w, info);
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


