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
package net.sf.tweety.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Default shell
 * @author Nils Geilen, Matthias Thimm
 *
 */
public class NativeShell extends Shell {
	
	NativeShell(){}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.Shell#run(java.lang.String)
	 */
	@Override
	public String run(String cmd) throws InterruptedException, IOException {
		 return NativeShell.invokeExecutable(cmd, -1, true);
	}
	
	/**
	 * Executes the given command on the commandline and returns the complete output.
	 * @param commandline some command
	 * @return the output of the execution
	 * @throws IOException of an error was encountered.
	 * @throws InterruptedException 
	 */
	public static String invokeExecutable(String commandline) throws IOException, InterruptedException{
		return NativeShell.invokeExecutable(commandline, -1);
	}
	
	public static String invokeExecutable(String commandline, long maxLines) throws IOException, InterruptedException{
		return NativeShell.invokeExecutable(commandline, maxLines, false);
	}
	
	/**
	 * Executes the given command on the commandline and returns the output up to a given number of lines.
	 * @param commandline some command
	 * @param maxLines the maximum number of lines to be read (the process is killed afterwards)
	 * @param suppressErrors if set to true, possible errors will not be included in the output
	 * @return the output of the execution
	 * @throws IOException of an error was encountered.
	 * @throws InterruptedException 
	 */
	public static String invokeExecutable(String commandline, long maxLines, boolean suppressErrors) throws IOException, InterruptedException{
		Process child = Runtime.getRuntime().exec(commandline);
		String output = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
		String line = "";
		long lines = 0;
		
		while((line = reader.readLine())!= null) {
			output += line + "\n";
			lines++;
			if(maxLines != -1 && lines >= maxLines)
				break;
		}
		reader.close();
		
		// check for errors (only if we did not exhaust max lines)
		if(maxLines == -1 || lines < maxLines){
			reader = new BufferedReader(new InputStreamReader(child.getErrorStream())); 
			line = "";		
			String error = "";
			while((line = reader.readLine())!= null) {
				error += line + "\n";
			}
			reader.close();
			child.destroy();
			child.waitFor();
			error.trim();
			if(!suppressErrors && !error.equals("")) 
				throw new IOException(error); 
		}
		return output;
	}
	
}