package net.sf.tweety.logics.fol.syntax.tptp.fof;

import java.util.Set;

/**
 * The abstract class for quantified formulas
 * @author Bastian Wolf
 */
public abstract class TptpFofQuantifiedFormula extends TptpFofLogicFormula {


    /**
     *	Variables used in this formula
     */
    private Set<TptpFofVariable> variables;


    /**
     *	the actual formula
     */
    private TptpFofFormula formula;


    /**
     * 
     * @param variables
     * @param formula
     */
	public TptpFofQuantifiedFormula(
			Set<TptpFofVariable> variables, TptpFofFormula formula) {
		super();
		this.variables = variables;
		this.formula = formula;
	}

	/*
	 * Getter
	 */

	public Set<TptpFofVariable> getVariables() {
		return variables;
	}

	public TptpFofFormula getFormula() {
		return formula;
	}
    
	/*
	 * 
	 */
	public boolean isQuantifiedFormula(){
		return true;
	}
    
}
