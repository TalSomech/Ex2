package api;
import java.util.LinkedList;
import api.DWGraph_DS.NodeData;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Stack;

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
    public boolean save(String file) {
        return false;
    }

    @Override
    public boolean load(String file) {
        return false;
    }


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
        //resetD();
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

    private class tempNode {
    }
}
