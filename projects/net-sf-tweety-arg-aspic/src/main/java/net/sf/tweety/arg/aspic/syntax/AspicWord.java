package net.sf.tweety.arg.aspic.syntax;

import net.sf.tweety.commons.Signature;

public class AspicWord implements AspicFormula {
	private String identifier;

	public AspicWord(String identifier) {
		super();
		this.identifier = identifier;
	}
	
	public void setID(String id) {
		identifier =id;
	}
	
	public String getID(){
		return identifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// generalized from getClass != getClass
		if (!(obj instanceof AspicWord))
			return false;
		AspicWord other = (AspicWord) obj;
		if (identifier == null) {
			//if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return identifier;
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
