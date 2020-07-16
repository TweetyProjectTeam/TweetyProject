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
package net.sf.tweety.lp.asp.beliefdynamics.selectiverevision;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.arg.lp.reasoner.LiteralReasoner;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.syntax.ArgumentationKnowledgeBase;
import net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.ClassicalHead;

/**
 * This class represents the sceptical transformation function
 * for literals as introduced in [1].
 * 
 * [1] Homann, Sebastian:
 *  Master thesis: Argumentationsbasierte selektive Revision von erweiterten logischen
 *  Programmen.
 * 
 * @author Sebastian Homann
 *
 */
public class ScepticalLiteralTransformationFunction implements
		MultipleTransformationFunction<ASPRule> {
	
	private Program beliefSet;
	private AttackStrategy attackRelation;
	private AttackStrategy defenseRelation;
	
	/**
	 * Creates a new sceptical transformation function for literals.
	 * @param beliefSet The belief set used for this transformation function.
	 * @param attackRelation the notion of attack used for attacking arguments
	 * @param defenseRelation the notion of attack used to attack attacking arguments
	 */
	public ScepticalLiteralTransformationFunction(Collection<ASPRule> beliefSet, AttackStrategy attackRelation, AttackStrategy defenseRelation) {
		this.beliefSet = new Program(beliefSet);
		this.attackRelation = attackRelation;
		this.defenseRelation = defenseRelation;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction#transform(java.util.Collection)
	 */
	@Override
	public Collection<ASPRule> transform(Collection<ASPRule> formulas) {
		Set<ASPRule> result = new HashSet<ASPRule>();
		Program checkSet = new Program(beliefSet);
		checkSet.addAll(formulas);
		ArgumentationKnowledgeBase argkb = new ArgumentationKnowledgeBase(checkSet);
		LiteralReasoner reasoner = new LiteralReasoner(attackRelation, defenseRelation);
		for(ASPRule r : formulas) {
			if(r.isFact()) {
				ASPLiteral head = ((ClassicalHead)r.getConclusion()).iterator().next();
				if(reasoner.isJustified(argkb,head)) {
					result.add(r);
				}
			} else {
				throw new IllegalArgumentException("Cannot process rule " + r +".\nTransformation function is only defined for literals/facts.");
			}
		}
		return result;
	}
	
	/**
	 * "Transforms" the single rule by either accepting or rejecting it.
	 * @param rule a single elp rule
	 * @return the rule if it is acceptable for the given attack-relations and beliefbase, an empty collection otherwise
	 */
	public Collection<ASPRule> transform(ASPRule rule) {
		LinkedList<ASPRule> in = new LinkedList<ASPRule>();
		in.add(rule);
		return transform(in);
	}
}
