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
package org.tweetyproject.lp.asp.beliefdynamics.selectiverevision;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.tweetyproject.arg.lp.reasoner.LiteralReasoner;
import org.tweetyproject.arg.lp.semantics.attack.AttackStrategy;
import org.tweetyproject.arg.lp.syntax.ArgumentationKnowledgeBase;
import org.tweetyproject.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateHead;
import org.tweetyproject.lp.asp.syntax.ClassicalHead;

/**
 * This class represents the naive transformation function
 * for literals as introduced in [1].
 * 
 * [1] Homann, Sebastian:
 *  Master thesis: Argumentationsbasierte selektive Revision von erweiterten logischen
 *  Programmen.
 * 
 * @author Sebastian Homann
 *
 */
public class NaiveLiteralTransformationFunction implements
		MultipleTransformationFunction<ASPRule> {
	
	private Program beliefSet;
	private AttackStrategy attackRelation;
	private AttackStrategy defenseRelation;
	
	/**
	 * Creates a new naive transformation function for literals.
	 * @param beliefSet The belief set used for this transformation function.
	 * @param attackRelation the notion of attack used for attacking arguments
	 * @param defenseRelation the notion of attack used to attack attacking arguments
	 */
	public NaiveLiteralTransformationFunction(Collection<ASPRule> beliefSet, AttackStrategy attackRelation, AttackStrategy defenseRelation) {
		this.beliefSet = new Program(beliefSet);
		this.attackRelation = attackRelation;
		this.defenseRelation = defenseRelation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.selectiverevision.MultipleTransformationFunction#transform(java.util.Collection)
	 */
	@Override
	public Collection<ASPRule> transform(Collection<ASPRule> formulas) {
		Set<ASPRule> result = new HashSet<ASPRule>();

		ArgumentationKnowledgeBase argkb = new ArgumentationKnowledgeBase(beliefSet);
		LiteralReasoner reasoner = new LiteralReasoner(attackRelation, defenseRelation);
		for(ASPRule r : formulas) {
			if(r.isFact()) {
				if (r.getConclusion() instanceof AggregateHead)
					throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
				ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
				if(reasoner.isOverruled(argkb,head.complement())) {
					result.add(r);
				}
			}
		}
		return result;
	}
	
	/**
	 * "Transforms" the single fact by either accepting or rejecting it.
	 * @param rule a single fact
	 * @return the fact if its negation is not acceptable for the given attack-relations and belief base, an empty collection otherwise
	 */
	public Collection<ASPRule> transform(ASPRule rule) {
		LinkedList<ASPRule> in = new LinkedList<ASPRule>();
		in.add(rule);
		return transform(in);
	}
}
