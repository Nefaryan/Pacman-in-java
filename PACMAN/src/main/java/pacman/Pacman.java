package pacman;

import javax.swing.*;

public class Pacman extends JFrame {
    public Pacman(){
        add(new Model());
    }

    public static void main(String[] args) {
        Pacman pac = new Pacman();
        pac.setVisible(true);
        pac.setTitle("PACMAN");
        pac.setSize(600,600);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);
        System.out.println("_______________________________");


    }
}
