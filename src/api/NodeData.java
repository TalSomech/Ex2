package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NodeData implements node_data {
    private int id;
    private transient GeoLocation loc;
    private transient int tag;
    private String pos;
    private transient double weight;
    public transient HashMap<Integer, edge_data> neighbors;
    transient ArrayList<Integer> cToMe;
    private transient boolean isVis;
    private transient node_data pred;

    public NodeData(int key) {
        this.id = key;
        this.tag = -1;
        neighbors = new HashMap<>();
        cToMe = new ArrayList<>();
        loc = new GeoLocation(0, 0, 0);
        this.pos = loc.toString();
    }

    public NodeData(int key, String s) {
        this.id = key;
        this.tag = -1;
        neighbors = new HashMap<>();
        cToMe = new ArrayList<>();
        String[] sArray=s.split(",");
        loc= new NodeData.GeoLocation(Double.parseDouble(sArray[0]),Double.parseDouble(sArray[1]), Double.parseDouble(sArray[2]));
        this.pos = loc.toString();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public geo_location getLocation() { //TODO: create this
        return loc;
    }

    @Override
    public void setLocation(geo_location p) {
        loc.setX(p.x());
        loc.setY(p.y());
        loc.setZ(p.z());
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
    public String getPos() {
        return this.pos;
    }

    @Override
    public void setPos(String s) {
        this.pos = s;
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
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(node_data o) {
        return Double.compare(this.getWeight(), o.getWeight());
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }
    ////////////////////////////////////////////GeoLocation////////////////////////////////////////////////

    class GeoLocation implements geo_location {
        double x, y, z;

        public GeoLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public double x() {
            return x;
        }

        @Override
        public double y() {
            return y;
        }

        @Override
        public double z() {
            return z;
        }

        public void setX(double other) {
            this.x = other;
        }

        public void setY(double other) {
            this.y = other;
        }

        public void setZ(double other) {
            this.z = other;
        }

        @Override
        public double distance(geo_location g) {
            return Math.sqrt(Math.pow(this.x + g.x(), 2) + Math.pow(this.y + g.y(), 2) + Math.pow(this.z + g.z(), 2));
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }
}
