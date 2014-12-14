package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * The Tptp logic formula class
 * @author Bastian Wolf
 */
public class TptpFofLogicFormula extends TptpFofFormula {

	/**
	 * 
	 */
	private Set<TptpFofAtom> atoms;
	
	/**
	 * 
	 */
	private Set<TptpFofFunctor> functors;
	
	/**
	 * 
	 */
	public TptpFofLogicFormula(){
		
	}
	
	/**
	 * 
	 * @param atoms
	 * @param functors
	 */
	public TptpFofLogicFormula(Set<TptpFofAtom> atoms,
			Set<TptpFofFunctor> functors) {
		super();
		this.atoms = atoms;
		this.functors = functors;
	}

	/*
	 * Getter
	 */
	public Set<TptpFofAtom> getAtoms() {
		return atoms;
	}

	
	public Set<TptpFofFunctor> getFunctors() {
		return functors;
	}


	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula#toString()
	 */
	public String toString(){
		// TODO implement
		return null;
	}

	
	
	
	
	
}
