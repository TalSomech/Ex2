package gameClient;

import api.game_service;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class scores extends JFrame{
    private Image back;
    private Graphics2D g2D;
    public static game_service game;
    scores( game_service game) {
        super();
        this.game=game;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        back= new ImageIcon("./rcs/scoreBack.png").getImage();
        repaint();
    }

    public int getGrade (){
        int grade=0;
        try {
            JSONObject line = new JSONObject(game.toString());
            JSONObject bla = line.getJSONObject("GameServer");
            grade = bla.getInt("grade");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return grade;
    }

    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        g2D.drawImage(back, 0, 0, w, h, null);
        g2D.setFont(new Font("OCR A Extended", Font.PLAIN, (this.getHeight() * this.getWidth()) /12000));
        String s= "YOU'RE GRADE IS:"+(getGrade());
        drawCenteredString(s, this.getWidth(), this.getHeight(), g);
    }
    public void drawCenteredString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(s, x, y);
    }
}

