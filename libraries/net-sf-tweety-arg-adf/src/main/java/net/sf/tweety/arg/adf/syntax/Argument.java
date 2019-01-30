package net.sf.tweety.arg.adf.syntax;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class Argument implements Formula, Node {

	private String name;

	private PropositionalFormula acceptanceCondition;
	
	public Argument(String name, PropositionalFormula acceptanceCondition) {
		super();
		this.name = name;
		this.acceptanceCondition = acceptanceCondition;
	}

	public String getName() {
		return name;
	}

	public PropositionalFormula getAcceptanceCondition() {
		return acceptanceCondition;
	}

	@Override
	public Signature getSignature() {
		return new AbstractDialecticalFrameworkSignature(this);
	}

}
