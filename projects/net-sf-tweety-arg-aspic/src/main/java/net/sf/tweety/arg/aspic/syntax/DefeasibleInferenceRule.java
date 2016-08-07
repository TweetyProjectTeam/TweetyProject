package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;

import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * Defeasible implementation of <code>InferenceRule<T></code>
 *
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class DefeasibleInferenceRule<T extends Invertable> extends InferenceRule<T> {
	
	/**
	 * Constructs an empty instance
	 */
	public DefeasibleInferenceRule(){
	}

	/**
	 * Constructs a defeasible inference rule p => c 
	 * @param conclusion	^= p
	 * @param premise	^= c
	 */
	public DefeasibleInferenceRule(T conclusion, Collection<T> premise) {
		super(conclusion, premise);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.InferenceRule#isDefeasible()
	 */
	@Override
	public boolean isDefeasible() {
		return true;
	}

}
