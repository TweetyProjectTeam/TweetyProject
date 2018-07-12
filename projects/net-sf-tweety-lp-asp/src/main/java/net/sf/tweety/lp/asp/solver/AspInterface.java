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
package net.sf.tweety.lp.asp.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides a common interface for asp-solvers. Most implementing
 * classes will simply call an external grounder and solver and return the
 * resulting string.
 * 
 * Taken from the tweety project (http://tweety.sf.net), and only the
 * core process runner is used.
 * 
 * @author Sebastian Homann (Original), Thomas Vengels (Modifications)
 */
public class AspInterface
{
	/** These variables holds the (result or error) of an answer set solver */
	private List<String> outputData = null;
	private List<String> errorData = null;
 
  /**
   * Executes an external application with a given path, a set of parameters and
   * an input string. The stdout of the execution is stored in the 
   * outputData field and the errout is stored in the errorData field.
   * If no error occured during execution, errorData will be "". 
   * <br>This method blocks the working thread until the execution terminates 
   * and all output streams have been emptied.
   * 
   * @param cmd A string array consisting of the path to the executable and a
   *          set of parameters.
   * @param input An input string which is handed over to the executed program.
   * @throws IOException
   */
  public void executeProgram( List<String> cmd, String input )
    throws IOException
  {
	  assert(cmd != null);
	  assert(cmd.size() > 0);
	  String runcmd = "";
	  Iterator<String> cmdIter = cmd.iterator();
	  if (cmd.iterator().hasNext())
		  runcmd = cmdIter.next();
	  while (cmdIter.hasNext())
		  runcmd = runcmd + " " + cmdIter.next();
	
	/*
	ProcessBuilder builder = new ProcessBuilder();
	File wdir = new File(runcmd.substring(0, runcmd.lastIndexOf('/')));
	builder.directory(wdir);
	builder.command(cmd);
	final Process process = builder.start();
	*/
	if (input == null)
		input = "";	
	final Process process = Runtime.getRuntime().exec( runcmd + " " + input);
		
    OutputStream stdin = process.getOutputStream();
    StreamFlusher stdoutFlusher = new StreamFlusher( process.getInputStream() );
    StreamFlusher erroutFlusher = new StreamFlusher( process.getErrorStream() );
    
    try {
    	
    	stdin.flush();
    	stdin.close();
    } catch(IOException ioexec) {
    	// ignore broken pipe and save output.
    }
    
    stdoutFlusher.start();
    erroutFlusher.start();
    int reval = 0;
    try {
      reval = process.waitFor();
    }
    catch ( InterruptedException e ) {
      e.printStackTrace();
    }
    
    this.outputData = stdoutFlusher.getOutput();
    this.errorData = erroutFlusher.getOutput();
    
    if(reval != 0) {
    	if(reval == -1073741515)
    		this.errorData.add("Process did not exit normally: a dll is missing. - To determine which dll try to start '" + cmd.get(0) + "' from the command line and read the error message.");
    	else
    		this.errorData.add("Process did not exit normally: " + reval);
    }
    
  }
  
  public void executeProgram(String... args) throws IOException {
	  assert(args.length > 1);
	  LinkedList<String> cmds = new LinkedList<String>();
	  for (int i = 0; i <= args.length-2; i++)
		  cmds.add(args[i]);
	  executeProgram(cmds, args[args.length-1]);
  }
  
  /**
   * This method returns the output of a previously called
   * answer set solver tools. Only content from the stdout 
   * stream is collected here.
   * 
   * @return output of answer set solver
   */
  public List<String> getOutput() {
	  return this.outputData;
  }
  
  /**
   * This method returns the error output of a previously called
   * answer set solver tools. Only content from the stderr 
   * stream is collected here.
   * 
   * @return error output of answer set solver
   */
  public List<String> getError() {
	  return this.errorData;
  }
  
  /**
   * Auxiliary class for reading output streams concurrently. Modified
   * to return a List<String> instead of a String.
   */
  class StreamFlusher
    extends Thread
  {
    private InputStream is;
    private List<String> output = new LinkedList<String>();
    
    StreamFlusher( InputStream is )
    {
      this.is = is;
    }
    
    public void run()
    {
      try {
        BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
        String line = null;
        while ( ( line = br.readLine() ) != null ) {
          output.add(line);
        }
        br.close();
        is.close();
      }
      catch ( IOException ioe ) {
        ioe.printStackTrace();
      }
    }
    
    public List<String> getOutput()
    {
      try {
        this.join();
      }
      catch ( InterruptedException e ) {
        e.printStackTrace();
      }
      return output;
    }
  }
  
}
