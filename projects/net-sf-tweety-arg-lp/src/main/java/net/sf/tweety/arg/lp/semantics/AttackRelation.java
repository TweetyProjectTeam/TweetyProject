package net.sf.tweety.arg.lp.semantics;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.lp.ArgumentationKnowledgeBase;
import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.arg.lp.syntax.Argument;

/**
 * This class represents an attack relation for a specific set
 * of arguments represented by an ArgumentationKnowledgeBase.
 * 
 * @author Sebastian Homann
 *
 */
public class AttackRelation {
	private ArgumentationKnowledgeBase kb;
	private AttackStrategy strategy;
	
	/**
	 * Creates a new AttackRelation for an argumentation knowledgebase
	 * and a certain notion of attack.
	 * @param kb an argument knowledge base
	 * @param strategy a notion of attack for arguments in the knowledgebase
	 */
	public AttackRelation(ArgumentationKnowledgeBase kb, AttackStrategy strategy) {
		this.kb = kb;
		this.strategy = strategy;
	}
	
	/**
	 * Returns true iff argument a attacks argument b for the notion of
	 * attack represented by this attack relation.
	 * @param a an argument
	 * @param b another argument
	 * @return true iff argument a attacks argument b
	 */
	public boolean attacks(Argument a, Argument b) {
		return strategy.attacks(a, b);
	}

	/**
	 * Is true iff at least one attacking argument attacks b
	 * @param attacker a set of arguments
	 * @param b argument to be attacked by one or more arguments from the attacking set
	 * @return true iff at least one argument from attacker attacks b 
	 */
	public boolean attacks(Set<Argument> attacker, Argument b) {
		for(Argument a : attacker) {
			if(attacks(a, b)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all arguments from the knowledgebase, that attack argument a.
	 * @param a an argument
	 * @return the set of arguments from kb, that attack argument a
	 */
	public Set<Argument> getAttackingArguments(Argument a) {
		Set<Argument> result = new HashSet<Argument>();
		
		for(Argument b : kb.getArguments()) {
			if(attacks(b, a)) {
				result.add(b);
			}
		}
		return result;
	}
	
}
