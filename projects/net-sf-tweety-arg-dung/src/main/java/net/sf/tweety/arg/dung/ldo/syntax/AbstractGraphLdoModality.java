package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * Provides common functionalities for the graph-based modalities in LDO.
 * @author Matthias Thimm
 *
 */
public abstract class AbstractGraphLdoModality extends AbstractLdoModality {

	private Set<LdoArgument> upperReferenceArguments;
	private Set<LdoArgument> lowerReferenceArguments;
	
	public AbstractGraphLdoModality(LdoFormula innerFormula, Set<LdoArgument> lowerReferenceArguments, Set<LdoArgument> upperReferenceArguments) {
		super(innerFormula);
		this.lowerReferenceArguments = lowerReferenceArguments;
		this.upperReferenceArguments = upperReferenceArguments;
	}

	/**
	 * Returns the lower reference arguments of this modality.
	 * @return the lower reference arguments of this modality.
	 */
	public Set<LdoArgument> getLowerReferenceArguments(){
		return this.lowerReferenceArguments;
	}
	
	/**
	 * Returns the upper reference arguments of this modality.
	 * @return the upper reference arguments of this modality.
	 */
	public Set<LdoArgument> getUpperReferenceArguments(){
		return this.upperReferenceArguments;
	}
	
	@Override
	public Set<LdoArgument> getAtoms() {
		Set<LdoArgument> result = super.getAtoms();
		result.addAll(lowerReferenceArguments);
		result.addAll(upperReferenceArguments);
		return result;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		Set<PropositionalPredicate> result = super.getPredicates();
		for(LdoArgument a: this.lowerReferenceArguments)
			result.add(a.getPredicate());
		for(LdoArgument a: this.upperReferenceArguments)
			result.add(a.getPredicate());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.ldo.syntax.AbstractLdoModality#clone()
	 */
	@Override
	public abstract LdoFormula clone();

}
