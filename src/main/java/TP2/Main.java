package TP2;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        World w = new World();
        MainWindow window = new MainWindow(w);
        JFrame frame = new JFrame("Pigeon");
        frame.setContentPane(window.getSwingPanel());
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
        frame.setVisible(true);
    }
}
