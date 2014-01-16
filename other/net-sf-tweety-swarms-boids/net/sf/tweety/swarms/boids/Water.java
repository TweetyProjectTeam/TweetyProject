/*
 * Created on 20/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.tweety.swarms.boids;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 * @author pkrumpel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Water extends Food {

    /**
     * @param p
     * @param s
     */
    public Water(Point p, int s) {
        super(p, s);
        // TODO Auto-generated constructor stub
    }

    public void paint ( Graphics g ) {
        g.setColor(Color.blue);
        g.fillOval(p.x,p.y,s,s);
        g.setColor(Color.white);
    }
}
