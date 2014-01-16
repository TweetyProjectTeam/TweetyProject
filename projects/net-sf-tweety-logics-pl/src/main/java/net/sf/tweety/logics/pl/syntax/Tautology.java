package net.sf.tweety.logics.pl.syntax;

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * A tautological formula.
 * @author Matthias Thimm
 */
public class Tautology extends SpecialFormula {

	/**
	 * Creates a new tautology.
	 */
	public Tautology() {
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.TAUTOLOGY();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Tautology;
	}

	@Override
	public int hashCode() {
		return 13;
	}

	@Override
	public Tautology clone() {
		return new Tautology();
	}
}
