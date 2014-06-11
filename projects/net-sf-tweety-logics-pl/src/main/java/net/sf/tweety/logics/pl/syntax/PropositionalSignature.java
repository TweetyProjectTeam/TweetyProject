package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;

import net.sf.tweety.commons.SetSignature;


/**
 * This class captures the signature of a specific
 * propositional language.
 * @author Matthias Thimm, Sebastian Homann
 */
public class PropositionalSignature extends SetSignature<Proposition> {
	
	/**
	 * Creates a new propositional signature with the given set
	 * of propositions.
	 * @param propositions a set of propositions.
	 */
	public PropositionalSignature(Collection<? extends Proposition> propositions){
		super(propositions);
	}
	
	/**
	 * Creates a new (empty) propositional signature.
	 */
	public PropositionalSignature(){
		super();
	}
	
	/**
	 * Creates a new propositional signature with the given number
	 * of propositions (named "A0"..."Anumvars".
	 * @param numvars the number of variables.
	 */
	public PropositionalSignature(int numvars){
		super();
		for(int i = 0; i < numvars; i++)
			this.add(new Proposition("A" + i));
	}
	
	/**
	 * Adds the elements of another signature to the current one.
	 * @param other a propositional signature.
	 */
	public void add(PropositionalSignature other) {
		for(Proposition p : other) {
			this.add(p);
		}
	}
}
