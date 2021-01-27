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
package org.tweetyproject.beliefdynamics.selectiverevision.argumentative;

import java.util.*;

import org.tweetyproject.arg.deductive.accumulator.*;
import org.tweetyproject.arg.deductive.categorizer.*;
import org.tweetyproject.arg.deductive.reasoner.CompilationReasoner;
import org.tweetyproject.arg.deductive.syntax.DeductiveKnowledgeBase;
import org.tweetyproject.beliefdynamics.selectiverevision.*;
import org.tweetyproject.logics.pl.syntax.*;

/**
 * This class implements the argumentative transformation functions proposed in [Kruempelmann:2011].
 * 
 * @author Matthias Thimm
 */
public class ArgumentativeTransformationFunction implements MultipleTransformationFunction<PlFormula> {

	/**
	 * The categorizer used by this transformation function.
	 */
	private Categorizer categorizer;
	
	/**
	 * The accumulator used by this transformation function.
	 */
	private Accumulator accumulator;
	
	/**
	 * Whether this transformation function is skeptical.
	 */
	private boolean isSkeptical;
	
	/**
	 * The belief set used by this transformation function.
	 */
	private PlBeliefSet beliefSet;
	
	/**
	 * Creates a new argumentative transformation function.
	 * @param categorizer The categorizer used by this transformation function.
	 * @param accumulator The accumulator used by this transformation function.
	 * @param beliefSet The belief set used by this transformation function.
	 * @param isSkeptical Whether this transformation function is skeptical.
	 */
	public ArgumentativeTransformationFunction(Categorizer categorizer, Accumulator accumulator, PlBeliefSet beliefSet, boolean isSkeptical){
		this.categorizer = categorizer;
		this.accumulator = accumulator;
		this.beliefSet = beliefSet;
		this.isSkeptical = isSkeptical;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.selectiverevision.MultipleTransformationFunction#transform(java.util.Collection)
	 */
	@Override
	public Collection<PlFormula> transform(Collection<PlFormula> formulas) {
		Collection<PlFormula> transformedSet = new HashSet<PlFormula>();
		DeductiveKnowledgeBase joinedBeliefSet = new DeductiveKnowledgeBase(this.beliefSet);
		joinedBeliefSet.addAll(formulas);
		CompilationReasoner reasoner = new CompilationReasoner(this.categorizer, this.accumulator);
		for(PlFormula f: formulas){
			Double result = reasoner.query(joinedBeliefSet,f);
			if(this.isSkeptical){
				if(result > 0)
					transformedSet.add(f);
			}else if(result >= 0)
				transformedSet.add(f);			
		}		
		return transformedSet;
	}	
}
