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
package org.tweetyproject.arg.lp.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.lp.semantics.AttackRelation;
import org.tweetyproject.arg.lp.semantics.attack.AttackStrategy;
import org.tweetyproject.arg.lp.syntax.Argument;
import org.tweetyproject.arg.lp.syntax.ArgumentationKnowledgeBase;
import org.tweetyproject.commons.QualitativeReasoner;


/**
 * This class models a reasoner for extended logic programming based arguments using
 * the fixpoint semantics from [1] parameterised by a notion of attack x for the opponent
 * and another notion of attack y as a defense for the proponent. This base implementation
 * only allows to query whether an argument A is x/y-justified in a ELP P.
 * A is called x/y-acceptable wrt. a set of arguments S if for every argument B in P such
 * that (B,A) \in x there exists an argument C \in S such that (C,B) \in y.
 * The set of acceptable arguments for P is defined as the fixpoint J_{P,x/y} of the function
 *  F_{P,x/y}(S) = { A | A is x/y-acceptable with regard to S}
 *
 *  In [1] it is shown that J_{a/u} equals Dung's grounded semantics, J_{u/su} equals the
 *  well founded semantics for normal logic programs and J_{u/a} equals the well-founded
 *  semantics for logic programs with explicit negation.
 *
 *
 * [1] Ralf Schweimeier and Michael Schroeder: A Parameterised Hierarchy of
 * Argumentation Semantics for Extended Logic Programming and its
 * Application to the Well-founded Semantics.
 * In: Theory and Practice of Logic Programming, 5(1-2):207-242, 2003.
 *
 * @author Sebastian Homann
 */
public class ArgumentationReasoner implements QualitativeReasoner<ArgumentationKnowledgeBase,Argument> {

    /**
     * The strategy used for attacks.
     */
    protected AttackStrategy attackStrategy;

    /**
     * The strategy used for defenses.
     */
    protected AttackStrategy defenceStrategy;

	/**
	 * Creates a new ArgumentationReasoner parameterised
	 * by a notion of attack for the opponent and another notion of attack for the defense
	 * @param attack some attack strategy
	 * @param defence some attack strategy
	 */
	public ArgumentationReasoner(AttackStrategy attack, AttackStrategy defence) {
		this.attackStrategy = attack;
		this.defenceStrategy = defence;
	}


	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.QualitativeReasoner#query(org.tweetyproject.commons.BeliefBase, org.tweetyproject.commons.Formula)
	 */
	@Override
	public Boolean query(ArgumentationKnowledgeBase kb, Argument query) {
		return getJustifiedArguments(kb).contains(query);
	}

	/**
	 * An argument is called x/y-overruled, if it is attacked by an
	 * x/y-justified argument.
	 * @param kb a knowledge base
	 *
	 * @param arg an argument
	 * @return true iff arg is x-attacked by an x/y-justified argument
	 */
	public boolean isOverruled(ArgumentationKnowledgeBase kb, Argument arg) {
		for(Argument attacker : getJustifiedArguments(kb)) {
			if(new AttackRelation(kb, this.attackStrategy).attacks(attacker, arg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * An argument is called x/y-defensible if it is neither x/y-justified
	 * nor x/y-overruled.
	 *  @param kb a knowledge base
	 * @param arg an argument
	 * @return true iff arg is neither x/y-justified nor x/y-overruled.
	 */
	public boolean isDefensible(ArgumentationKnowledgeBase kb,Argument arg) {
		return (! isOverruled(kb,arg) ) && (! query(kb,arg) );
	}

	/**
	 * Returns the set of x/y-justified arguments using a bottom-up fixpoint calculation
	 *  @param kb a knowledge base
	 * @return the set of x/y-justified arguments
	 */
	public Set<Argument> getJustifiedArguments(ArgumentationKnowledgeBase kb) {
		Set<Argument> arguments = kb.getArguments();
		Set<Argument> result = new HashSet<Argument>();

		// fixpoint calculation: add defended arguments until nothing changes
		boolean changes = true;
		while(changes) {
			changes = false;
			for(Argument arg : arguments) {
				if(isAcceptable(kb,arguments, result, arg)) {
					if(!result.contains(arg)) {
						result.add(arg);
						changes = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns the set of overruled arguments, i.e. the set of arguments,
	 * which are attacked by a justified argument.
	 *  @param kb a knowledge base
	 * @return the set of overruled arguments.
	 */
	public Set<Argument> getOverruledArguments(ArgumentationKnowledgeBase kb) {
		Set<Argument> arguments = kb.getArguments();
		Set<Argument> result = new HashSet<Argument>();
		Set<Argument> justifiedArguments = getJustifiedArguments(kb);
		for(Argument candidate : arguments) {
			for(Argument justified : justifiedArguments) {
				if(new AttackRelation(kb, this.attackStrategy).attacks(justified, candidate)) {
					result.add(candidate);
				}
			}
		}
		return result;
	}

	/**
	 * Returns the set of defensible arguments, i.e. the set of arguments,
	 * that are neither justified nor overruled.
	 *  @param kb a knowledge base
	 * @return the set of defensible arguments.
	 */
	public Set<Argument> getDefensibleArguments(ArgumentationKnowledgeBase kb) {
		Set<Argument> result = new HashSet<Argument>();
		Set<Argument> arguments = kb.getArguments();
		Set<Argument> justifiedArguments = getJustifiedArguments(kb);
		Set<Argument> overruledArguments = getOverruledArguments(kb);
		for(Argument candidate : arguments) {
			if(!justifiedArguments.contains(candidate)) {
				if(!overruledArguments.contains(candidate)) {
					result.add(candidate);
				}
			}
		}
		return result;
	}

	/**
	 * Returns true iff the argument toCheck is x/y-acceptable wrt. the set of defendingArguments.
	 * A is called x/y-acceptable wrt. a set of arguments S if for every argument B in P such
     * that (B,A) \in x there exists an argument C \in S such that (C,B) \in y.
     *
     * @param kb a knowledge base
	 * @param arguments a set of arguments
	 * @param defendingArguments a s set of defending arguments
	 * @param toCheck the argument to be checked
	 * @return true iff toCheck is x/y-acceptable wrt. defendingArguments
	 */
	private boolean isAcceptable(ArgumentationKnowledgeBase kb, Set<Argument> arguments, Set<Argument> defendingArguments, Argument toCheck) {
		Set<Argument> attackingArguments = new AttackRelation(kb, this.attackStrategy).getAttackingArguments(toCheck);
		for(Argument attackingArgument : attackingArguments) {
			// check if  there is an argument in defendingArguments, that defends against the attacker
			if(! new AttackRelation(kb, this.defenceStrategy).attacks(defendingArguments, attackingArgument)) {
				// no defense against attacker
				return false;
			}
		}
		return true;
	}


	@Override
	public boolean isInstalled() {
		return true;
	}

}
