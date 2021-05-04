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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.Program;

/**
 * An answer set is a belief set which only contains literals and represents the
 * deductive belief set of an extended logic program under the answer set
 * semantic. 
 * 
 * @author Thomas Vengels
 * @author Tim Janus
 * @author Anna Gessler
 */
public class AnswerSet extends InterpretationSet<ASPLiteral,Program,ASPRule> {
	public final int level;
	public final int weight;

	/**
	 * Creates a new empty AnswerSet.
	 */
	public AnswerSet() {
		level = 0;
		weight = 1;
	}

	/**
	 * Creates a new empty AnswerSet with the given level and weight.
	 * @param lits the literals
	 * @param level the level
	 * @param weight the weight
	 */
	public AnswerSet(Collection<ASPLiteral> lits, int level, int weight) {
		super(lits);
		this.level = level;
		this.weight = weight;
	}

	/**
	 * Copy-Constructor
	 * 
	 * @param other another answer set
	 */
	public AnswerSet(AnswerSet other) {
		super(other);
		this.level = other.level;
		this.weight = other.weight;
	}

	/**
	 * Returns all literals of a given name in the AnswerSet.
	 * 
	 * @param name the name of the literal
	 * @return set of literals
	 */
	public Set<ASPLiteral> getLiteralsWithName(String name) {
		Set<ASPLiteral> reval = new HashSet<ASPLiteral>();
		for (ASPLiteral lit : this) {
			if (lit.getName().equals(name)) {
				reval.add(lit);
			}
		}
		return reval;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + level + "," + weight + "]";
	}

	@Override
	public Object clone() {
		return new AnswerSet(this);
	}


	@Override
	public boolean satisfies(Program beliefBase) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("");
	}

	@Override
	public boolean satisfies(ASPRule formula) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(level, weight);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnswerSet other = (AnswerSet) obj;
		return level == other.level && weight == other.weight;
	}
	
}
