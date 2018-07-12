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
import javax.swing.*;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;

public class BoidSimulation extends JFrame implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private static Timer t;
	private static BoidSimulation f;
	
	public static void main( String args[] )
	{
		f = new BoidSimulation(1000,710,"Patrick's Boids");
		t = new javax.swing.Timer(80, f);
		BoidPanel p = new BoidPanel(f);
		f.getContentPane().add(p);
		f.addKeyListener(p);
		f.repaint();
		t.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		f.repaint();
    }
	
	public void toggleTimer() {
	    if (t.isRunning()) t.stop();
	    else t.start();
	}
	
	public BoidSimulation( int x, int y, String title)
	{
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setVisible( true );
		setSize( x, y );
		setTitle(title);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( (d.width - getSize().width ) / 2,
                	 (d.height - getSize().height) / 2 );
	}
}
