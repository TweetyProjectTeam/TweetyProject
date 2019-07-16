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
package net.sf.tweety.logics.commons.syntax.interfaces;

import java.util.Map;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Rule;

/**
 * An interface for a logic program, which is a set of rules.
 * @author Tim Janus
 * @author Matthias Thimm
 *
 * @param <C>	The type of the formulas used for conclusions
 * @param <P>	The type of the formulas used for the premise
 * @param <T>	The type of the rules used in the program
 */
public interface LogicProgram<C extends Formula, P extends Formula, T extends Rule<?,?>> extends BeliefBase{
	/**
	 * Adds the given fact to the program
	 * @param fact a fact
	 */
	void addFact(C fact);
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBase#getSignature()
	 */
	Signature getMinimalSignature();
	
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and returns the new formula.
	 * @param v the term to be substituted.
	 * @param t the term to substitute.
	 * @return a formula where every occurrence of "v" is replaced
	 * 		by "t".
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 */
	LogicProgram<?,?,?> substitute(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/**
	 * Substitutes all occurrences of all terms "v" in map.keyset() in this formula
	 * by map.get(v) and returns the new formula.<br>
	 * @param map a mapping defining which terms to be substituted.
	 * @return a formula where every term in map.keyset() has been replaced by map.get(v).
	 * @throws IllegalArgumentException if any term and its mapping are of different sorts
	 */
	LogicProgram<?,?,?> substitute(Map<? extends Term<?>,? extends Term<?>> map) 
			throws IllegalArgumentException;
	
	/**
	 * Substitutes all occurrences of term "v" in this formula
	 * by term "t" and at the same time replaces all occurrences of term "t"
	 * by term "v" and eventually returns the new formula.
	 * @param v a term.
	 * @param t a term.
	 * @return a new logical formula with both "v" and "t" exchanged.
	 * @throws IllegalArgumentException if "v" and "t" are of different sorts
	 */
	LogicProgram<?,?,?> exchange(Term<?> v, Term<?> t) throws IllegalArgumentException;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();
}
