/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
package net.sf.tweety.action.grounding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.util.MapTools;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This class holds common grounding functionality for causal theories using
 * first-order formulas.
 * 
 * @author Sebastian Homann
 */
public class GroundingTools
{
  /**
   * Checks a grounding application for compliance with the grounding
   * requirements.
   * 
   * @param map
   * @return true, if map is a valid application of constants to variables.
   */
  public static boolean isValidGroundingApplication(
    Map< Variable, Constant > map, Set< GroundingRequirement > requirements )
  {
    for ( GroundingRequirement req : requirements ) {
      if ( !req.isValid( map ) )
        return false;
    }
    return true;
  }
  
  /**
   * Calculates all possible substitutions of variables for a given set of
   * constants
   * 
   * @param variables The set of variables to be substituted.
   * @param constants The set of constants to be used as substitution for
   *          variables.
   * @return A set of maps of possible substitutions.
   */
  public static Set< Map< Variable, Constant >> getAllSubstitutions(
    Set< Variable > variables, Set< Constant > constants )
  {
    //partition variables by sorts
    Map< Sort, Set< Variable >> sorts_variables =
      new HashMap< Sort, Set< Variable >>();
    for ( Variable v : variables ) {
      if ( !sorts_variables.containsKey( v.getSort() ) )
        sorts_variables.put( v.getSort(), new HashSet< Variable >() );
      sorts_variables.get( v.getSort() ).add( v );
    }
    //partition constants by sorts
    Map< Sort, Set< Constant >> sorts_consts =
      new HashMap< Sort, Set< Constant >>();
    for ( Constant c : constants ) {
      if ( !sorts_consts.containsKey( c.getSort() ) )
        sorts_consts.put( c.getSort(), new HashSet< Constant >() );
      sorts_consts.get( c.getSort() ).add( c );
    }
    //combine the partitions
    Map< Set< Variable >, Set< Constant >> relations =
      new HashMap< Set< Variable >, Set< Constant >>();
    for ( Sort s : sorts_variables.keySet() ) {
      if ( !sorts_consts.containsKey( s ) )
        throw new IllegalArgumentException( "There is no constant of sort " +
          s + " to substitute." );
      relations.put( sorts_variables.get( s ), sorts_consts.get( s ) );
    }
    return new MapTools< Variable, Constant >().allMaps( relations );
  }
  
  /**
   * Calculates all possible substitutions in the given set of variables using
   * all possible constants of the same sort.
   * 
   * @param variables The set of variables to be substituted.
   * @return A set of maps of possible substitutions.
   */
  public static Set< Map< Variable, Constant >> getAllSubstitutions(
    Set< Variable > variables )
  {
    //partition variables by sorts
    Map< Sort, Set< Variable >> sorts_variables =
      new HashMap< Sort, Set< Variable >>();
    for ( Variable v : variables ) {
      if ( !sorts_variables.containsKey( v.getSort() ) )
        sorts_variables.put( v.getSort(), new HashSet< Variable >() );
      sorts_variables.get( v.getSort() ).add( v );
    }
    //combine the partitions
    Map< Set< Variable >, Set< Constant >> relations =
      new HashMap< Set< Variable >, Set< Constant >>();
    for ( Sort s : sorts_variables.keySet() ) {
      if ( !s.getTerms(Constant.class).isEmpty() )
        relations.put( sorts_variables.get( s ), s.getTerms(Constant.class));
    }
    return new MapTools< Variable, Constant >().allMaps( relations );
  }
}
