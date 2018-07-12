/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
