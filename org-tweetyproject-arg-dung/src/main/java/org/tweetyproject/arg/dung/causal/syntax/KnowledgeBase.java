/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.causal.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class describes a knowledge base consisting of a set of formulas (knowledge) and a set of assumptions.
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class KnowledgeBase extends PlBeliefSet {

	/**
	 * set of background assumptions
	 */
	private Collection<PlFormula> assumptions;
	
    /**
     * Constructs a knowledge base with a specified set of formulas and assumptions.
     * 
     * @param assumptions A set of propositional logic formulas representing the background assumptions.
     */
	public KnowledgeBase(Collection<PlFormula> formulas, Collection<PlFormula> assumptions) {
		super(formulas);
		this.assumptions = new HashSet<>(assumptions);
	}

    /**
     * Adds an assumption to this knowledge base.
     * 
     * @param assumption The PlFormula representing the assumption to be added
     * @return "True" iff the assumption was successfully added
     */
	public boolean addAssumption(PlFormula assumption) {
		if (!assumption.isLiteral()) throw new IllegalArgumentException("Assumption must be literal");
		return this.assumptions.add(assumption);
	}
	
	/**
	 * Determines whether the specified conclusion can be inferred from the given premises via this knowledge base.
	 *
	 * @param premises Set of formulas, which will be added to this knowledge base
	 * @param conclusion Formula, which is checked to be a conclusion of the combination of this instance and the specified premises or not
	 * @return "True" iff the specified formula is a conclusion of this knowledge base and the specified set of premises.
	 */
	public boolean entails(Set<PlFormula> premises, PlFormula conclusion) {
		var beliefs = this.getBeliefs();
		beliefs.addAll(premises);
		var beliefSet = new PlBeliefSet(beliefs);
		return new SimplePlReasoner().query(beliefSet, conclusion);
	}

	/**
	 * @return Set of {@link PlFormula}, which are the assumptions of this instance. 
	 */
	public HashSet<PlFormula> getAssumptions() {
		return new HashSet<>(this.assumptions);
	}
	
    /**
     * Retrieves all beliefs (propositional formulas) stored in this knowledge base.
     * 
     * @return A set of PlFormulas representing the beliefs held in this knowledge base.
     */
	public HashSet<PlFormula> getBeliefs(){
		return new HashSet<>(this.formulas);
	}

    /**
     * Removes an assumption from this knowledge base.
     * 
     * @param assumption The assumption to be removed.
     * @return true if the assumption was successfully removed, false if it was not found in the set.
     */
	public boolean removeAssumption(PlFormula assumption) {
		return this.assumptions.remove(assumption);
	}
	
	@Override
	public KnowledgeBase clone() {
        return new KnowledgeBase(formulas, this.assumptions);
	}
}
