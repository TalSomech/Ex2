package api;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * this class is used to perform different algorithms on the graph
 * it implements dw graph algorithms
 */
public class DWGraph_Algo implements dw_graph_algorithms {
    DWGraph_DS graph;

    /**
     * constructor
     * @param g
     */
    DWGraph_Algo(DWGraph_DS g) {
        this.graph = g;
    }

    /**
     * construtor
     */
    public DWGraph_Algo() {
        this.graph = new DWGraph_DS();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_Algo that = (DWGraph_Algo) o;
        return Objects.equals(graph, that.graph);
    }

    @Override
    public void init(directed_weighted_graph g) {
        graph = (DWGraph_DS) g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }


    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.graph);
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     * this function uses the Trajan's algorithm to find SSC to check if there are more than 1
     * @return boolean if connected
     */
    @Override
    public boolean isConnected() {
        resetT();
        Tarjan t = new Tarjan(this.graph);
        boolean flag = true;
        if (graph.getV().isEmpty())
            return true;
        for (node_data cur : graph.getV()) {
            if (!flag) break;
            if (cur.getTag() == -1) {
                flag = t.isConnected();
            }
        }
        return flag;
    }

    /**
     * this function returns all the SCC in the graph
     * @return a list SSC of nodes
     */
    public List<List<node_data>> getComponents (){
        Tarjan t = new Tarjan(this.graph);
        resetT();
        t.tarjan();
        return t.getComponents();
    }

    /**
     * this function find the shortest distant between 2 vertices in the graph
     * using the dijkstra's algorithm to a weighted directional graph
     * returns -1 if there isn't a path between the 2 nodes
     * @param src - start node
     * @param dest - end (target) node
     * @return double of cost of the distant from a source node to destination
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest)
            return 0;
        if (graph.getNode(dest) == null || graph.getNode(src) == null)
            return -1;
        node_data desti = graph.getNode(dest);
        dijkstra(graph.getNode(src));
        double path = desti.getWeight();
        if (desti.getWeight() == Integer.MAX_VALUE) {
            return -1;
        }
        return path;
    }

    /**
     * this function find the shortest path between 2 vertices in the graph
     * using the dijkstra's algorithm to a weighted directional graph
     * return null if there isn't a path
     * @param src - start node
     * @param dest - end (target) node
     * @return list of nodes representing the path from source node to destination
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        List<node_data> path = new LinkedList<>();
        if (src == dest)
            return path;
        if (graph.getNode(dest) == null || graph.getNode(src) == null)
            return path;
        dijkstra(graph.getNode(src));
        NodeData desti = (NodeData) graph.getNode(dest);
        while (desti.getPred() != null) {
            path.add(0, desti);
            desti = ((NodeData) desti.getPred());
        }
        path.add(0, desti);
        if (!path.contains(graph.getNode(src))) {
           return null;
        }
        return path;
    }
    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format using JsonWriter lib
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        try {
            FileWriter r = new FileWriter(file);
            JsonWriter writer = new JsonWriter(r);
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("Edges");
            writer.beginArray();
            for (node_data n : graph.getV()) {
                for (edge_data ed : graph.getE(n.getKey())) {
                    writer.beginObject();
                    writer.name("src").value(ed.getSrc());
                    writer.name("w").value(ed.getWeight());
                    writer.name("dest").value(ed.getDest());
                    writer.endObject();
                }
            }
            writer.endArray();
            writer.name("Nodes");
            writer.beginArray();
            for (node_data n : graph.getV()) {
                writer.beginObject();
                if(n.getLocation()!=null)
                writer.name("pos").value(n.getLocation().toString());
                writer.name("id").value(n.getKey());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     * this function uses a custom graph Gson Deserialization
     * and GsonBuilder
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        try {
            GsonBuilder b = new GsonBuilder();
            b.registerTypeAdapter(DWGraph_DS.class, new GraphJsonDesrializeltion());
            Gson gson = b.setPrettyPrinting().create();
            FileReader r = new FileReader(file);
            graph = gson.fromJson(r, DWGraph_DS.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * the algorithm finds all the connected nodes to a source node
     * the algorithm checks weighted paths the neighboring nodes until all nodes are visited
     * and sets every node's predecessor by the shortest path from src node to it
     * runTime:(O(V+E))
     * @param src node
     * @return int
     */
    private void dijkstra(node_data src) {
        resetD();
        src.setWeight(0);
        PriorityQueue<node_data> queue = new PriorityQueue<>(graph.getV());
        while (!queue.isEmpty()) {
            node_data cur = queue.poll();
            for (edge_data ed : graph.getE(cur.getKey())) {
                NodeData nei = (NodeData) graph.getNode(ed.getDest());
                if (!nei.isVis()) {
                    double dis = cur.getWeight() + ed.getWeight();
                    if (nei.getWeight() > dis) {
                        nei.setWeight(dis);
                        nei.setPred(cur);
                        queue.remove(nei);
                        queue.add(nei);
                    }
                }
            }
            ((NodeData) cur).setVis(true);
        }
    }

    /**
     * resets all the vertices in the graph to be able to use the dijkstra algorithm
     */
    public void resetD() {
        for (node_data nd : graph.getV()) {
            nd.setWeight(Integer.MAX_VALUE);
            ((NodeData) nd).setPred(null);
            ((NodeData) nd).setVis(false);
        }
    }
    /**
     * resets all the vertices in the graph to be able to use the tarjan algorithm
     */
    public void resetT() {
        for (node_data nd : graph.getV()) {
            nd.setTag(-1);
            ((NodeData) nd).setVis(false);
        }
    }

    /**
     * this class implements JSON Deserializer inorder to be able to load
     * a graph in the load function
     */
    private class GraphJsonDesrializeltion implements JsonDeserializer<DWGraph_DS> {
        @Override
        public DWGraph_DS deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            DWGraph_DS g = new DWGraph_DS();
            int key;
            int dest;
            double w;
            String pos;
            JsonObject j = jsonElement.getAsJsonObject();
            JsonArray jN = j.get("Nodes").getAsJsonArray();
            JsonArray jE = j.get("Edges").getAsJsonArray();
            for (JsonElement node : jN) {
                key = ((JsonObject) node).get("id").getAsInt();
                pos = ((JsonObject) node).get("pos")==null? "-5,-5,-5":((JsonObject) node).get("pos").getAsString();
                g.addNode(new NodeData(key, pos));
            }
            for (JsonElement edge : jE) {
                key = ((JsonObject) edge).get("src").getAsInt();
                dest = ((JsonObject) edge).get("dest").getAsInt();
                w = ((JsonObject) edge).get("w").getAsDouble();
                g.connect(key, dest, w);
            }
            return g;
        }
    }

    /**
     * this class is used to find all the SCC in the graph and save them
     */
    private class Tarjan {
        int time;
        DWGraph_DS g;
        Stack<NodeData> s;
        List<List<node_data>> components;

        public Tarjan(DWGraph_DS g){
            this.g = g;
            s = new Stack<>();
            time = 0;
            components = new ArrayList<>();
        }

        /**
         * this function is the implementation of the Tarjan's algorithm to find SCC
         * the algorithm: https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
         *
         * @return list of all the nodes in the graph , divided to SCC
         */
        public List<List<node_data>> tarjan() {
            for (node_data nds : g.getV()) {
                if (((NodeData) nds).isVis() == false)
                    dfs((NodeData) nds);
            }
            return components;
        }

        /**
         * this is an implementation of the depth for search algorithm used in the trajan algorithm
         * dfs: https://en.wikipedia.org/wiki/Depth-first_search
         * @param nds - a start node to begin dfs on
         */
        public void dfs (NodeData nds) {
            nds.setTag(time++);
            nds.setVis(true);
            s.push(nds);
            boolean isComponentRoot = true;
            for (edge_data ed : graph.getE(nds.getKey())) {
                NodeData nei = (NodeData) graph.getNode(ed.getDest());
                if (nei.isVis() ==false)
                    dfs(nei);
                if (nds.getTag()>nei.getTag()){
                    nds.setTag(nei.getTag());
                    isComponentRoot=false;
                }
            }
            if (isComponentRoot){
                List<node_data> component= new ArrayList<>();
                while (true){
                    node_data x= s.pop();
                    component.add(x);
                    x.setTag(Integer.MAX_VALUE);
                    if (x==nds) break;
                }
                components.add(component);
            }
        }

        /**
         *this function checks if the graph is connected
         * @return boolean is connected
         */
        public boolean isConnected() {
            tarjan();
            return (this.components.size() == 1);
        }

        /**
         * this function return all the vertices in the graph divided into SCC
         * @return list of SCC
         */
        public List<List<node_data>> getComponents() {
            return this.components;
        }
    }
}
