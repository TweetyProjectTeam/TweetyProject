package net.sf.tweety.arg.aba.syntax;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *	An assumption of an ABA theory
 * @param <T>	is the type of the language that the ABA theory's rules range over 
 */
public class Assumption <T extends Invertable> implements ABARule< T> {
	/**
	 * The assumed formula
	 */
	T assumption;

	/**
	 * Creates a new assumption
	 * @param assumption	the assumed formula
	 */
	public Assumption(T assumption) {
		super();
		this.assumption = assumption;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isFact()
	 */
	@Override
	public boolean isFact() {
		return true; 
	}
	

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#isConstraint()
	 */
	@Override
	public boolean isConstraint() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#setConclusion(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void setConclusion(T conclusion) {
		assumption = conclusion;
		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremise(net.sf.tweety.commons.Formula)
	 */
	@Override
	public void addPremise(T premise) {
		throw new RuntimeException("Cannot add Premise to Assumtion");			
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#addPremises(java.util.Collection)
	 */
	@Override
	public void addPremises(Collection<? extends T> premises) {
		throw new RuntimeException("Cannot add Premise to Assumtion");	
		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return assumption.getSignature();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getPremise()
	 */
	@Override
	public Collection<? extends T> getPremise() {
		return new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.util.rules.Rule#getConclusion()
	 */
	@Override
	public T getConclusion() {
		return assumption;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.syntax.ABARule#isAssumption()
	 */
	@Override
	public boolean isAssumption() {
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return assumption.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assumption == null) ? 0 : assumption.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assumption other = (Assumption) obj;
		if (assumption == null) {
			if (other.assumption != null)
				return false;
		} else if (!assumption.equals(other.assumption))
			return false;
		return true;
	}
	
	

}
