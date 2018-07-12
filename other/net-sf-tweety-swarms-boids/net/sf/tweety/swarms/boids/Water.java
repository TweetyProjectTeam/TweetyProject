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
