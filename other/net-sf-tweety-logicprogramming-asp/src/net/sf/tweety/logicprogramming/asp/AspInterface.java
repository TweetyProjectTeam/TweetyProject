/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logicprogramming.asp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import net.sf.tweety.util.Pair;

/**
 * This class provides a common interface for asp-solvers. Most implementing
 * classes will simply call an external grounder and solver and return the
 * resulting string.
 * 
 * @author Sebastian Homann
 */
public abstract class AspInterface
{
  /**
   * Calculate all answer sets of the given extended logic program.
   * 
   * @param program
   * @return all answer sets of the given extended logic program, or null if
   *         program is unsatisfiable.
   * @throws Exception may be thrown when the program is illshaped or an
   *           external asp solver causes a failure of some kind.
   */
  public abstract String[] calculateAnswerSets( String program )
    throws IOException;
  
  /**
   * Calculate a target number of answer sets of the given extended logic
   * program, if they exist.
   * 
   * @param program
   * @param maximumNumberOfAnswerSets the maximum number of answer sets, that
   *          will be calculated.
   * @return maximumNumberOfAnswerSets many answer sets of the given extended
   *         logic program or null if program is unsatisifable.
   * @throws Exception may be thrown when the program is illshaped or an
   *           external asp solver causes a failure of some kind.
   */
  public abstract String[] calculateAnswerSets( String program,
    int maximumNumberOfAnswerSets )
    throws IOException;
  
  /**
   * Calculates whether a given program is satisfiable or not.
   * 
   * @param program
   * @return true iff the given program is satisfiable.
   * @throws Exception may be thrown when the program is illshaped or an
   *           external asp solver causes a failure of some kind.
   */
  public abstract boolean calculateSatisfiable( String program )
    throws IOException;
  
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
   * @return a pair of strings, where the first element equals to the stdout
   *         read during execution and the second element equals the errout read
   *         during execution.
   * @throws IOException
   */
  public Pair< String, String > executeProgram( String[] cmd, String input )
    throws IOException
  {
    final Process process = Runtime.getRuntime().exec( cmd );
    
    OutputStream stdin = process.getOutputStream();
    StreamFlusher stdoutFlusher = new StreamFlusher( process.getInputStream() );
    StreamFlusher erroutFlusher = new StreamFlusher( process.getErrorStream() );
    
    stdin.write( input.getBytes() );
    stdin.flush();
    stdin.close();
    
    stdoutFlusher.start();
    erroutFlusher.start();
    try {
      process.waitFor();
    }
    catch ( InterruptedException e ) {
      e.printStackTrace();
    }
    
    return new Pair< String, String >( stdoutFlusher.getOutput(), erroutFlusher
      .getOutput() );
  }
  
  class StreamFlusher
    extends Thread
  {
    private InputStream is;
    private String output = "";
    
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
          output += line + "\n";
        }
        br.close();
        is.close();
      }
      catch ( IOException ioe ) {
        ioe.printStackTrace();
      }
    }
    
    public String getOutput()
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
