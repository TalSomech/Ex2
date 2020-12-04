package gameClient.util;

import api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MyFrame2 extends JFrame implements ActionListener {
    MenuItem login;
    MenuItem load;
    directed_weighted_graph gr;
    private JPanel pn;


    public  MyFrame2(directed_weighted_graph gr){
        this.setSize(1000,700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.gr=gr;
        addMenu();
        initPanel();

    }

    public void addMenu(){
        MenuBar menu=new MenuBar();
        Menu options=new Menu("Options");
        menu.add(options);
        this.setMenuBar(menu);
        login=new MenuItem("login");
        load=new MenuItem("load");
        login.addActionListener(this);
        load.addActionListener(this);
        options.add(login);
        options.add(load);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==load){

        }
        if(e.getSource()==login){

        }
    }

    public void initPanel(){
        //pn= new gamePanel(gr);
        pn.setPreferredSize(new Dimension(this.size().width*3/4,this.size().height*3/4));
       // graph.setMaximumSize(new Dimension(500,500));
////        JPanel data= new JPanel();
//        data.setBackground(Color.gray);
//        data.setPreferredSize(new Dimension(this.size().width*1/4,this.size().height*1/4));
        this.setLayout(new BorderLayout());
        JPanel container = new JPanel();
        pn.add(new JLabel("Game"));
//        data.add(new JLabel("Data"));
       // container.setLayout(new GridLayout(1,3));
//        container.add(data,BorderLayout.EAST);
       // container.add(graph,GridLayout);
        container.add(pn);
//        container.add(data);
        this.add(container);
    }

    public static void main(String[] args) {
        Random _rnd = new Random(1);
        int rnd;
        int rnd2;
        directed_weighted_graph g = new DWGraph_DS();
        for (int i = 0; i < 10; i++) {
            g.addNode(new NodeData(i));
        }
        while (g.edgeSize() < 20) {
            rnd = Math.abs((_rnd.nextInt()) % 10);
            rnd2 = Math.abs((_rnd.nextInt()) % 10);
            g.connect(rnd, rnd2, _rnd.nextDouble() * 100);
        }
        MyFrame2 f= new MyFrame2(g);
    }

}
