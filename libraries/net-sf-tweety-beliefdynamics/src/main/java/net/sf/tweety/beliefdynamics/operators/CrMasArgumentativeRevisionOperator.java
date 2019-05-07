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
package net.sf.tweety.beliefdynamics.operators;

import java.util.*;

import net.sf.tweety.arg.deductive.accumulator.*;
import net.sf.tweety.beliefdynamics.*;
import net.sf.tweety.beliefdynamics.mas.*;
import net.sf.tweety.beliefdynamics.selectiverevision.*;
import net.sf.tweety.beliefdynamics.selectiverevision.argumentative.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class is an exemplary instantiation of a revision operator based on deductive argumentation and credibilities where
 * several parameters have been fixed:
 * - the inner revision is a Levi revision which bases on the random kernel contraction
 * - the transformation function is credulous
 * - the accumulator used for deductive argumentation is the simple accumulator
 * - the categorizer used for deductive argumentation is the credibility categorizer
 * 
 * @author Matthias Thimm
 */
public class CrMasArgumentativeRevisionOperator extends MultipleBaseRevisionOperator<InformationObject<PlFormula>>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<InformationObject<PlFormula>> revise(Collection<InformationObject<PlFormula>> base,	Collection<InformationObject<PlFormula>> formulas) {
		if(!(base instanceof CrMasBeliefSet))
			throw new IllegalArgumentException("Argument 'base' has to be of type CrMasBeliefSet.");		
		Collection<InformationObject<PlFormula>> allInformation = new HashSet<InformationObject<PlFormula>>(base);
		allInformation.addAll(formulas);
		Collection<PlFormula> plainFormulasFromBase = new HashSet<PlFormula>();
		for(InformationObject<PlFormula> f: base)
			plainFormulasFromBase.add(f.getFormula());
		MultipleTransformationFunction<PlFormula> transFunc = new ArgumentativeTransformationFunction(
				new CredibilityCategorizer(allInformation, ((CrMasBeliefSet<PlFormula,PlSignature>)base).getCredibilityOrder()),
				new SimpleAccumulator(),
				new PlBeliefSet(plainFormulasFromBase),
				false);
		CrMasRevisionWrapper<PlFormula> rev = new CrMasRevisionWrapper<PlFormula>(
				new MultipleSelectiveRevisionOperator<PlFormula>(transFunc,
						new LeviMultipleBaseRevisionOperator<PlFormula>(
								new RandomKernelContractionOperator(),
								new DefaultMultipleBaseExpansionOperator<PlFormula>()
				)));		
		return rev.revise(base, formulas);
	}
}
