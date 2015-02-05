package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * Provides common functionalities for all modalities in LDO.
 * @author Matthias Thimm
 *
 */
public abstract class AbstractLdoModality extends LdoFormula{

	/** The inner formula of this modality */
	private LdoFormula innerFormula;
	
	/**
	 * Creates a new modality for the given inner formula
	 * @param innerFormula some ldo formula
	 */
	public AbstractLdoModality(LdoFormula innerFormula){
		this.innerFormula = innerFormula;
	}
	
	@Override
	public Set<LdoArgument> getAtoms() {
		return this.innerFormula.getAtoms();
	}

	/**
	 * Returns the inner formula of this modality.
	 * @return the inner formula of this modality.
	 */
	public LdoFormula getInnerFormula(){
		return this.innerFormula;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return this.innerFormula.getPredicates();
	}

	@Override
	public Set<LdoFormula> getLiterals() {
		return this.innerFormula.getLiterals();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((innerFormula == null) ? 0 : innerFormula.hashCode());
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
		AbstractLdoModality other = (AbstractLdoModality) obj;
		if (innerFormula == null) {
			if (other.innerFormula != null)
				return false;
		} else if (!innerFormula.equals(other.innerFormula))
			return false;
		return true;
	}

	@Override
	public abstract LdoFormula clone();
	
}
