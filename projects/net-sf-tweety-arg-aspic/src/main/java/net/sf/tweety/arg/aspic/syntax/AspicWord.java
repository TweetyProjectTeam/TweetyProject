package net.sf.tweety.arg.aspic.syntax;

import net.sf.tweety.commons.SetSignature;
import net.sf.tweety.commons.Signature;

/**
 * @author Nils Geilen
 *
 * An AspicWord represents a variable in a rule or a premise
 */
public class AspicWord implements AspicFormula {
	/**
	 * The word's identifying name
	 */
	private String identifier;

	/**
	 * Create new  word
	 * @param identifier	the word's identifying name
	 */
	public AspicWord(String identifier) {
		super();
		this.identifier = identifier;
	}
	
	/**
	 * Set the word's identifying name
	 * @param id	new name
	 */
	public void setID(String id) {
		identifier =id;
	}
	
	/**
	 * @return	the word's idetifying name
	 */
	public String getID(){
		return identifier;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return identifier;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		SetSignature<AspicWord> sig = new SetSignature<>(this);
		return sig;
	}
	
	
}
