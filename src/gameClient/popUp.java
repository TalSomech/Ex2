package gameClient;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;

public class popUp {
    public static String getId (){
        ImageIcon pikachu = new ImageIcon("./rcs/popup.png");
        String id=  (String) JOptionPane.showInputDialog(
                null,
                "Please enter ID: ",
                "ID",
                JOptionPane.QUESTION_MESSAGE,
                pikachu,
                null,
                207603978
        );
        return id;
    }
    public static String getSen (){
        ImageIcon pikachu = new ImageIcon("./rcs/popup2.png");
        String[] options ={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
        String sen= (String) JOptionPane.showInputDialog(
                null,
                "Please enter level: ",
                "level",
                JOptionPane.QUESTION_MESSAGE,
                pikachu,
                options,
                options[0]
        );
        return sen;
    }
}
