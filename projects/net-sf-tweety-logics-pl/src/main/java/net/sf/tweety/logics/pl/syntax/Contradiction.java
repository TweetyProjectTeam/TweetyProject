package net.sf.tweety.logics.pl.syntax;

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * A contradictory formula.
 * @author Matthias Thimm
 */
public class Contradiction extends SpecialFormula{
	
	/**
	 * Creates a new contradiction.
	 */
	public Contradiction() {		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.CONTRADICTION();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Contradiction;
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public Contradiction clone() {
		return new Contradiction();
	}
}
