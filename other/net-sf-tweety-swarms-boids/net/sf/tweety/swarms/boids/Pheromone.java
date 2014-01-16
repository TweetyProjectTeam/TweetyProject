package net.sf.tweety.swarms.boids;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

public class Pheromone extends Obstacle {
    
    private Random generator = new Random();
    int lifetime, time;
    int distance    = 0;
    
    public Pheromone(Point p, int distance, int lifetimeOffset) {
        super(p, 3);
        this.distance = distance;
        lifetime = generator.nextInt(100) + lifetimeOffset;
        time = lifetime;
        // TODO Auto-generated constructor stub
    }
    
    public void decrease () {
        time--;
        s = Math.round(Math.round( ( time*3/lifetime) +0.5 ));       
    }
    
    public void paint ( Graphics g ) {;
        g.setColor( new Color(Math.max(Math.round(255-distance/2), 50),0,0) );
        g.fillOval(p.x,p.y,s,s);
        g.setColor(Color.white);
    }
    
    public int getDistance() {
        return distance;
    }

}