package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * This class models the Tptp-Fof-ForallQuantifiedFormula
 * Format:
 * ExistsQuantifiedFormula := <quantifier> [<variables>] : <formula>
 * @author Bastian Wolf
 */
public class TptpFofForallQuantifiedFormula extends TptpFofQuantifiedFormula {

	/**
	 * Static forall quantifier 
	 */
	private static String quantifier = TptpFofLogicalSymbols.FORALLQUANTIFIER();

	/**
     *
     */
	private Set<TptpFofVariable> variables;

	/**
     *
     */
	private TptpFofFormula formula;

	/**
	 * Constructor
	 * @param variables
	 * @param formula
	 */
	public TptpFofForallQuantifiedFormula(Set<TptpFofVariable> variables,
			TptpFofFormula formula) {
		super(variables, formula);
	}
	
	
	/*
	 *	Getter 
	 */
	public Set<TptpFofVariable> getVariables() {
		return variables;
	}

	public String getQuantifier() {
		return quantifier;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TptpFofForallQuantifiedFormula that = (TptpFofForallQuantifiedFormula) o;

		if (!quantifier.equals(that.quantifier))
			return false;
		if (!variables.equals(that.variables))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = quantifier.hashCode();
		result = 31 * result + variables.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofLogicFormula#toString()
	 */
	@Override
	public String toString() {
		return quantifier + " [" + this.variables.toString() + "] : "
				+ this.formula.toString();
	}
}
