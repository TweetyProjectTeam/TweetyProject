/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.beliefdynamics.operators;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.beliefdynamics.*;
import net.sf.tweety.beliefdynamics.mas.*;
import net.sf.tweety.commons.KernelProvider;
import net.sf.tweety.graphs.orders.*;
import net.sf.tweety.logics.pl.NaiveReasoner;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This revision operator accepts only those pieces of information for revision where the credibility
 * of the source is at least as high as the credibility of the agent which proves the complement. The
 * actual revision is performed using a Levi revision which bases on the random kernel contraction
 * 
 * @author Matthias Thimm
 */
public class CrMasSimpleRevisionOperator extends MultipleBaseRevisionOperator<InformationObject<PropositionalFormula>>{

	/**
	 * Private extension of credibility comparer
	 * @author Matthias Thimm
	 */
	private class CredibilityComparer extends AbstractCredibilityComparer{
		public CredibilityComparer(Collection<InformationObject<PropositionalFormula>> formulas, Order<Agent> credOrder) {
			super(formulas, credOrder);
		}		
		public boolean isFormerAtLeastAsPreferredAsLatter(PropositionalFormula f, Collection<PropositionalFormula> formulas){
			return this.isAtLeastAsPreferredAs(f, formulas);
		}
	};
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<InformationObject<PropositionalFormula>> revise(Collection<InformationObject<PropositionalFormula>> base,	Collection<InformationObject<PropositionalFormula>> formulas) {
		if(!(base instanceof CrMasBeliefSet))
			throw new IllegalArgumentException("Argument 'base' has to be of type CrMasBeliefSet.");		
		Collection<InformationObject<PropositionalFormula>> allInformation = new HashSet<InformationObject<PropositionalFormula>>(base);
		allInformation.addAll(formulas);
		CredibilityComparer comparer = new CredibilityComparer(allInformation,((CrMasBeliefSet<PropositionalFormula>)base).getCredibilityOrder());
		Collection<PropositionalFormula> allProps = new HashSet<PropositionalFormula>();
		for(InformationObject<PropositionalFormula> f: allInformation)
			allProps.add(f.getFormula());
		Collection<InformationObject<PropositionalFormula>> credFormulas = new HashSet<InformationObject<PropositionalFormula>>();
		for(InformationObject<PropositionalFormula> f: formulas){
			// get all proofs of the complement of the formula			
			KernelProvider<PropositionalFormula> kernelProvider = new NaiveReasoner();
			Collection<Collection<PropositionalFormula>> kernels = kernelProvider.getKernels(allProps, new Negation(f.getFormula()));
			// if there is one kernel of the complement that is strictly more preferred then don't revise
			boolean formulaIsPlausible = true;
			for(Collection<PropositionalFormula> kernel: kernels){
				if(!comparer.isFormerAtLeastAsPreferredAsLatter(f.getFormula(), kernel)){
					formulaIsPlausible = false;
					break;
				}
			}			
			if(formulaIsPlausible)
				credFormulas.add(f);		
		}
		CrMasRevisionWrapper<PropositionalFormula> rev = new CrMasRevisionWrapper<PropositionalFormula>(
					new LeviMultipleBaseRevisionOperator<PropositionalFormula>(
							new RandomKernelContractionOperator(),
							new DefaultMultipleBaseExpansionOperator<PropositionalFormula>()
				));		
		return rev.revise(base, credFormulas);		
	}

}
