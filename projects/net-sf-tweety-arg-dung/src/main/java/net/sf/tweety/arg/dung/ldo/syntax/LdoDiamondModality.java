package net.sf.tweety.arg.dung.ldo.syntax;

public class LdoDiamondModality extends AbstractLdoModality {

	public LdoDiamondModality(LdoFormula innerFormula) {
		super(innerFormula);
	}

	@Override
	public LdoFormula clone() {
		return new LdoDiamondModality(this.getInnerFormula());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "<>(" + this.getInnerFormula() + ")";
	}
}
