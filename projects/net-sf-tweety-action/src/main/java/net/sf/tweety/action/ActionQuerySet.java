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
package net.sf.tweety.action;

import java.util.Collection;

import net.sf.tweety.action.ActionQuery;
import net.sf.tweety.commons.BeliefSet;

/**
 * An Action Query Set consists of action queries in a specific query language
 * and provides some common functionalities for such queries.
 * 
 * @author Sebastian Homann
 * @param <T>
 */
public abstract class ActionQuerySet< T extends ActionQuery >
  extends BeliefSet< T >
{
  
  /**
   * Creates a new ActionQuerySet initialized with the given collection of
   * action queries.
   * 
   * @param c
   */
  public ActionQuerySet( Collection< ? extends T > c )
  {
    super( c );
  }
  
  /**
   * Creates an empty ActionQuerySet
   */
  public ActionQuerySet()
  {
    super();
  }
}
