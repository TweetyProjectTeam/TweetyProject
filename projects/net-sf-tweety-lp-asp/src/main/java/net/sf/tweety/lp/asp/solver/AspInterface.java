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
	/** these variables holds the (result or error) of an answer set solver */
	List<String> outputData = null;
	List<String> errorData = null;
 
  /**
   * Executes an external application with a given path, a set of parameters and
   * an input string. Returns a pair of strings where the first element is equal
   * to the stdout during exection, and the second element is errout which
   * equals "" when no error occured during execution. This method blocks the
   * working thread until the execution terminates and all output streams have
   * been emptied.
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
    final Process process = Runtime.getRuntime().exec( runcmd );
	
    OutputStream stdin = process.getOutputStream();
    StreamFlusher stdoutFlusher = new StreamFlusher( process.getInputStream() );
    StreamFlusher erroutFlusher = new StreamFlusher( process.getErrorStream() );
    
    try {
    	if (input != null)
    		stdin.write( input.getBytes() );
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
    		this.errorData.add("process did not exit normally: a dll is missing. - To determine which dll try to start '" + cmd.get(0) + "' from the command line and read the error message.");
    	else
    		this.errorData.add("process did not exit normally: " + reval);
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
   * this method returns the output of a previously called
   * answer set solver tools. only content from the stdout 
   * stream is collected here.
   * 
   * @return output of answer set solver
   */
  public List<String> getOutput() {
	  return this.outputData;
  }
  
  public List<String> getError() {
	  return this.errorData;
  }
  
  /**
   * auxiliary class for reading output streams concurrently. modified
   * to return a List<String> instead of a String
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
