package TP2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrawPanel extends JPanel {
    private World world;

    public DrawPanel(World world) {
        this.world = world;
        new Timer(30, actionEvent -> {
            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);
        world.render(g);
    }
}
