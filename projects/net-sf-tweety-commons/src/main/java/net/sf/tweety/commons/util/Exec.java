package net.sf.tweety.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class contains static methods for accessing other executable files.
 * @author Matthias Thimm
 */
public class Exec {

	/**
	 * Executes the given command on the commandline and returns the complete output.
	 * @param commandline some command
	 * @return the output of the execution
	 * @throws IOException of an error was encountered.
	 * @throws InterruptedException 
	 */
	public static String invokeExecutable(String commandline) throws IOException, InterruptedException{
		return Exec.invokeExecutable(commandline, -1);
	}
	
	/**
	 * Executes the given command on the commandline and returns the output up to a given number of lines.
	 * @param commandline some command
	 * @param maxLines the maximum number of lines to be read (the process is killed afterwards)
	 * @return the output of the execution
	 * @throws IOException of an error was encountered.
	 * @throws InterruptedException 
	 */
	public static String invokeExecutable(String commandline, long maxLines) throws IOException, InterruptedException{
		Process child = Runtime.getRuntime().exec(commandline);
		//child.waitFor();
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
			error.trim();
			if(!error.equals(""))
				throw new IOException(error);
		}
		return output;
	}
}
