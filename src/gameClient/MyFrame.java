package gameClient;

import gameClient.util.dataPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 *
 */
public class MyFrame extends JFrame implements ActionListener {
	MenuItem login;
	MenuItem load;
	private int _ind;
	private Arena _ar;
	gamePanel gP;
	dataPanel dP;
	MyFrame(String a) {
		super(a);
		//this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int _ind = 0;
		addMenu();
	}

	public void initPanel(){
		gP=new gamePanel(_ar.getGraph(),_ar);
		this.add(gP);
	}
	public void update(Arena ar) {
		this._ar = ar;
		initPanel();
		gP.update();
	}

	public void paint(Graphics g) {
		gP.updatePanel();
		gP.repaint();
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
}
