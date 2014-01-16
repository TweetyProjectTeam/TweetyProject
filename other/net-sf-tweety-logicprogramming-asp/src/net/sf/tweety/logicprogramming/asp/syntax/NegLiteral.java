package net.sf.tweety.logicprogramming.asp.syntax;

/**
 * class for strict negated literals
 * 
 * @author Thomas Vengels
 *
 */
public class NegLiteral implements ELPLiteral {

	protected ELPAtom atom;
	
	public NegLiteral(ELPAtom a) {
		atom = a;
	}
	
	@Override
	public ELPLiteral getLiteral() {
		return this;
	}

	@Override
	public boolean isAtom() {
		return false;
	}

	@Override
	public boolean isDefaultNegated() {
		return false;
	}

	@Override
	public boolean isStrictNegated() {
		return true;
	}

	@Override
	public ELPAtom getAtom() {
		return this.atom;
	}
	
	public String toString() {
		return "-"+atom.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NegLiteral))
			return false;

		NegLiteral nl = (NegLiteral) o;
		
		return this.atom.equals( nl.getAtom() );
	}

	@Override
	public boolean isPredicate() {
		return false;
	}

}
