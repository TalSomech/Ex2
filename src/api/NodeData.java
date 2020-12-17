package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NodeData implements node_data, Comparable<node_data> {
    private int key;
    private GeoLocation loc;
    private transient int tag;
    private transient String info;
    private transient double weight;
    public transient HashMap<Integer, edge_data> neighbors;
    transient ArrayList<Integer> cToMe;
    private transient boolean isVis;
    private transient node_data pred;

    public NodeData(int key) {
        this.key = key;
        this.tag = -1;
        neighbors = new HashMap<>();
        cToMe = new ArrayList<>();
        loc = new GeoLocation(0, 0, 0);
        this.info = loc.toString();
    }


    public NodeData(int key, String s) {
        this.key = key;
        this.tag = -1;
        neighbors = new HashMap<>();
        cToMe = new ArrayList<>();
        String[] sArray = s.split(",");
        loc = new GeoLocation(Double.parseDouble(sArray[0]), Double.parseDouble(sArray[1]), Double.parseDouble(sArray[2]));
        this.info = loc.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return key == nodeData.key &&
                Double.compare(nodeData.weight, weight) == 0 &&
                Objects.equals(loc, nodeData.loc) &&
                Objects.equals(neighbors, nodeData.neighbors);
    }

    @Override
    public int getKey() {
        return this.key;
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
    public String toString() {
        return "key - "+key;
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

    ////////////////////////////////////////////GeoLocation////////////////////////////////////////////////
    class GeoLocation implements geo_location {
        double x, y, z;

        public GeoLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setLoc(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GeoLocation that = (GeoLocation) o;
            return Double.compare(that.x, x) == 0 &&
                    Double.compare(that.y, y) == 0 &&
                    Double.compare(that.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
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
            return Math.sqrt(Math.pow(this.x - g.x(), 2) + Math.pow(this.y - g.y(), 2) + Math.pow(this.z - g.z(), 2));
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }
}



