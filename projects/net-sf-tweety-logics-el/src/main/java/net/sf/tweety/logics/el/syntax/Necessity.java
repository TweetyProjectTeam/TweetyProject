package net.sf.tweety.logics.el.syntax;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * This class models the necessity modality.
 * @author Matthias Thimm
 */
public class Necessity extends ModalFormula {

	/**
	 * Creates a new necessity formula with the
	 * given inner formula
	 * @param formula a formula, either a modal formula or a first-order formula.
	 */
	public Necessity(RelationalFormula formula){
		super(formula);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException{
		return new Necessity(this.getFormula().substitute(v, t));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	public String toString(){
		return "[]("+this.getFormula()+")";
	}


	@Override
	public RelationalFormula clone() {
		return new Necessity(this.getFormula().clone());
	}
}
