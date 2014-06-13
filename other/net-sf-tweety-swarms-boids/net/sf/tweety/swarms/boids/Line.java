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
import java.awt.geom.Line2D;

public class Line {

        int x1, y1, x2, y2; 
        
        public Line (int x1, int y1, int x2, int y2) {
            this.x1 =x1;
            this.y1 =y1;
            this.x2 =x2;
            this.y2 =y2;
        }

        public void paint ( Graphics g ) {
            g.setColor(Color.gray);
            g.drawLine(x1, y1, x2, y2);
        }
        
        public int getDistance(int x, int y) {
           return Math.round(Math.round( Line2D.ptSegDist(x1, y1, x2, y2, x, y) ));
        }
        
        public Point getNormV( int x, int y) {
            Point n = new Point(y2-y1 ,-(x2-x1));
            return( BV.scalarMult( (BV.vectorAngle(x-x1,y-y1)>BV.vectorAngle(x2-x1, y2-y1)? 1 : -1)/BV.norm(n), n) );
        }
}
