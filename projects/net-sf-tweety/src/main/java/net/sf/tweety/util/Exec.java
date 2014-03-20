package net.sf.tweety.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class contains static methods for accessing other executable files.
 * @author Matthias Thimm
 */
public class Exec {

	/**
	 * Executes the given command on the commandline and returns the output.
	 * @param commandline some command
	 * @return the output of the execution
	 * @throws IOException of an error was encountered.
	 * @throws InterruptedException 
	 */
	public static String invokeExecutable(String commandline) throws IOException, InterruptedException{
		Process child = Runtime.getRuntime().exec(commandline);
		child.waitFor();
		String output = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
		String line = "";			
		while((line = reader.readLine())!= null) {
			output += line + "\n";
		}
		reader.close();
		// check for errors
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
		return output;
	}
}
