package net.sf.tweety.arg.aspic.syntax;

import net.sf.tweety.commons.Signature;

/**
 * @author Nils Geilen
 * 
 * This is the negation of a Aspic word or a defeasible rule
 *
 */
public class AspicNegation implements AspicFormula {
	
	/**
	 * The negated formula
	 */
	private AspicFormula formula;

	/**
	 * Constructs a negation of a word or a rule
	 * @param formula	the negation of the formula represented by AspicNegation
	 */
	public AspicNegation(AspicFormula formula) {
		super();
		this.formula = formula;
	}
	
	/**
	 * Checks if a negates b
	 * @param a	a formula
	 * @param b	a formula
	 * @return	true iff a == -b
	 */
	public static boolean negates(AspicFormula a, AspicFormula b) {
		int negs = 0;
		while(a instanceof AspicNegation) {
			negs++;
			a=((AspicNegation)a).formula;
		}
		while(b instanceof AspicNegation) {
			negs++;
			b=((AspicNegation)b).formula;
		}
		return negs%2 == 1 && a.equals(b); 
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return formula.getSignature();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AspicNegation other = (AspicNegation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "-" + formula ;
	}
	
	
	
}
