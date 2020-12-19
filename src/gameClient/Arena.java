package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * This class represents a multi Agents Arena which move on a graph - grabs Pokemons and avoid the Zombies.
 *
 * @author boaz.benmoshe
 */
public class Arena {
    public static final double EPS1 = 0.001, EPS2 = EPS1 * EPS1, EPS = EPS2;
    private directed_weighted_graph _gg;
    private static List<CL_Agent> _agents;
    private static List<CL_Pokemon> _pokemons;
    private List<String> _info;
    private String time;
    private static Point3D MIN = new Point3D(0, 100, 0);
    private static Point3D MAX = new Point3D(0, 100, 0);

    public Arena() {

        _info = new ArrayList<>(4);
        _info.add(" ");
        time = "";
    }

    private Arena(directed_weighted_graph g, List<CL_Agent> r, List<CL_Pokemon> p) {
        _gg = g;
        this.setAgents(r);
        this.setPokemons(p);
    }

    public void setPokemons(List<CL_Pokemon> f) {
        this._pokemons = f;
    }

    public void setAgents(List<CL_Agent> f) {
        this._agents = f;
    }

    public void setGraph(directed_weighted_graph g) {
        this._gg = g;
    }

    /**
     * initializes the Arena
     */
    private void init() {
        MIN = null;
        MAX = null;
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        Iterator<node_data> iter = _gg.getV().iterator();
        while (iter.hasNext()) {
            geo_location c = iter.next().getLocation();
            if (MIN == null) {
                x0 = c.x();
                y0 = c.y();
                x1 = x0;
                y1 = y0;
                MIN = new Point3D(x0, y0);
            }
            if (c.x() < x0) {
                x0 = c.x();
            }
            if (c.y() < y0) {
                y0 = c.y();
            }
            if (c.x() > x1) {
                x1 = c.x();
            }
            if (c.y() > y1) {
                y1 = c.y();
            }
        }
        double dx = x1 - x0, dy = y1 - y0;
        MIN = new Point3D(x0 - dx / 10, y0 - dy / 10);
        MAX = new Point3D(x1 + dx / 10, y1 + dy / 10);
    }

    public List<CL_Agent> getAgents() {
        return _agents;
    }

    public List<CL_Pokemon> getPokemons() {
        return _pokemons;
    }

    public directed_weighted_graph getGraph() {
        return _gg;
    }

    public List<String> get_info() {
        return _info;
    }

    public void set_info(String s, int id) {
        if (_info.size() != id) {
            _info.remove(id);
        }
        _info.add(id, s);
    }

    public void setTime(String t) {
        this.time = t;
    }

    public String getTime() {
        return this.time;
    }

    ////////////////////////////////////////////////////

    /**
     * reads agents from a Jsonfile and creates,or updates all the agents in the game
     * as an actual agent object
     * @param aa
     * @param gg
     * @return returns a list of agents which represents the game's agents current status.
     */
    public static List<CL_Agent> updateAgents(String aa, directed_weighted_graph gg) {
        CL_Agent c;
        ArrayList<CL_Agent> ans = new ArrayList<>();
        try {
            JSONObject ttt = new JSONObject(aa);
            JSONArray ags = ttt.getJSONArray("Agents");
            for (int i = 0; i < ags.length(); i++) {
                JsonObject trainer = JsonParser.parseString(ags.get(i).toString()).getAsJsonObject();
                int id = trainer.get("Agent").getAsJsonObject().get("id").getAsInt();
                CL_Agent is = isIn_Agents(id);
                if(is==null){
                    c =new CL_Agent(gg, 0);
                }
                else{
                    c=is;
                   c.set_curr_fruit(null);
                   c.setPath(new LinkedList<>(),null);
                }
                c.update(ags.get(i).toString());
                ans.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * checks if an agent already exists in the arena so it will update it
     * @param agent
     * @return a pointer to said agent
     */
    private static CL_Agent isIn_Agents (int agent){
        if(_agents ==null) return null;
        for (CL_Agent ag:_agents) {
            if (ag.getID()==agent) return ag;
        }
        return null;
    }

    /**
     *  function which reads the status and number of pokemons
     *  from the server in the format of Json and turns it into actual objects of pokemons ,
     *  the function returns a list of pokemons
     *  which represents the current status of all the current pokemons in the game
     *  the function only reads the pokemon and does not actually put it in the graph.
     * @param fs
     * @return array list of objects of pokemons
     */
    public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
        ArrayList<CL_Pokemon> ans = new ArrayList<>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for (int i = 0; i < ags.length(); i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                String p = pk.getString("pos");
                    CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
                ans.add(f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * a function which is used after json2Pokemon which updates each pokemon's edge
     * based on their geolocation in the game and puts each pokemon on the graph.
     * @param fr
     * @param g
     */
    public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        while (itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while (iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(fr.getLocation(), e, fr.getType(), g);
                if (f) {
                    fr.set_edge(e);
                }
            }

        }

    }

    /**
     * checks if an agent is on an edge
     * @param p
     * @param src
     * @param dest
     * @return boolean if its on an edge
     */
    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if (dist > d1 - EPS2) {
            ans = true;
        }
        return ans;
    }

    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p, src, dest);
    }

    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if (type < 0 && dest > src) {
            return false;
        }
        if (type > 0 && src > dest) {
            return false;
        }
        return isOnEdge(p, src, dest, g);
    }

    /**
     * this function turns the graph into a frame visualisation dimensions
     * @param g
     * @return Range2D object
     */
    private static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        boolean first = true;
        while (itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if (first) {
                x0 = p.x();
                x1 = x0;
                y0 = p.y();
                y1 = y0;
                first = false;
            } else {
                if (p.x() < x0) {
                    x0 = p.x();
                }
                if (p.x() > x1) {
                    x1 = p.x();
                }
                if (p.y() < y0) {
                    y0 = p.y();
                }
                if (p.y() > y1) {
                    y1 = p.y();
                }
            }
        }
        Range xr = new Range(x0, x1);
        Range yr = new Range(y0, y1);
        return new Range2D(xr, yr);
    }

    /**
     * fuction to turn the graph's "world" (by Geolocation and such) to actual visualisation
     * that can be read in the GUI.
     * it uses GraphRange function
     * @param g
     * @param frame
     * @return Range2Range object
     */
    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

}
