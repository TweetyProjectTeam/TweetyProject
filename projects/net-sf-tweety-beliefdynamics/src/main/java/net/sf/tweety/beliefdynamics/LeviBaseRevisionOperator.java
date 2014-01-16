package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.ClassicalFormula;

/**
 * This class implements the Levi identity for revision, ie. an revision that is composed of the
 * contraction with the negated formula and then expansion of the formula.
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of formulas this operators works on.
 */
public class LeviBaseRevisionOperator<T extends ClassicalFormula> implements BaseRevisionOperator<T> {

	/**
	 * The contraction operator of this Levi revision.
	 */
	private BaseContractionOperator<T> contraction;
	
	/**
	 * The expansion operator of this Levi revision.
	 */
	private BaseExpansionOperator<T> expansion;
	
	/**
	 * Creates a new Levi base revision with the given contraction and expansion operators.
	 * @param contraction some contraction operator.
	 * @param expansion some expansion operator.
	 */
	public LeviBaseRevisionOperator(BaseContractionOperator<T> contraction, BaseExpansionOperator<T> expansion){
		this.contraction = contraction;
		this.expansion = expansion;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseRevisionOperator#revise(java.util.Collection, net.sf.tweety.Formula)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> revise(Collection<T> base, T formula) {
		return this.expansion.expand(this.contraction.contract(base, (T) formula.complement()), formula);
	}

}
