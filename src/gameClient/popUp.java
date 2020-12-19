package gameClient;
import javax.swing.*;

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
                0
        );
        return id;
    }
    public static String getSen (){
        ImageIcon pikachu = new ImageIcon("./rcs/popup2.png");
        String sen= (String) JOptionPane.showInputDialog(
                null,
                "Please enter level: ",
                "level",
                JOptionPane.QUESTION_MESSAGE,
                pikachu,
                null,
                0
        );
        return sen;
    }
}
