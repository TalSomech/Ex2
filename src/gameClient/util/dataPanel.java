package gameClient.util;

import api.directed_weighted_graph;
import gameClient.Arena;
import gameClient.CL_Agent;

import javax.swing.*;
import java.awt.*;

public class dataPanel extends JPanel {
    private Arena _ar;
    public dataPanel(Arena a){
        _ar=a;
    }

    public void updatePanel() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-70,100);
        Range2D frame = new Range2D(rx,ry);
    }
}
