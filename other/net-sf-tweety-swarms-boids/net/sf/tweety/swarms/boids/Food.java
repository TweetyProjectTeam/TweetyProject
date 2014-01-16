package net.sf.tweety.swarms.boids;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;

public class Food extends Obstacle {
    
    int biteCount = 0;
    
    public Food(Point p, int s) {
        super(p, s);
        // TODO Auto-generated constructor stub
    }

    public void decrease (int ds) {
        if (s>0)
            if ( ++biteCount > 50 ) {
                s -= ds;
                biteCount = 0;
            }
    }
    
    public void paint ( Graphics g ) {
        g.setColor(Color.orange);
        g.fillOval(p.x,p.y,s,s);
        g.setColor(Color.white);
    }

}
