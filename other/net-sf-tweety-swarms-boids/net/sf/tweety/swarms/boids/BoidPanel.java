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
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

class BoidPanel extends JPanel implements MouseListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	private Vector<Boid> boids 	      = new Vector<Boid>();
    private Vector<Obstacle> obs      = new Vector<Obstacle>();
    private Vector<Food> food	      = new Vector<Food>();
    private Vector<Line> lines        = new Vector<Line>();
    private Vector<Pheromone> trail   = new Vector<Pheromone>();
    private Vector<Grave> graves      = new Vector<Grave>();
    BoidSimulation f;
    final int DEFAULT = -Integer.MAX_VALUE;
    private int x1=DEFAULT, y1=DEFAULT, x2=DEFAULT, y2=DEFAULT;
    boolean birth =false;
    
    final int BOIDCOUNT = 200;
    
    public BoidPanel(BoidSimulation f) {
        this.f = f;
    	for(int i=0; i<BOIDCOUNT; i++)
    		boids.add( new Boid(boids, obs, food, lines, trail, i, 2) );
    	   	
    	setBackground(Color.black);
    	setVisible( true );
    	RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
    	addMouseListener(this);
        
        //drawMaze();
    }
    
    //private void drawMaze() {
      //  lines.add(new Line(    0,      0,  1000,   0 ));
      //  lines.add(new Line( 1000,      0,  1000, 710 ));
      //  lines.add(new Line( 1000,    710,     0, 710 ));
      //  lines.add(new Line(    0,    710,   0,0 ));
    //}

    public void paintComponent( Graphics g )
    {
      super.paintComponent(g);
      
      for(int i=0, s=boids.size(); i < s; i++) {
          boids.get(i).think();
          
          // remove the dead and birth
          if (boids.get(i).getDeath()) {
              boolean r = boids.get(i).getRace();
              graves.add(new Grave(boids.get(i).getP(), boids.get(i).getRace()));
              boids.remove(boids.get(i)); i--; s--;
              if (birth) 
                  boids.add( new Boid(boids, obs, food, lines, trail, boids.size(), r?1:0) );
          }
      }
      
      for( ListIterator<Grave> it = graves.listIterator(); it.hasNext(); ) 
          it.next().paint(g);
      
      for( int i=0, s=trail.size(); i < s; i++ ) {
          if ( trail.get(i).getS() == 0 ){
              trail.remove(trail.get(i));
              i--; s--;
          }
          else {
              trail.get(i).decrease();
              trail.get(i).paint(g);
          }
      }
      
      for( ListIterator<Line> it        = lines.listIterator(); it.hasNext(); ) 
          it.next().paint(g);
      
      for( ListIterator<Food> it        = food.listIterator(); it.hasNext(); )
          it.next().paint(g);
            
      for( ListIterator<Obstacle> it    = obs.listIterator(); it.hasNext(); ) 
          it.next().paint(g);
            
      for(int i=0; i<boids.size(); i++)
          boids.get(i).paint(g);
    }  
    
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) 
            food.add(new Food( new Point(e.getX(), e.getY()), 20 ));
        if (e.getButton() == MouseEvent.BUTTON2) 
            obs.add(new Obstacle( new Point(e.getX(), e.getY()), 20 ));
        if (e.getButton() == MouseEvent.BUTTON1)
            if ( x1==DEFAULT ) {
                x1 = e.getX(); y1 = e.getY();}
            else {
                x2 = e.getX(); y2 = e.getY();
                lines.add( new Line(x1, y1, x2, y2) );
                x1=DEFAULT; y1=DEFAULT; x2=DEFAULT; y2=DEFAULT;
            }
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }
    
    public void keyPressed(KeyEvent e) {
        switch ( e.getKeyChar() ) {
        case 'q': food.remove(food.lastElement());  break;
        case 'a': obs.remove(obs.lastElement());    break;
        case 'z': lines.remove(lines.lastElement());break;
        case 'b': birth = birth ? false:true; System.out.println("Birth set to: "+birth); break;
        case 's': 
            int green=0;
            for (int i=0; i<graves.size(); i++) 
                if (graves.get(i).getRace()) green++;
            System.out.println();
            System.out.println("*** Death Statistics ***");
            System.out.println("Total death: " + graves.size() + " Race green: "+green+" Race blue: "+(graves.size()-green));
            System.out.println();
            break;
        case KeyEvent.VK_SPACE : f.toggleTimer();   break;
        case KeyEvent.VK_ESCAPE: x1=DEFAULT; y1=DEFAULT; System.out.println("Line canceled");
        }
    }
    
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
	}
}
