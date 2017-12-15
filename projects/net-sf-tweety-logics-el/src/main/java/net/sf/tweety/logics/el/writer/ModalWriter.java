package net.sf.tweety.logics.el.writer;

import net.sf.tweety.commons.Writer;
import net.sf.tweety.logics.el.ModalBeliefSet;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

public class ModalWriter extends Writer {

	public ModalWriter(RelationalFormula formula) {
		super(formula);
	}
	
	public ModalWriter(ModalBeliefSet beliefSet) {
		super(beliefSet);
	}

	@Override
	public String writeToString() {
		return this.getObject().toString();
	}

}
