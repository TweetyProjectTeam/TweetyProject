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

import java.util.Map;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This class represents a single grounding requirement stating that a variable
 * is not allowed to be substituted by a specific constant.
 * 
 * @author Sebastian Homann
 */
public class VarConstNeqRequirement
  implements GroundingRequirement
{
  
  private Variable variable;
  private Constant constant;
  
  /**
   * Creates a new grounding requirement expressing, that the variable given may
   * not be set to the specific constant.
   * 
   * @param variable a variable.
   * @param constant a constant.
   */
  public VarConstNeqRequirement( Variable variable, Constant constant )
  {
    if ( variable == null || constant == null ) {
      throw new NullPointerException();
    }
    this.variable = variable;
    this.constant = constant;
  }
  
  /*
   * (non-Javadoc)
   * @see
   * net.sf.tweety.action.desc.c.syntax.GroundingRequirement#isValid(java.util
   * .Map)
   */
  @Override
  public boolean isValid( Map< Variable, Constant > assignment )
  {
    Constant a = assignment.get( variable );
    if ( a == null || constant == null || !constant.equals( a ) )
      return true;
    return false;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    VarConstNeqRequirement other = (VarConstNeqRequirement) obj;
    if ( !variable.equals( other.variable ) )
      return false;
    if ( !constant.equals( other.constant ) )
      return false;
    return true;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( variable == null ) ? 0 : variable.hashCode() );
    result = prime * result + ( ( constant == null ) ? 0 : constant.hashCode() );
    return result;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return variable.get() + "<>" + constant.get();
  }
}
