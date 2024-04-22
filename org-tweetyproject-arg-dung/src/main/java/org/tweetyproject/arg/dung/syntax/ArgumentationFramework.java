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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

 package org.tweetyproject.arg.dung.syntax;

 import java.util.Collection;
 import org.tweetyproject.arg.dung.semantics.Extension;
 import org.tweetyproject.commons.BeliefBase;
 
 /**
  * Represents a generic interface for argumentation frameworks in the context of Dung's theory.
  * This interface defines the basic structure and operations that can be performed within an argumentation framework.
  * It extends the {@link BeliefBase} interface, integrating with the broader infrastructure for managing logical beliefs.
  * 
  * @param <Arg> the type of arguments managed within the argumentation framework.
  * @author Sebastian Franke
  */
 public interface ArgumentationFramework<Arg> extends BeliefBase {
	 /**
	  * Checks if the argumentation framework contains all the elements in the given collection.
	  * 
	  * @param c the collection of elements to be checked for containment.
	  * @return true if this framework contains all elements in the specified collection, false otherwise.
	  */
	 boolean containsAll(Collection<?> c);
 
	 /**
	  * Determines whether a given argument is attacked by any arguments in the specified extension.
	  * 
	  * @param a the argument to check if it is being attacked.
	  * @param ext the extension that potentially contains attackers of the argument.
	  * @return true if the argument {@code a} is attacked by any argument in the given extension {@code ext}, false otherwise.
	  */
	 boolean isAttacked(Arg a, Extension<? extends ArgumentationFramework> ext);
 
	 /**
	  * Retrieves all the nodes (arguments) of this argumentation framework.
	  * 
	  * @return a collection of all arguments (nodes) within this framework.
	  */
	 Collection<Arg> getNodes();
 }
 
