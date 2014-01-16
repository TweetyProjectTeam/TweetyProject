package net.sf.tweety.lp.asp.beliefdynamics.selectiverevision;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.arg.lp.ArgumentationKnowledgeBase;
import net.sf.tweety.arg.lp.LiteralReasoner;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;

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
		MultipleTransformationFunction<Rule> {
	
	private Program beliefSet;
	private AttackStrategy attackRelation;
	private AttackStrategy defenseRelation;
	
	/**
	 * Creates a new naive transformation function for literals.
	 * @param beliefSet The belief set used for this transformation function.
	 * @param attackRelation the notion of attack used for attacking arguments
	 * @param defenseRelation the notion of attack used to attack attacking arguments
	 */
	public NaiveLiteralTransformationFunction(Collection<Rule> beliefSet, AttackStrategy attackRelation, AttackStrategy defenseRelation) {
		this.beliefSet = new Program(beliefSet);
		this.attackRelation = attackRelation;
		this.defenseRelation = defenseRelation;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction#transform(java.util.Collection)
	 */
	@Override
	public Collection<Rule> transform(Collection<Rule> formulas) {
		Set<Rule> result = new HashSet<Rule>();

		ArgumentationKnowledgeBase argkb = new ArgumentationKnowledgeBase(beliefSet);
		LiteralReasoner reasoner = new LiteralReasoner(argkb, attackRelation, defenseRelation);
		for(Rule r : formulas) {
			if(r.isFact()) {
				DLPLiteral head = r.getConclusion().iterator().next();
				if(reasoner.isOverruled(head.complement())) {
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
	public Collection<Rule> transform(Rule rule) {
		LinkedList<Rule> in = new LinkedList<Rule>();
		in.add(rule);
		return transform(in);
	}
}
