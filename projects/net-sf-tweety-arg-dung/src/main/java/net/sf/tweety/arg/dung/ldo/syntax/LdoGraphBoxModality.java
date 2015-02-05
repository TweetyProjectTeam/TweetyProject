package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

public class LdoGraphBoxModality extends AbstractGraphLdoModality {

	public LdoGraphBoxModality(LdoFormula innerFormula,	Set<LdoArgument> referenceArguments) {
		super(innerFormula, referenceArguments);
	}

	@Override
	public LdoFormula clone() {
		return new LdoGraphBoxModality(this.getInnerFormula(),this.getReferenceArguments());
	}

	public String toString(){
		return "[" + this.getReferenceArguments() + "](" + this.getInnerFormula() + ")";
	}
}
