package net.sf.tweety.beliefdynamics.mas;

import net.sf.tweety.*;
import net.sf.tweety.agents.*;

/**
 * This class represents a formula annotated with the source of the formula.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of the inner formula.
 */
public class InformationObject<T extends Formula> implements Formula{
	
	/**
	 * The source of this information object.
	 */
	private Agent source;
	
	/**
	 * The formula of this information object
	 */
	private T formula;
	
	/**
	 * Creates a new information object for the given formula
	 * and the given source.
	 * @param formula some formula.
	 * @param source some agent.
	 */
	public InformationObject(T formula, Agent source){
		this.formula = formula;
		this.source = source;
	}
	
	/**
	 * Returns the source of this information object.
	 * @return the source of this information object.
	 */
	public Agent getSource(){
		return this.source;
	}
	
	/**
	 * Returns the formula of this information object.
	 * @return the formula of this information object.
	 */
	public T getFormula(){
		return this.formula;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Formula#getSignature()
	 */
	@Override
	public Signature getSignature() {
		return this.formula.getSignature();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.source + ":" + this.formula;
	}
}
