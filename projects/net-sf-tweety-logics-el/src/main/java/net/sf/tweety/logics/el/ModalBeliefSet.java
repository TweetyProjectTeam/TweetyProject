package net.sf.tweety.logics.el;

import java.util.Collection;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.el.syntax.ModalFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * This class models a modal knowledge base, i.e. a set of formulas
 * in modal logic.
 * 
 * @author Anna Gessler
 */
public class ModalBeliefSet extends BeliefSet<ModalFormula> {
	
	/**
	 * Creates a new empty modal knowledge base.
	 */
	public ModalBeliefSet(){
		super();
	}
	
	/**
	 * Creates a new modal knowledge base with the given set of formulas.
	 * @param formulas
	 */
	public ModalBeliefSet(Set<ModalFormula> formulas){
		super(formulas);
	}

	@Override
	public Signature getSignature() {
		FolSignature sig = new FolSignature();
		for(Formula m: this) {
			while (m instanceof ModalFormula) {
				m = ((ModalFormula)m).getFormula();
			}
			sig.add(m);	
			}	
		return sig;
	}

}
