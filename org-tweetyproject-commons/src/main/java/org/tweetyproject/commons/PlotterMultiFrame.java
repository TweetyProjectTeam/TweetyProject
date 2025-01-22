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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * This class is responsible for the behavior of a plotter with multiple frames.
 * Closing one frame shall only exit the application, iff it was the last frame.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class PlotterMultiFrame extends Plotter {
	private static int s_openedFrames = 0;

	/** Default Constructor*/
	public PlotterMultiFrame(){
		super();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createFrame(int frameWidth, int frameHeight) {
		super.createFrame(frameWidth, frameHeight);


		this.frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				s_openedFrames--;
				if(s_openedFrames == 0) {
					System.exit(0);
				}
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}

	@Override
	public void show() {
		super.show();
		s_openedFrames++;
		this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
