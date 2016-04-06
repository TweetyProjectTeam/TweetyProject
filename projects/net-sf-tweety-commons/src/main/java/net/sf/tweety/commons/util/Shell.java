/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.util;

import java.io.IOException;

/**
 * @author Nils Geilen
 * Provides several ways to run unix commands on different OSes.
 */
public abstract class Shell {
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
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public abstract String run(String cmd) throws InterruptedException, IOException;
}






