package net.sf.tweety.arg.aspic.syntax;

import net.sf.tweety.commons.Signature;

public class AspicNegation implements AspicFormula {
	
	AspicFormula formula;

	public AspicNegation(AspicFormula formula) {
		super();
		this.formula = formula;
	}
	
	public static boolean negates(AspicFormula a, AspicFormula b) {
		int negs = 0;
		while(a instanceof AspicNegation) {
			negs++;
			a=((AspicNegation)a).formula;
		}
		while(b instanceof AspicNegation) {
			negs++;
			b=((AspicNegation)b).formula;
		}
		return negs%2 == 1 && a.equals(b); 
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AspicNegation other = (AspicNegation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "-" + formula ;
	}
	
	
	
}
