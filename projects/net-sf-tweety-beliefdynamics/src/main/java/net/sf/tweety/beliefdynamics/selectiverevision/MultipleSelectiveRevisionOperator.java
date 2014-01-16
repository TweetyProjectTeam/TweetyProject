package net.sf.tweety.beliefdynamics.selectiverevision;

import java.util.Collection;

import net.sf.tweety.Formula;
import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;

/**
 * This class implements a multiple selective revision operator following [Krümpelmann:2011,Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this operator works on.
 */
public class MultipleSelectiveRevisionOperator<T extends Formula> extends MultipleBaseRevisionOperator<T> {

	/**
	 * The transformation function for this revision.
	 */
	private MultipleTransformationFunction<T> transformationFunction;
	
	/**
	 * The revision operator for the inner revision.
	 */
	private MultipleBaseRevisionOperator<T> revisionOperator;
	
	/**
	 * Creates a new multiple selective revision operator for the given transformation function
	 * and inner revision.
	 * @param transformationFunction a transformation function.
	 * @param revisionOperator the inner revision.
	 */
	public MultipleSelectiveRevisionOperator(MultipleTransformationFunction<T> transformationFunction, MultipleBaseRevisionOperator<T> revisionOperator){
		this.transformationFunction = transformationFunction;
		this.revisionOperator = revisionOperator;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<T> revise(Collection<T> base, Collection<T> formulas) {
		return this.revisionOperator.revise(base, this.transformationFunction.transform(formulas));
	}

}
