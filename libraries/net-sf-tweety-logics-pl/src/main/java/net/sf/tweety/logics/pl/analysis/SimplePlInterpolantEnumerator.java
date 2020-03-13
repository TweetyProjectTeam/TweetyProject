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
import net.sf.tweety.logics.pl.syntax.PlFormula;

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
		// TODO continue
		return interpolants;
	}

	@Override
	public PlFormula getStrongestInterpolant(Collection<PlFormula> k1, Collection<PlFormula> k2) {
		Collection<PlFormula> interpolants = this.getInterpolants(k1, k2);
		if(interpolants.isEmpty())
			throw new IllegalArgumentException("Strongest interpolant undefined as the union of the given knowledge bases is consistent");
		PlFormula f = interpolants.iterator().next();		
		for(PlFormula g: interpolants) {
			if(this.reasoner.query(g, f));
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
			if(this.reasoner.query(f, g));
				f = g;
		}		
		return f;
	}

}
