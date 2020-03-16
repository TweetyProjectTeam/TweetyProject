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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.logics.commons.analysis.InterpolantEnumerator;
import net.sf.tweety.logics.pl.reasoner.AbstractPlReasoner;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.logics.pl.util.EnumeratingIterator;

/**
 * Implements an exhaustive search approach to compute all interpolants of
 * a knowledge base wrt. another knowledge base.
 * 
 * @author Matthias Thimm
 *
 */
public class SimplePlInterpolantEnumerator implements InterpolantEnumerator<PlFormula>{

	/**
	 * A PL reasoner that is used for entailment queries during search
	 */
	private AbstractPlReasoner reasoner;
	
	/**
	 * Creates a new SimplePlInterpolantEnumerator that uses the given PL reasoner
	 * for entailment queries.
	 * @param reasoner some PL reasoner
	 */
	public SimplePlInterpolantEnumerator(AbstractPlReasoner reasoner) {
		this.reasoner = reasoner;
	}
	
	@Override
	public Collection<PlFormula> getInterpolants(Collection<PlFormula> k1, Collection<PlFormula> k2) {
		Collection<PlFormula> interpolants = new HashSet<PlFormula>();
		PlSignature commonSignature = PlSignature.getSignature(k1);
		commonSignature.retainAll(PlSignature.getSignature(k2).toCollection());
		// the following iterator can be used to enumerate all possible formulas of
		// the given signature
		EnumeratingIterator it = new EnumeratingIterator(commonSignature,true);
		// omit the empty belief base
		it.next();
		PlFormula currentCandidate;
		PlBeliefSet currentBeliefSet;		
		while(true) {			
			currentBeliefSet = it.next();
			// if there is more than one formula in the set, we are finished
			if(currentBeliefSet.size() > 1)
				break;
			currentCandidate = currentBeliefSet.iterator().next();
			if(this.isInterpolant(currentCandidate, k1, k2))
				interpolants.add(currentCandidate);
		}
		return interpolants;
	}
	
	@Override
	public boolean isInterpolant(PlFormula candidate, Collection<PlFormula> k1, Collection<PlFormula> k2) {
		PlSignature commonSignature = PlSignature.getSignature(k1);
		commonSignature.retainAll(PlSignature.getSignature(k2).toCollection());
		if(!candidate.getSignature().isSubSignature(commonSignature))
			return false;
		if(!this.reasoner.query(new PlBeliefSet(k1), candidate))
			return false;
		Collection<PlFormula> k2extended = new HashSet<PlFormula>(k2);
		k2extended.add(candidate);
		if(!this.reasoner.query(new PlBeliefSet(k2extended), new Contradiction()))
			return false;
		return true;
	}
	
	@Override
	public PlFormula getStrongestInterpolant(Collection<PlFormula> k1, Collection<PlFormula> k2) {
		Collection<PlFormula> interpolants = this.getInterpolants(k1, k2);
		if(interpolants.isEmpty())
			throw new IllegalArgumentException("Strongest interpolant undefined as the union of the given knowledge bases is consistent");
		PlFormula f = interpolants.iterator().next();		
		for(PlFormula g: interpolants) {
			if(this.reasoner.query(g, f))
				f = g;
		}		
		return f;
	}

	@Override
	public PlFormula getWeakestInterpolant(Collection<PlFormula> k1, Collection<PlFormula> k2) {
		Collection<PlFormula> interpolants = this.getInterpolants(k1, k2);
		if(interpolants.isEmpty())
			throw new IllegalArgumentException("Weakest interpolant undefined as the union of the given knowledge bases is consistent");
		PlFormula f = interpolants.iterator().next();		
		for(PlFormula g: interpolants) {
			if(this.reasoner.query(f, g))
				f = g;
		}		
		return f;
	}
}
