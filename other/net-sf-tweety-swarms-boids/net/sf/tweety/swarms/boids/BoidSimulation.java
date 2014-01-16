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
