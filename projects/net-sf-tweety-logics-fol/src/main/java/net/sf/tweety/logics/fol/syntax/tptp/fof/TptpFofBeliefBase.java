package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Iterator;
import java.util.Set;

/**
 * Currently unused
 * Meant to be used to collect a single tptp-fof-file in one class
 * @author Bastian Wolf
 *
 */

public class TptpFofBeliefBase {

	/**
	 * 
	 */
	private Set<TptpFofFormula> formulas;

	/*
	 * Constructor
	 */
	public TptpFofBeliefBase() {
		
	}
	
	/**
	 * 
	 * @param formulas
	 */
	public TptpFofBeliefBase(Set<TptpFofFormula> formulas) {
		
		this.formulas = formulas;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<TptpFofFormula> getFormulas() {
		return formulas;
	}
	
	/**
	 * 
	 * @param formulas
	 */
	public void setFormulas(Set<TptpFofFormula> formulas) {
		this.formulas = formulas;
	}

	/*
	 * 
	 */
	public boolean add(TptpFofFormula e) {
		return formulas.add(e);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return formulas.isEmpty();
	}

	/**
	 * 
	 * @return
	 */
	public Iterator<TptpFofFormula> iterator() {
		return formulas.iterator();
	}

	
}
