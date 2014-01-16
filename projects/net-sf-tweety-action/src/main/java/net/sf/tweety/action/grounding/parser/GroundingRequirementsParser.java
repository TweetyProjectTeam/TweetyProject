package net.sf.tweety.action.grounding.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.ParserException;
import net.sf.tweety.action.grounding.GroundingRequirement;
import net.sf.tweety.action.grounding.VarConstNeqRequirement;
import net.sf.tweety.action.grounding.VarsNeqRequirement;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This class parses a list of grounding requirements of the form 
 *   REQUIREMENT ("," REQUIREMENT)*
 * 
 * @author Sebastian Homann
 */
public class GroundingRequirementsParser
{
  
  /**
   * Parses a string of the following form: REQUIREMENT ("," REQUIREMENT)*
   * 
   * @param s A set of variables which are allowed in these requirements.
   * @return The set of grounding requirements parsed from the given string.
   */
  public Set< GroundingRequirement > parseRequirements( String s,
    Set< Variable > variables )
    throws ParserException
  {
    Set< GroundingRequirement > result = new HashSet< GroundingRequirement >();
    if ( s == null || s.trim().equals( "" ) )
      return result;
    String[] reqs = s.split( "," );
    for ( String token : reqs ) {
      token = token.trim();
      result.add( parseRequirement( s, variables ) );
    }
    return result;
  }
  
  /**
   * Parses a string of the following form: 
   *   (VARIABLENAME "<>" VARIABLENAME | VARIABLENAME "<>" CONSTANTNAME) 
   * where the constant CONSTANTNAME has to be of the same sort as the 
   * variable VARIABLENAME.
   * 
   * @param s a string containing a single requirement
   * @return the parsed grounding requirement.
   * @throws ParserException
   */
  public GroundingRequirement parseRequirement( String s,
    Set< Variable > variables )
    throws ParserException
  {
    String[] params = s.split( "<>" );
    if ( params.length != 2 )
      throw new ParserException( "Missing '<>' in grounding requirement." );
    String par1 = params[0].trim();
    String par2 = params[1].trim();
    Variable var1 = null;
    Variable var2 = null;
    for ( Variable v : variables ) {
      if ( v.get().equals( par1 ) )
        var1 = v;
      else if ( v.get().equals( par2 ) )
        var2 = v;
    }
    if ( var1 == null && var2 == null )
      throw new ParserException( "Missing variable in grounding requirement." );
    if ( var1 == null ) {
      Constant constant = getConstant( par1, var2.getSort() );
      if ( constant == null )
        throw new ParserException( "Element '" + par1 +
          "' in grounding requirement has to be a constant of sort '" +
          var2.getSort().toString() + "'." );
      return new VarConstNeqRequirement( var2, constant );
    }
    else if ( var2 == null ) {
      Constant constant = getConstant( par2, var1.getSort() );
      if ( constant == null )
        throw new ParserException( "Element '" + par2 +
          "' in grounding requirement has to be a constant of sort '" +
          var1.getSort().toString() + "'." );
      return new VarConstNeqRequirement( var1, constant );
    }
    else
      return new VarsNeqRequirement( var1, var2 );
  }
  
  /**
   * Returns the constant with the name 'name' of the sort 'sort', if one
   * exists, null otherwise.
   * 
   * @param name some constant name represented by a string
   * @param sort the sort of that constant
   * @return The constant with the name 'name' of the sort 'sort', if one
   *         exists, null otherwise.
   */
  private Constant getConstant( String name, Sort sort )
  {
    for ( Constant c : sort.getTerms(Constant.class) ) {
      if ( c.get() == name)
        return c;
    }
    return null;
  }
}
