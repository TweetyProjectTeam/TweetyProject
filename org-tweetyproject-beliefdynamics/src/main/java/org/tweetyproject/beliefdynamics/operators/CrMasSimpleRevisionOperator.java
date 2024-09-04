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
package org.tweetyproject.beliefdynamics.operators;

import java.util.*;

import org.tweetyproject.agents.*;
import org.tweetyproject.beliefdynamics.*;
import org.tweetyproject.beliefdynamics.mas.*;
import org.tweetyproject.commons.KernelProvider;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.comparator.Order;

/**
 * This revision operator accepts only those pieces of information for revision where the credibility
 * of the source is at least as high as the credibility of the agent which proves the complement. The
 * actual revision is performed using a Levi revision which bases on the random kernel contraction
 *
 * @author Matthias Thimm
 */
public class CrMasSimpleRevisionOperator extends MultipleBaseRevisionOperator<InformationObject<PlFormula>>{

	/**Default */

	public CrMasSimpleRevisionOperator() {
	}


	/**
	 * Private extension of credibility comparer
	 * @author Matthias Thimm
	 */
	private class CredibilityComparer extends AbstractCredibilityComparer{
		public CredibilityComparer(Collection<InformationObject<PlFormula>> formulas, Order<Agent> credOrder) {
			super(formulas, credOrder);
		}
		public boolean isFormerAtLeastAsPreferredAsLatter(PlFormula f, Collection<PlFormula> formulas){
			return this.isAtLeastAsPreferredAs(f, formulas);
		}
	};

	/* (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<InformationObject<PlFormula>> revise(Collection<InformationObject<PlFormula>> base,	Collection<InformationObject<PlFormula>> formulas) {
		if(!(base instanceof CrMasBeliefSet))
			throw new IllegalArgumentException("Argument 'base' has to be of type CrMasBeliefSet.");
		Collection<InformationObject<PlFormula>> allInformation = new HashSet<InformationObject<PlFormula>>(base);
		allInformation.addAll(formulas);
		CredibilityComparer comparer = new CredibilityComparer(allInformation,((CrMasBeliefSet<PlFormula,PlSignature>)base).getCredibilityOrder());
		Collection<PlFormula> allProps = new HashSet<PlFormula>();
		for(InformationObject<PlFormula> f: allInformation)
			allProps.add(f.getFormula());
		Collection<InformationObject<PlFormula>> credFormulas = new HashSet<InformationObject<PlFormula>>();
		for(InformationObject<PlFormula> f: formulas){
			// get all proofs of the complement of the formula
			KernelProvider<PlFormula> kernelProvider = new SimplePlReasoner();
			Collection<Collection<PlFormula>> kernels = kernelProvider.getKernels(allProps, new Negation(f.getFormula()));
			// if there is one kernel of the complement that is strictly more preferred then don't revise
			boolean formulaIsPlausible = true;
			for(Collection<PlFormula> kernel: kernels){
				if(!comparer.isFormerAtLeastAsPreferredAsLatter(f.getFormula(), kernel)){
					formulaIsPlausible = false;
					break;
				}
			}
			if(formulaIsPlausible)
				credFormulas.add(f);
		}
		CrMasRevisionWrapper<PlFormula> rev = new CrMasRevisionWrapper<PlFormula>(
					new LeviMultipleBaseRevisionOperator<PlFormula>(
							new RandomKernelContractionOperator(),
							new DefaultMultipleBaseExpansionOperator<PlFormula>()
				));
		return rev.revise(base, credFormulas);
	}

}
