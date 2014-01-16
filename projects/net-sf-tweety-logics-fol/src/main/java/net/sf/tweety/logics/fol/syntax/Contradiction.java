package net.sf.tweety.logics.fol.syntax;

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
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.CONTRADICTION();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#hashCode()
	 */
	@Override
	public int hashCode(){
		return 3;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (this == obj)
			return true;		
		if (getClass() != obj.getClass())
			return false;		
		return true;
	}

	@Override
	public Contradiction clone() {
		return new Contradiction();
	}
}
