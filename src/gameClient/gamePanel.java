package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class gamePanel extends JPanel {
    private Graphics2D g2D;
    private Image agent, pikachu, mew, back;
    private Arena _ar;
    directed_weighted_graph gr;
    private gameClient.util.Range2Range _w2f;
    MyLabel jLabel;

    public gamePanel(directed_weighted_graph g, Arena a) {
        super();
        _ar = a;
        gr = g;
        agent = new ImageIcon("./rcs/pokeball.png").getImage();
        pikachu = new ImageIcon("./rcs/picka.png").getImage();
        mew = new ImageIcon("./rcs/chi.png").getImage();
        back = new ImageIcon("./rcs/newBack.png").getImage();
    }

    public void updatePanel() {
        Range rx = new Range(5, this.getWidth() - 10);
        Range ry = new Range(this.getHeight() - 10, 150);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame);
        initLabel();
        jLabel.updateLabel();
    }

    public void initLabel(){
        jLabel= new MyLabel(_ar);
        this.add(jLabel);
    }

    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        if (_ar.getAgents().size() >= 3) back = new ImageIcon("./rcs/Back.png").getImage();
        if (_ar.getAgents().size() == 2) back = new ImageIcon("./rcs/Back2Agents.png").getImage();
        if (_ar.getAgents().size() == 1) back = new ImageIcon("./rcs/Back1Agent.png").getImage();
        g2D.drawImage(back, 0, 0, w, h, null);
        drawGraph(g);
        drawPokemons(g);
        drawAgants(g);
        drawInfo(g);
        jLabel.updateLabel();
        jLabel.repaint();
    }

    public void drawNode(node_data n, Graphics g) {
        geo_location p = n.getLocation();
        geo_location f = this._w2f.world2frame(p);
        g.fillOval((int) f.x() - 5, (int) f.y() - 5, 2 * 5, 2 * 5);
        g.drawString("" + n.getKey(), (int) f.x(), (int) f.y() - 4 * 5);
    }

    public void drawGraph(Graphics g) {
        g2D = (Graphics2D) g;
        Iterator<node_data> itr = gr.getV().iterator();
        while (itr.hasNext()) {
            node_data n = itr.next();
            g.setColor(Color.black);
            drawNode(n, g);
            for (edge_data e : gr.getE(n.getKey())) {
                g2D.setStroke(new BasicStroke(3));
                g2D.setColor(Color.WHITE);
                drawEdge(e, g2D, gr);
            }
        }
    }

    private void drawInfo(Graphics g) {
        List<String> str = _ar.get_info();
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Color.blue);
        g2D.setFont(new Font("OCR A Extended", Font.PLAIN, (this.getHeight() + this.getWidth()) / 80));
        //String dt=_ar
        int x0 = this.getWidth() / 70;
        int y0 = this.getHeight() / 20;
        g2D.drawString(_ar.getTime(), (int) x0 * 5, (int) y0);
        g2D.setFont(new Font("OCR A Extended", Font.PLAIN, (this.getHeight() + this.getWidth()) /95));
        for (int i = 0; i < str.size(); i++) {
            g2D.drawString(str.get(i), (int) x0*5, (int) y0 + (i*2+3) * 10);
        }
    }

    public void drawEdge(edge_data e, Graphics g, directed_weighted_graph gr) {
        geo_location src = gr.getNode(e.getSrc()).getLocation();
        geo_location dest = gr.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(src);
        geo_location d0 = this._w2f.world2frame(dest);
        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
    }

    private void drawPokemons(Graphics g) {
        g2D = (Graphics2D) g;
        List<CL_Pokemon> fs = _ar.getPokemons();
        if (fs != null) {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while (itr.hasNext()) {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r = 10;
                if (c != null) {
                    geo_location fp = this._w2f.world2frame(c);
                    if (f.getType() < 0) {
                        g2D.drawImage(pikachu, (int) fp.x() - 3 * r, (int) fp.y() - r, 5 * r, 3 * r, null);
                    } else {
                        g2D.drawImage(mew, (int) fp.x() - r, (int) fp.y() - r, 3 * r, 3 * r, null);
                    }
                }
            }
        }
    }

    private void drawAgants(Graphics g) {
        g2D = (Graphics2D) g;
        List<CL_Agent> rs = _ar.getAgents();
        g.setColor(Color.red);
        int i = 0;
        while (rs != null && i < rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r = 8;
            i++;
            if (c != null) {
                geo_location fp = this._w2f.world2frame(c);
                g2D.drawImage(agent, (int) fp.x() - 2 * r, (int) fp.y() - r, 3 * r, 3 * r, null);
            }
        }
    }
}
