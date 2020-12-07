package api;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;

import com.google.gson.stream.JsonWriter;


    public class DWGraph_Algo implements dw_graph_algorithms {
    DWGraph_DS graph;

    DWGraph_Algo(DWGraph_DS g) {
        this.graph = g;
    }
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
    public int hashCode() {
        return Objects.hash(graph);
    }

    @Override
    public void init(directed_weighted_graph g) {
        graph =(DWGraph_DS)g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.graph);
    }
    @Override
    public boolean isConnected() {
        resetT();
        boolean flag=true;
        Stack<node_data> s=new Stack<>();
        if(graph.getV().isEmpty())
            return true;
        for (node_data cur : graph.getV()) {
            if (!flag) break;
            if (cur.getTag()==-1){
            flag= tarjan((NodeData)cur,s);
            }
        }
        return flag;
    }
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest)
            return 0;
        if(graph.getNode(dest)==null||graph.getNode(src)==null)
            return -1;
        node_data desti = graph.getNode(dest);
        dijkstra(graph.getNode(src));
        double path=desti.getWeight();
        if (desti.getWeight()==Integer.MAX_VALUE){
            return -1;
        }
        return path;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        List<node_data> path = new LinkedList<>();
        if (src == dest)
            return path;
        if(graph.getNode(dest)==null||graph.getNode(src)==null)
            return path;
        dijkstra(graph.getNode(src));
        NodeData desti = (NodeData)graph.getNode(dest);
        while (desti.getPred() != null) {
            path.add(0,desti);
            desti = ((NodeData)desti.getPred());
        }
        path.add(0,desti);
        if (!path.contains(graph.getNode(src))) {
            path.clear();
            return path;
        }
        return path;
    }

    @Override
    public boolean save(String file){
        try {
            FileWriter r= new FileWriter(file);
            JsonWriter writer = new JsonWriter(r);
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("Edges");
            writer.beginArray();
            for (node_data n : graph.getV()) {
                for (edge_data ed: graph.getE(n.getKey())) {
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

    @Override
    public boolean load(String file) {
        try {
            GsonBuilder b= new GsonBuilder();
            b.registerTypeAdapter(DWGraph_DS.class,new GraphJsonDesrializeltion());
            Gson gson= b.setPrettyPrinting().create();
            FileReader r= new FileReader(file);
            graph= gson.fromJson(r, DWGraph_DS.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean tarjan(NodeData nd,Stack s){
        boolean flag;
        nd.setTag(nd.getKey());
        s.push(nd);
        for (edge_data ed: graph.getE(nd.getKey())){
            NodeData nei= (NodeData)graph.getNode(ed.getDest());
            if (nei.getTag()==-1) {
                flag=tarjan(nei,s);
                if(!flag)
                    return false;
                nd.setTag(Math.min(nd.getTag(),nei.getTag()));
            }
            else if (s.contains(nei)){
                nd.setTag(Math.min(nd.getTag(),nei.getKey()));
            }
        }
        if(nd.getTag()==nd.getKey()&&nd.getKey()!=graph.getV().stream().findFirst().get().getKey())
            return false;
        return true;
    }

    private void dijkstra(node_data src) {
        resetD();
        src.setWeight(0);
        PriorityQueue<node_data> queue = new PriorityQueue<>(graph.getV());
        while (!queue.isEmpty()) {
            node_data cur = queue.poll();
            for (edge_data ed : graph.getE(cur.getKey())) {
                NodeData nei= (NodeData)graph.getNode(ed.getDest());
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
        ((NodeData)cur).setVis(true);
        }
    }

    public void resetD() {
        for (node_data nd:graph.getV()) {
            nd.setWeight(Integer.MAX_VALUE);
            ((NodeData)nd).setPred(null);
            ((NodeData)nd).setVis(false);
            }
        }
    public void resetT(){
        for (node_data nd:graph.getV()) {
            nd.setTag(-1);
        }
    }


    private class GraphJsonDesrializeltion implements JsonDeserializer<DWGraph_DS> {
        @Override
        public DWGraph_DS deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            DWGraph_DS g=new DWGraph_DS();
            int key;
            int dest;
            double w;
            String pos;
            JsonObject j= jsonElement.getAsJsonObject();
            JsonArray jN= j.get("Nodes").getAsJsonArray();
            JsonArray jE= j.get("Edges").getAsJsonArray();
            for (JsonElement node:jN) {
                key=((JsonObject)node).get("id").getAsInt();
                pos=((JsonObject)node).get("pos").getAsString();
              g.addNode(new NodeData(key,pos));
            }
            for (JsonElement edge:jE) {
                key=((JsonObject)edge).get("src").getAsInt();
                dest=((JsonObject)edge).get("dest").getAsInt();
                w=((JsonObject)edge).get("w").getAsDouble();
                g.connect(key,dest,w);
            }
            return g;
        }
    }
}
