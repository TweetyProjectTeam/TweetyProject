package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;

public class AspicWord implements Formula {
	private final String identifier;
	private boolean //axiom = false, 
			negation = false;

	public AspicWord(String identifier, boolean negation) {
		super();
		this.identifier = identifier;
		this.negation = negation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + (negation ? 1231 : 1237);
		return result;
	}
	

/*	public boolean isAxiom() {
		return axiom;
	}

	public AspicWord asAxiom(boolean axiom) {
		this.axiom = axiom;
		return this;
	}*/

	public boolean isNegation() {
		return negation;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AspicWord other = (AspicWord) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (negation != other.negation)
			return false;
		return true;
	}


	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		if(negation)
			sw.write("not ");
		sw.write(identifier);
	//	if(axiom)
		//	sw.write(" (axiom)");
		return sw . toString();
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
