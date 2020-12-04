package api;

import gameClient.Arena;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class MyPanel extends JPanel {

    directed_weighted_graph gr;
    private gameClient.util.Range2Range _w2f;
    public MyPanel(directed_weighted_graph g){
        this.gr=g;
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        _w2f = Arena.w2f(g,frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g,gr);
    }
    public void drawNode(node_data n, Graphics g){
        geo_location p=n.getLocation();
        geo_location f=this._w2f.world2frame(p);
        g.drawOval((int)f.x(),(int)f.y(),20,20);
    }
    public void drawGraph(Graphics g,directed_weighted_graph gg){
        Iterator<node_data> itr=gg.getV().iterator();
        while(itr.hasNext()){
            node_data n=itr.next();
            g.setColor(Color.black);
            drawNode(n,g);
            for(edge_data e:gg.getE(n.getKey())){
                g.setColor(Color.blue);
                drawEdge(e,g,gg);
            }
        }
    }

    public void drawEdge(edge_data e,Graphics g,directed_weighted_graph gr){
        geo_location src=gr.getNode(e.getSrc()).getLocation();
        geo_location dest=gr.getNode(e.getDest()).getLocation();
        geo_location s0=this._w2f.world2frame(src);
        geo_location d0=this._w2f.world2frame(dest);
        g.drawLine((int)s0.x(),(int)s0.y(),(int)d0.x(),(int)d0.y());
    }

}
