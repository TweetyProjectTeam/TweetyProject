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
package net.sf.tweety.action.signature;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolAtom;

/**
 * An action is a truth-valued function on the set of action names, which is
 * denoted by the set of actions which are mapped to 'true'.
 * 
 * @author Sebastian Homann
 */
public class FolAction
  implements Iterable< FolAtom >, ActionName
{
  private Set< FolAtom > actionNames = new HashSet< FolAtom >();
  
  /**
   * Creates a new action, which maps all action names to false.
   */
  public FolAction()
  {
  }
  
  /**
   * Creates a new action, which maps the given action names to true, and every
   * thing else to false.
   * 
   * @param actionNames a set of action names which are mapped to true by this
   *          action.
   */
  public FolAction( Set< FolAtom > actionNames )
  {
    for ( FolAtom a : actionNames ) {
      if ( !( a.getPredicate() instanceof FolActionName ) )
        throw new IllegalArgumentException(
          "actionNames has to be a set of atoms with an ActionName predicate." );
    }
    this.actionNames.addAll( actionNames );
  }
  
  /**
   * Returns all atoms, e.g. all action names, that are mapped to true by this
   * action.
   * 
   * @return the set of atoms, that are contained in this action.
   */
  public Set< FolAtom > getAtoms()
  {
    Set< FolAtom > result = new HashSet< FolAtom >();
    result.addAll( actionNames );
    return result;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return actionNames.toString().replaceAll( "\\[", "\\{" ).replaceAll( "\\]",
      "\\}" );
  }
  
  /**
   * Substitutes terms in all non-zero-arity atoms in this action according to
   * the given map. This is used for grounding, where variables are substituted
   * by constants.
   * 
   * @param map a map, containing all substitutions to be applied.
   * @return the resulting action.
   */
  public FolAction substitute( Map< ? extends Term<?>, ? extends Term<?> > map )
  {
    Set< FolAtom > result = new HashSet< FolAtom >();
    for ( FolAtom a : actionNames )
      result.add( (FolAtom) a.substitute( map ) );
    return new FolAction( result );
  }
  
  /**
   * Checks whether this action is ground or not. An action is ground when all
   * action names are ground.
   * 
   * @return true iff this action is ground
   */
  public boolean isGround()
  {
    for ( FolAtom a : actionNames )
      if ( !a.isGround() )
        return false;
    return true;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Iterable#iteratOor()
   */
  @Override
  public Iterator< FolAtom > iterator()
  {
    return Collections.unmodifiableCollection( actionNames ).iterator();
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.action.ActionName#getName()
   */
  @Override
  public String getName()
  {
    return this.toString();
  }
}
