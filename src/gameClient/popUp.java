package gameClient;
import javax.swing.*;

public class popUp {
    /**
     * the function creates the first pop up window of the id request.
     * It creats a JOptionPane as the pop up that returns a String that then
     * will be cast to an integer and returned in the end. Also check if the input is valid- if not send an error.
     * @return
     */
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
                    207603978
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

    /**
     *  the function creates the second pop up window of the scenario request.
     *  It creates a JOptionPane as the pop up that returns a String
     *  that then will be cast to an integer and returned in the end.
     *  Also check if the input is valid- if not send an error.
     * @return
     */
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
