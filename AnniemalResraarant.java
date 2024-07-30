// Annie Liu, Esraa Kandil
// AnniemalResraarant
// RPG resturant simulation game

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;

class AnniemalResraarant extends JFrame{
    public AnniemalResraarant(){
        super("Anniemal Resraarant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        pack();
        setVisible(true);
    }
    public static void main(String []args){
        AnniemalResraarant game = new AnniemalResraarant();
    }
}