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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons.util;

import java.io.IOException;

/**
 * Provides several ways to run unix commands on different OSes.
 * @author Nils Geilen
 */
public abstract class Shell {

	/** Default Constructor*/
	public Shell(){

	}
	private static Shell nat = new NativeShell();

	/**
	 * a wrapper for the os' native shell
	 * @return  a wrapper for the os' native shell
	 */
	public static Shell getNativeShell(){
		return nat;
	}

	/**
	 * a wrapper around the cygwin shell
	 * @param binary path to bash.exe
	 * @return  a wrapper for the os' native shell
	 */
	public static Shell getCygwinShell(String binary){
		return new CygwinShell(binary);
	}

	/**
	 * runs command
	 * @param cmd the command to be run
	 * @return the terminal output
	 * @throws InterruptedException if some interruption occurred.
	 * @throws IOException if some IO issue occurred.
	 */
	public abstract String run(String cmd) throws InterruptedException, IOException;
}






