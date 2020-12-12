package gameClient;

import api.directed_weighted_graph;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;

public class MyLabel extends JLabel {
    private Arena _ar;
    private Graphics2D g2D;
    ImageIcon im;
    private gameClient.util.Range2Range _w2f;

    MyLabel(Arena a){
        super();
        _ar = a;
        im= new ImageIcon("./rcs/j1.png");
    }
    public void updateLabel() {
        Range rx = new Range(0, this.getWidth());
        Range ry = new Range(this.getHeight(), 10);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame);
    }
    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        g2D.setFont(new Font("OCR A Extended", Font.PLAIN, (this.getHeight() + this.getWidth()) /95));
        this.setText("hiiiiiiiiiiiiiiii");
    }
}
