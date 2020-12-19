package gameClient;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 */
public class MyFrame extends JFrame {
    private Arena _ar;
    gamePanel gP;

    MyFrame(String a) {
        super(a);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * init a panel
     */
    public void initPanel() {
        gP = new gamePanel(_ar.getGraph(), _ar);
        this.add(gP);
    }

    /**
     * function which initializing the frame and the panel (Uses gamePanel's function "initPanel").
     * @param ar
     */
    public void update(Arena ar) {
        this._ar = ar;
        initPanel();
        gP.updatePanel();
    }

    /**
     *  funcion that calls the gamePanel's function "updatePanel" and "repaint".
     * @param g
     */
    public void paint(Graphics g) {
        gP.updatePanel();
        gP.repaint();
    }
}
