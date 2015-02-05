package net.sf.tweety.arg.dung.ldo.syntax;

public class LdoBoxModality extends AbstractLdoModality {

	public LdoBoxModality(LdoFormula innerFormula) {
		super(innerFormula);
	}

	@Override
	public LdoFormula clone() {
		return new LdoBoxModality(this.getInnerFormula());
	}

	public String toString(){
		return "[](" + this.getInnerFormula() + ")";
	}
}
