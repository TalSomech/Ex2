package gameClient;

import api.game_service;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class scores extends JFrame{
    private Image back;
    private Graphics2D g2D;
    private int grade;
    private int moves;
    public static game_service game;
    scores( game_service game) {
        super();
        this.game=game;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        back= new ImageIcon("./rcs/scoreBack.png").getImage();
        repaint();
    }

    public void getGrade (){
         grade=0;
         moves=0;
        try {
            JSONObject line = new JSONObject(game.toString());
            JSONObject bla = line.getJSONObject("GameServer");
            grade = bla.getInt("grade");
            moves = bla.getInt("moves");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        g2D.drawImage(back, 0, 0, w, h, null);
        g2D.setFont(new Font("OCR A Extended", Font.PLAIN, (this.getHeight() * this.getWidth()) /12000));
        getGrade();
        String s= "YOU'RE GRADE IS:"+(grade);
        String k = "NUMBER OF MOVES: "+moves;
        drawCenteredString(s, this.getWidth(), this.getHeight(), g);
        drawCenteredString(k, this.getWidth(), this.getHeight()+40, g);

    }
    public void drawCenteredString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(s, x, y);
    }
}

