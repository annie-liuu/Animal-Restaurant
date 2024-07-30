//Annie Liu, Esraa Kandil
//Tips
//stores information for the tips class, how many coins are in it, drawing when there are tips to collect, deposting and withdrawing coins

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Tips {
    private static int tips;//total tips in the tip jar
    private Image exclamationPoint;//image to show there are tips to collect
    
    //takes in parameter i for amount of tips currently in the jar
    public Tips(int i){
        tips = i;
        exclamationPoint = new ImageIcon("other/point.png").getImage();
    }

    public void deposit(int d){//adds deposit to total
        tips += d;
    }
    public int withdraw(){//returns amount of tips in jar and sets total to 0
        int amount = tips;
        tips = 0;
       return amount; 
    }
    public int getTotal(){
        return tips;
    }
    public void draw(Graphics g){//if there are tips then it draws exclamation to let user know
        if (tips>0){
            g.drawImage(exclamationPoint, 261 + GamePanel.offset, 199, null);
        }
    }
}
