package net.sf.tweety.swarms.boids;

import java.awt.*;

public class Obstacle {

    Point p;
    int s; 
    
    public Obstacle(Point p, int s) {
        this.p = p;
        this.s = s;
    }

    public void paint ( Graphics g ) {
        g.setColor(Color.gray);
        g.fillOval(p.x,p.y,s,s);
        g.setColor(Color.white);
    }
    
    public Point getp () {
        return new Point(p.x+s/2, p.y+s/2);
    }
    
    public int getS () {
        return s;
    }
}
