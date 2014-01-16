package net.sf.tweety.logicprogramming.asp;

import java.io.IOException;
import java.util.Arrays;

import net.sf.tweety.util.Pair;

/**
 * This class provides access to the clasp answer set solver and the
 * lparse-equivalent grounder gringo.
 * 
 * @author Sebastian Homann
 */
public class ClaspInterface
  extends AspInterface
{
  private String pathToGrounder = "./gringo";
  private String pathToClasp = "./clasp";
  
  /**
   * Creates a new interface to the clasp answer set solver, which expects the
   * clasp binary as well as the gringo binary by their default names in the
   * active working directory.
   */
  public ClaspInterface()
  {
  }
  
  /**
   * Creates a new interface to the clasp answer set solver.
   * 
   * @param pathToGrounder The absolute path to a clasp-compatible grounder,
   *          e.g. gringo, lparse.
   * @param pathToClasp The absolute path to the clasp binary.
   */
  public ClaspInterface( String pathToGrounder, String pathToClasp )
  {
    this.pathToClasp = pathToClasp;
    this.pathToGrounder = pathToGrounder;
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.asp.AspInterface#calculateAnswerSets(java.lang.String)
   */
  @Override
  public String[] calculateAnswerSets( String program )
    throws IOException
  {
    return calculateAnswerSets( program, 0 );
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.asp.AspInterface#calculateAnswerSets(java.lang.String,
   * int)
   */
  @Override
  public String[] calculateAnswerSets( String program,
    int maximumNumberOfAnswerSets )
    throws IOException
  {
    String[] cmd =
      { pathToClasp, Integer.toString( maximumNumberOfAnswerSets ),
          "--verbose=0" };
    Pair< String, String > result =
      this.executeProgram( cmd, ground( program ) );
    if ( !result.getSecond().trim().equals( "" ) &&
      !result.getSecond().toLowerCase().contains( "warning:" ) )
      throw new AspException( result.getSecond() );
    String[] r = result.getFirst().split( "\r\n|\r|\n" );
    if ( r[r.length - 1].trim().equals( "UNSATISFIABLE" ) )
      return null;
    return Arrays.copyOfRange( r, 0, r.length - 1 );
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.asp.AspInterface#calculateSatisfiable(java.lang.String)
   */
  @Override
  public boolean calculateSatisfiable( String program )
    throws IOException
  {
    String[] cmd = { pathToClasp, "--verbose=0", "-q" };
    Pair< String, String > result =
      this.executeProgram( cmd, ground( program ) );
    if ( !result.getSecond().trim().equals( "" ) &&
      !result.getSecond().toLowerCase().contains( "warning:" ) )
      throw new AspException( result.getSecond() );
    if ( result.getFirst().contains( "UNSATISFIABLE" ) )
      return false;
    return true;
  }
  
  /**
   * Uses an external lparse compatible grounder to calculate all grounded
   * instances of the given extended logic program.
   * 
   * @param program
   * @return the lparse compatible grounded program
   * @throws IOException
   */
  public String ground( String program )
    throws IOException
  {
    String[] cmd = { pathToGrounder };
    Pair< String, String > result = this.executeProgram( cmd, program );
    if ( !result.getSecond().trim().equals( "" ) &&
      !result.getSecond().toLowerCase().contains( "warning:" ) )
      throw new AspException( result.getSecond() );
    return result.getFirst();
  }
}
