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
package org.tweetyproject.logics.pl.syntax;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.commons.LogicalSymbols;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;

/**
 * A contradictory formula.
 * @author Matthias Thimm
 */
public class Contradiction extends SpecialFormula{
	
	/**
	 * Creates a new contradiction.
	 */
	public Contradiction() {		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.CONTRADICTION();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Contradiction;
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public Contradiction clone() {
		return new Contradiction();
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#getModels(org.tweetyproject.logics.pl.syntax.PropositionalSignature)
	 */
	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		return new HashSet<PossibleWorld>();
	}
}
