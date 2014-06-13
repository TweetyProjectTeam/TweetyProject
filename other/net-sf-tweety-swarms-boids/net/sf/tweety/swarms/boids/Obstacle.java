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
