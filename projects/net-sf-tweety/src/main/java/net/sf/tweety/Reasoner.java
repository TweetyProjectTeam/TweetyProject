package net.sf.tweety;

/**
 * A reasoner specifies a specific inference operation for a given language.
 * @author Matthias Thimm
 * 
 * @param S the class of belief bases for this reasoner.
 * @param T the class of formulas for this reasoner
 */
public abstract class Reasoner {
	
	/**
	 * The knowledge base on which reasoning is performed.
	 */
	private BeliefBase beliefBase;
	
	/**
	 * Creates a new reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 */
	public Reasoner(BeliefBase beliefBase){
		this.beliefBase = beliefBase;
	}
	
	/**
	 * This method determines the answer of the given query
	 * wrt. to the given knowledge base.
	 * @param query a query.
	 * @return the answer to the query.
	 */
	public abstract Answer query(Formula query);
	
	/**
	 * Returns the knowledge base of this reasoner.
	 * @return the knowledge base of this reasoner.
	 */
	public BeliefBase getKnowledgBase(){
		return this.beliefBase;
	}
}
