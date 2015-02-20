package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

public class LdoGraphBoxModality extends AbstractGraphLdoModality {

	public LdoGraphBoxModality(LdoFormula innerFormula,	Set<LdoArgument> lowerReferenceArguments, Set<LdoArgument> upperReferenceArguments) {
		super(innerFormula, lowerReferenceArguments,upperReferenceArguments);
	}

	@Override
	public LdoFormula clone() {
		return new LdoGraphBoxModality(this.getInnerFormula(),this.getLowerReferenceArguments(),this.getUpperReferenceArguments());
	}

	public String toString(){
		return "[" + this.getLowerReferenceArguments() + "," + this.getUpperReferenceArguments() + "](" + this.getInnerFormula() + ")";
	}
}
