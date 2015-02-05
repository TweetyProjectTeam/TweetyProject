package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

public class LdoGraphDiamondModality extends AbstractGraphLdoModality{
	
	public LdoGraphDiamondModality(LdoFormula innerFormula,	Set<LdoArgument> referenceArguments) {
		super(innerFormula, referenceArguments);
	}

	@Override
	public LdoFormula clone() {
		return new LdoGraphBoxModality(this.getInnerFormula(),this.getReferenceArguments());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<" + this.getReferenceArguments() + ">(" + this.getInnerFormula() + ")";
	}
}
