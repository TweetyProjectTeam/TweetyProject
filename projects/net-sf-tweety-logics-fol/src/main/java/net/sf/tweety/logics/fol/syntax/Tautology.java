package net.sf.tweety.logics.fol.syntax;

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
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public String toString() {
		return LogicalSymbols.TAUTOLOGY();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#hashCode()
	 */
	@Override
	public int hashCode(){
		return 5;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolBasicStructure#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;		
		if (obj == null || getClass() != obj.getClass())
			return false;		
		return true;
	}

	@Override
	public Tautology clone() {
		return new Tautology();
	}
}
