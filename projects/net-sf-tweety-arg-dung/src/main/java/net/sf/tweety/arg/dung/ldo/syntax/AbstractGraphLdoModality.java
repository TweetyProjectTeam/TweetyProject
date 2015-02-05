package net.sf.tweety.arg.dung.ldo.syntax;

import java.util.Set;

import net.sf.tweety.logics.pl.syntax.PropositionalPredicate;

/**
 * Provides common functionalities for the graph-based modalities in LDO.
 * @author Matthias Thimm
 *
 */
public abstract class AbstractGraphLdoModality extends AbstractLdoModality {

	private Set<LdoArgument> referenceArguments;
	
	public AbstractGraphLdoModality(LdoFormula innerFormula, Set<LdoArgument> referenceArguments) {
		super(innerFormula);
		this.referenceArguments = referenceArguments;
	}

	
	/**
	 * Returns the reference arguments of this modality.
	 * @return the reference arguments of this modality.
	 */
	public Set<LdoArgument> getReferenceArguments(){
		return this.referenceArguments;
	}
	
	@Override
	public Set<LdoArgument> getAtoms() {
		Set<LdoArgument> result = super.getAtoms();
		result.addAll(referenceArguments);
		return result;
	}
	
	@Override
	public Set<PropositionalPredicate> getPredicates() {
		Set<PropositionalPredicate> result = super.getPredicates();
		for(LdoArgument a: this.referenceArguments)
			result.add(a.getPredicate());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.ldo.syntax.AbstractLdoModality#clone()
	 */
	@Override
	public abstract LdoFormula clone();

}
