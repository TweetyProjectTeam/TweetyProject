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
package net.sf.tweety.action.transitionsystem;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.action.signature.FolFluentName;
import net.sf.tweety.logics.fol.syntax.FolAtom;

/**
 * Represents a state in an action transition system, which is a representation
 * of an interpretation of all fluent names in an action description.
 * 
 * @author Sebastian Homann
 */
public class State
{
  private Set< FolAtom > fluents = new HashSet< FolAtom >();
  
  /**
   * Creates a new State with a set of fluents that are mapped to true.
   * 
   * @param fluents The fluents which are mapped to true by this state.
   */
  public State( Set< FolAtom > fluents )
  {
    this.fluents.addAll( fluents );
  }
  
  /**
   * Returns true iff the fluent given is mapped to true by this state.
   * 
   * @param fluent
   * @return true iff the fluent given is mapped to true by this state.
   */
  public boolean isMappedToTrue( FolAtom fluent )
  {
    if ( fluent.getPredicate() instanceof FolFluentName )
      return fluents.contains( fluent );
    return false;
  }
  
  /**
   * Returns the set of fluent atoms that are mapped to true by this state.
   */
  public Set< FolAtom > getPositiveFluents()
  {
    return new HashSet< FolAtom >( fluents );
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    return fluents.equals( obj );
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return fluents.hashCode();
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return fluents.toString();
  }
}
