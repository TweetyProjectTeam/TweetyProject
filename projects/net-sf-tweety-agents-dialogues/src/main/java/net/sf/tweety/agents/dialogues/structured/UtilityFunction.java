/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;

/**
 * This interface models an utility function, i.e. a 
 * function that maps sets of propositions to an integer and
 * thus ranking sets of propositions.
 * 
 * @author Matthias Thimm
 */
public interface UtilityFunction {

	/**
	 * Rank the given collection of propositions. A set S is preferred to
	 * a set T if rank(S)>rank(T).
	 * @param propositions a collection of propositions.
	 * @return the rank of the given collection.
	 */
	public int rank(Collection<? extends Proposition> propositions);
}
