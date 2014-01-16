package net.sf.tweety.swarms.boids;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Grave extends Point {

	private static final long serialVersionUID = 1L;
	boolean r;
    char[] c = {'X'};
    
    public Grave(Point p, boolean r) {
        super(p);
        this.r = r;
        // TODO Auto-generated constructor stub
    }

    public Grave(int x, int y, boolean r) {
        super(x, y);
        this.r = r;
        // TODO Auto-generated constructor stub
    }

    public void paint ( Graphics g ) {
        g.setColor(r ? new Color(0,80,0) : new Color(0,0,80) );
        g.drawChars(c,0,1,x,y);
        g.setColor(Color.white);
    }
    
    public boolean getRace() {
        return r;
    }
}
