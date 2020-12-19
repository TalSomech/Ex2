package gameClient;
import javax.swing.*;

public class popUp {
    public static int getId (){
        boolean flag =false;
        String id;
        int ID=0;
        while(!flag) {
            ImageIcon pikachu = new ImageIcon("./rcs/popup.png");
            id = (String) JOptionPane.showInputDialog(
                    null,
                    "Please enter ID: ",
                    "ID",
                    JOptionPane.QUESTION_MESSAGE,
                    pikachu,
                    null,
                    0
            );
            if(id==null){
                System.exit(0);
            }
            try {
                 ID = Integer.parseInt(id);
                flag=true;
            } catch (NumberFormatException e) {
                invalid();
            }
            catch (Exception E){
                flag=true;
            }
        }
        return ID;
    }
    public static int getSen () {
        boolean flag = false;
        String sen;
        int SEN = 0;
        ImageIcon ball=new ImageIcon("./rcs/popup2.png");
        while (!flag) {
            // ball = new ImageIcon("./rcs/popup2.png");
            sen = (String) JOptionPane.showInputDialog(
                    null,
                    "Please enter level: ",
                    "level",
                    JOptionPane.QUESTION_MESSAGE,
                    ball,
                    null,
                    0
            );
            if (sen == null) {
                System.exit(0);
            }
            try {
                SEN = Integer.parseInt(sen);
                flag = true;
            } catch (NumberFormatException e) {
                invalid();
            } catch (Exception E) {
                flag = true;
            }
        }
        return SEN;
    }
    public static void invalid (){
        JOptionPane.showMessageDialog(null,    "You're choice is invalid! ",
                "Invalid",JOptionPane.WARNING_MESSAGE);
    }
}
