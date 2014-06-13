/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
