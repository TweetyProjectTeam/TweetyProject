/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A class for managing and displaying several plots in a single frame
 * @author Benedikt Knopp
 */
public class Plotter {

	/**
	 * The frame where the model is drawn
	 */
	private JFrame frame;
	/**
	 * The main panel in the frame
	 */
	private JPanel mainPanel;
	
	/**
	 * Create a new instance
	 */
	public Plotter() {
	}
	
	/**
	 * Create a new main frame with specific proportions
	 * @param frameWidth the width of the frame
	 * @param frameHeight the height of the frame
	 */
	public void createFrame(int frameWidth, int frameHeight) {
		this.frame = new JFrame();
		this.mainPanel = new JPanel();
        this.frame.setSize(frameWidth, frameHeight);	
        
	}
	
	/**
	 * Show the frame after adding some plots
	 */
	public void show() {
		this.frame.add(mainPanel);
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Add a sub-plot the the frame
	 * @param panel the panel containing the sub-plot
	 */
	public void add(JPanel panel) {
		this.mainPanel.add(panel);
	}

	/**
	 * Get the horizontal gap between panels
	 * @return the horizontal gap
	 */
	public int getHGap() {
		return 20;
	}

	/**
	 * Get the vertical gap between panels
	 * @return the vertical gap
	 */
	public int getVGap() {
		return 20;
	}
	
	/**
	 * Add some description to the frame
	 * @param labels a list of strings that will be aligned vertically
	 */
	public void addLabels(List<String> labels) {
		String labelHTML = "<html>";
		for(String label : labels ) {
			labelHTML += label + "<br>";
		}
		labelHTML += "</html>";
		this.mainPanel.add( new JLabel(labelHTML), FlowLayout.LEFT );
	}

	
}
