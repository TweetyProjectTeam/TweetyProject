package net.sf.tweety.util.rules;

import java.util.*;

import net.sf.tweety.*;

/**
 * This interface models a general rule, i.e. a structure with some
 * premise (a set of formulas) and some conclusion (a single formula).
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public interface Rule<C extends Formula, P extends Formula> extends Formula {

	public boolean isFact();
	
	public boolean isConstraint();
	
	public void setConclusion(C conclusion);
	
	public void addPremise(P premise);
	
	public void addPremises(Collection<? extends P> premises);
	
	@Override
	public Signature getSignature();
	
	/**
	 * Returns the premise of this rule.
	 * @return the premise of this rule.
	 */
	public Collection<? extends P> getPremise();
	
	/**
	 * Returns the conclusion of this rule.
	 * @return the conclusion of this rule.
	 */
	public C getConclusion();
		
}
