package TP2;

import java.awt.*;

public class Food implements Entity {
    private Point position;

    public Food(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void render(Graphics g) {
        g.setColor(Color.ORANGE);
        int r = 5;
        g.fillOval(position.x-r, position.y-r, 2*r+1, 2*r+1);
    }
}
