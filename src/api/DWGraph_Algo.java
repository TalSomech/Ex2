package api;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


public class DWGraph_Algo implements dw_graph_algorithms {
    DWGraph_DS graph;

    DWGraph_Algo(DWGraph_DS g) {
        this.graph = g;
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
        boolean flag=true;
        if(graph.getV().isEmpty())
            return true;
        for (node_data cur : graph.getV()) {
            if (!flag) break;
            if (cur.getTag()==-1){
            flag= tarjan((NodeData)cur);
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
        double path=desti.getTag();
        if (desti.getTag()==Integer.MAX_VALUE){
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
        path.add(desti);
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
            Gson gson= b.create();
            FileReader r= new FileReader(file);
            DWGraph_DS res= gson.fromJson(r, DWGraph_DS.class);
            System.out.println(res);
            this.init(res);
            save("testLoadd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
//    public boolean load2(String file){
//        try {
//            FileReader fr=new FileReader(file);
//            JsonReader jr=new JsonReader(fr);
//            int key=0;
//            jr.beginObject();
//            jr.
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }

    public boolean tarjan(NodeData nd){
        resetT();
        Stack<node_data> s=new Stack<>();
        nd.setTag(nd.getKey());
        s.push(nd);
        for (edge_data ed: graph.getE(nd.getKey())){
            NodeData nei= (NodeData)graph.getNode(ed.getDest());
            if (nei.getTag()==-1) {
                tarjan(nei);
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
                        nei.setWeight(dis);//TODO: send mail about double
                        nei.setPred(nei);
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
            nd.setInfo("");
            nd.setTag(-1);
        }
    }

    public static void main(String[] args) {
        DWGraph_DS g = new DWGraph_DS();
        NodeData one= new NodeData(1);
        NodeData two= new NodeData(2);
        NodeData three= new NodeData(3);
        NodeData four= new NodeData(4);
        g.addNode(one);
        g.addNode(two);
        g.addNode(three);
        g.addNode(four);
        g.connect(1,2,5);
        g.connect(2,4,8);
        DWGraph_Algo G= new DWGraph_Algo(g);
        G.save("Graph.json");
        G.load("Graph.json");

    }
    private class GraphJsonDesrializeltion implements JsonDeserializer<DWGraph_DS> {
        @Override
        public DWGraph_DS deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            DWGraph_DS g=new DWGraph_DS();
            int key=0;
            int dest=0;
            double w=0;
            String pos="";
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
