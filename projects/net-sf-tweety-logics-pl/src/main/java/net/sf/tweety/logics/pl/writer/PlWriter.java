package net.sf.tweety.logics.pl.writer;

import net.sf.tweety.commons.Writer;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class implements a writer for propositional formulas and belief bases.
 * 
 * @author Anna Gessler
 */
public class PlWriter extends Writer {

	public PlWriter(PropositionalFormula plFormula) {
		super(plFormula);
	}
	
	public PlWriter(PlBeliefSet plBeliefSet) {
		super(plBeliefSet);
	}

	@Override
	public String writeToString() {
		return this.getObject().toString();
	}

}
