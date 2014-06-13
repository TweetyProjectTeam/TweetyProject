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
package net.sf.tweety.beliefdynamics.selectiverevision.argumentative;

import java.util.*;

import net.sf.tweety.arg.deductive.*;
import net.sf.tweety.arg.deductive.accumulator.*;
import net.sf.tweety.arg.deductive.categorizer.*;
import net.sf.tweety.beliefdynamics.selectiverevision.*;
import net.sf.tweety.logics.pl.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements the argumentative transformation functions proposed in [Kruempelmann:2011].
 * 
 * @author Matthias Thimm
 */
public class ArgumentativeTransformationFunction implements MultipleTransformationFunction<PropositionalFormula> {

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
	 * @see net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction#transform(java.util.Collection)
	 */
	@Override
	public Collection<PropositionalFormula> transform(Collection<PropositionalFormula> formulas) {
		Collection<PropositionalFormula> transformedSet = new HashSet<PropositionalFormula>();
		DeductiveKnowledgeBase joinedBeliefSet = new DeductiveKnowledgeBase(this.beliefSet);
		joinedBeliefSet.addAll(formulas);
		CompilationReasoner reasoner = new CompilationReasoner(joinedBeliefSet, this.categorizer, this.accumulator);
		for(PropositionalFormula f: formulas){
			Double result = reasoner.query(f).getAnswerDouble();
			if(this.isSkeptical){
				if(result > 0)
					transformedSet.add(f);
			}else if(result >= 0)
				transformedSet.add(f);			
		}		
		return transformedSet;
	}	
}
