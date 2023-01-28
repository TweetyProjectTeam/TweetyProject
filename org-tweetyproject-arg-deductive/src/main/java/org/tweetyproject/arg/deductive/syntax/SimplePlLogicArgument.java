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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.deductive.syntax;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.util.rules.Derivation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * 
 * @author Federico Cerutti (federico.cerutti(at)acm.org)
 *
 * Argument structure as defined in 
 * http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf
 *
 */
public class SimplePlLogicArgument extends Argument{

	/**
	 * support
	 */
	private Collection<SimplePlRule> support = null;
	/**claim*/
	private PlFormula claim = null;
	/**constructor*/
	public SimplePlLogicArgument(Collection<SimplePlRule> _support,
			PlFormula _claim) {
		super(null);
		this.support = _support;
		this.claim = _claim;
	}

	/**
	 * Constructor
	 * 
	 * @param 	derivation some derivation
	 */
	public SimplePlLogicArgument(Derivation<SimplePlRule> derivation) {
		super(null);
		this.support = new HashSet<SimplePlRule>(derivation);
		this.claim = (PlFormula) derivation.getConclusion();
	}
	/**
	 * 
	 * @return support
	 */
	public Collection<? extends SimplePlRule> getSupport() {
		return this.support;
	}

	/**
	 * 
	 * @return claim
	 */
	public PlFormula getClaim() {
		return this.claim;
	}

	/**
	 * prints String representation
	 */
	public String toString() {
		return "<" + this.support.toString() + "," + this.claim.toString()
				+ ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((support == null) ? 0 : support.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePlLogicArgument other = (SimplePlLogicArgument) obj;
		if (!this.claim.equals(other.claim))
			return false;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.equals(other.support))
			return false;
		return true;
	}
}
