package net.sf.tweety.beliefdynamics.selectiverevision;

import java.util.*;

import net.sf.tweety.beliefdynamics.*;
import net.sf.tweety.commons.*;

/**
 * This class implements a selective revision operator following [Ferme:1999].
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas this operator works on.
 */
public class SelectiveRevisionOperator<T extends Formula> implements BaseRevisionOperator<T> {

	/**
	 * The transformation function for this revision.
	 */
	private TransformationFunction<T> transformationFunction;
	
	/**
	 * The revision operator for the inner revision.
	 */
	private BaseRevisionOperator<T> revisionOperator;
	
	/**
	 * Creates a new selective revision operator for the given transformation function
	 * and inner revision.
	 * @param transformationFunction a transformation function.
	 * @param revisionOperator the inner revision.
	 */
	public SelectiveRevisionOperator(TransformationFunction<T> transformationFunction, BaseRevisionOperator<T> revisionOperator){
		this.transformationFunction = transformationFunction;
		this.revisionOperator = revisionOperator;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseRevisionOperator#revise(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public Collection<T> revise(Collection<T> base, T formula) {
		return this.revisionOperator.revise(base, this.transformationFunction.transform(formula));
	}

}
