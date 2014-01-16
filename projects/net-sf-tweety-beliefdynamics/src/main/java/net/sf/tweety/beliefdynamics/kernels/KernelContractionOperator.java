package net.sf.tweety.beliefdynamics.kernels;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.beliefdynamics.*;

/**
 * This class implements kernel contraction for belief bases [Hansson:2001]. That is, 
 * contraction is realized by determining the set of kernel for the contraction, ie.
 * the set of all minimal proofs for the formulas to be contracted, and then removing
 * one formula from each kernel using an incision function.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas that this operator works on.
 */
public class KernelContractionOperator<T extends Formula> extends MultipleBaseContractionOperator<T> {

	/**
	 *  The incision function of the kernel contraction.
	 */
	private IncisionFunction<T> incisionFunction;
	
	/**
	 * Used to determine kernels.
	 */
	private EntailmentRelation<T> entailmentRelation;
	
	/**
	 * Creates a new kernel contraction operator with the given incision function.
	 * @param incisionFunction some incision function.
	 * @param entailmentRelation an entailment relation used to determine kernels.
	 */
	public KernelContractionOperator(IncisionFunction<T> incisionFunction, EntailmentRelation<T> entailmentRelation){
		this.incisionFunction = incisionFunction;
		this.entailmentRelation = entailmentRelation;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseContractionOperator#contract(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<T> contract(Collection<T> base, Collection<T> formulas) {
		// Determine kernels of base for formulas
		Collection<Collection<T>> kernels = new HashSet<Collection<T>>();
		for(T formula: formulas)
			kernels.addAll(this.entailmentRelation.getKernels(base, formula));
		// incise each kernel
		Collection<T> incision = this.incisionFunction.incise(kernels);
		// remove incision
		Set<T> contractedBase = new HashSet<T>();
		contractedBase.addAll(base);
		contractedBase.removeAll(incision);		
		return contractedBase;
	}


}
