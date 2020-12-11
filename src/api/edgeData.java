package api;

import java.io.Serializable;
import java.util.Objects;

public class edgeData implements edge_data, Serializable {
    private int src, dest;
    private transient int tag;
    private double w;
    private transient String info;

    public edgeData(int keySrc, int keyDest, double w) {
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
