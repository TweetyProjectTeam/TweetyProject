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
package org.tweetyproject.arg.lp.syntax;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.lp.asp.syntax.*;


/**
 * Instances of this class represent arguments in the sense
 * of Definition 2 in [1].
 *
 * [1] Ralf Schweimeier and Michael Schroeder: A Parameterised Hierarchy of
 *  Argumentation Semantics for Extended Logic Programming and its
 *  Application to the Well-founded Semantics.
 *  In: Theory and Practice of Logic Programming, 5(1-2):207-242, 2003.
 *
 * @author Sebastian Homann
 */
public class Argument extends LinkedList<ASPRule> implements Formula {

	private static final long serialVersionUID = 6017406379850600902L;

	/**
	 * Constructs an `Argument` object initialized with a single ASP rule.
	 *
	 * @param rule an `ASPRule` object that represents the rule to be added to the argument.
	 */
	public Argument(ASPRule rule) {
		this.add(rule);
	}

	/**
	 * Constructs an `Argument` object initialized with a list of ASP rules.
	 *
	 *
	 * @param rules a `List` of `ASPRule` objects representing the rules to be added to the argument.
	 */
	public Argument(List<ASPRule> rules) {
		this.addAll(rules);
}

	/**
	 * Returns the set of conclusions of this argument, which is made up of
	 * the heads of all rules contained in this argument.
	 * @return the set of conclusions
	 */
	public Set<ASPLiteral> getConclusions() {
		Set<ASPLiteral> result = new HashSet<ASPLiteral>();
		for(ASPRule r : this) {
			if (r.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
			result.add(((ClassicalHead)r.getConclusion()).iterator().next());
		}
		return result;
	}

	/**
	 * Returns the set of assumptions, i.e. the set of DLPLiterals, that appear
	 * default-negated in the premise of some rule in this argument.
	 * Ex.: Let a &lt;- b, not -c. be a rule in argument A. Then A.getAssumptions()
	 *      will contain -c.
	 *
	 * @return a set of literals which this argument assumes not to be true
	 */
	public Set<ASPLiteral> getAssumptions() {
		Set<ASPLiteral> result = new HashSet<ASPLiteral>();
		for(ASPRule r : this) {
			for(ASPBodyElement elem : r.getPremise()) {
				if(elem instanceof DefaultNegation) {
					ASPLiteral assumption = elem.getLiterals().iterator().next();
					result.add(assumption);
				}
			}
		}
		return result;
	}


	/**
	 * An argument may only contain nonempty ground rules (i.e. no constraints).
	 * Also, for an argument A = [r1, r2, ..., rn] for each literal Lj in the body
	 * of a rule ri there has to be a rule rk with k&gt;i with head(rk) = Lj.
	 * @return true iff the the argument is valid
	 */
	public boolean checkValid() {
		Set<ASPLiteral> foundLiterals = new HashSet<ASPLiteral>();
		@SuppressWarnings("unchecked")
		LinkedList<ASPRule> reversed = (LinkedList<ASPRule>)this.clone();
		Collections.reverse(reversed);
		for(ASPRule r : reversed) {
			if(!r.isGround()) {
				return false;
			}
			if(r.isConstraint()) {
				return false;
			}
			if(r.isFact()) {
				if (r.getConclusion() instanceof AggregateHead)
					throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
				foundLiterals.add(((ClassicalHead)r.getConclusion()).getFormulas().iterator().next());
			}
			for(ASPBodyElement element : r.getPremise()) {
				if(element instanceof DefaultNegation) {
					continue;
				}
				if(! foundLiterals.containsAll(element.getLiterals())) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString() {
		String result = new String();

		String delimiter = "";
		for(ASPRule r : this) {
			result += delimiter + r.toString();
			delimiter = ",";
		}

		return "[" + result + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see org.tweetyproject.Formula#getSignature()
	 */
	public Signature getSignature() {
		FolSignature result = new FolSignature();
		for(ASPRule r : this) {
			result.addSignature(r.getSignature());
		}
		return result;
	}
}
