/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
