package net.sf.tweety.beliefdynamics.mas;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.graphs.orders.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class provides for auxiliary functions used to compare
 * formulas and proofs of formulas wrt. the credibility of the
 * agents.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractCredibilityComparer {

	/**
	 *  The credibility order used to guide the comparison.
	 */
	private Order<Agent> credOrder;
	
	/**
	 * The information objects that hold the information which agents
	 * uttered the formulas.
	 */
	private Collection<InformationObject<PropositionalFormula>> formulas;
	
	/**
	 * Creates a new credibility comparer that is guided by the giving information which
	 * agents uttered the formulas and the credibility order. 
	 * @param formulas The information objects that hold the information which agents
	 * 		uttered the formulas.
	 * @param credOrder The credibility order used to guide the comparison.
	 */
	public AbstractCredibilityComparer(Collection<InformationObject<PropositionalFormula>> formulas, Order<Agent> credOrder){
		this.formulas = formulas;
		this.credOrder = credOrder;
	}
	
	/**
	 * Checks whether col1 is at least as preferred as col2 wrt. the credibility order.
	 * @param col1 a set of formulas
	 * @param col2 a set of formulas
	 * @return "true" iff col1 is at least as preferred as cl2 wrt. the credibility order. 
	 */
	protected boolean isAtLeastAsPreferredAs(Collection<? extends PropositionalFormula> col1, Collection<? extends PropositionalFormula> col2){
		for(PropositionalFormula f: col1){
			if(!this.isAtLeastAsPreferredAs(f, col2))
				return false;
		}		
		return true;
	}	
	
	/**
	 * Checks whether f is at least as preferred as some formula in "formulas"
	 * @param f some formula
	 * @param formulas a set of formulas
	 * @return "true" iff f is at least as preferred as each formula in "formulas"
	 */
	protected boolean isAtLeastAsPreferredAs(PropositionalFormula f, Collection<? extends PropositionalFormula> formulas){
		for(PropositionalFormula f2: formulas){
			if(this.isAtLeastAsPreferredAs(f, f2))
				return true;
		}		
		return false;
	}
	
	/**
	 * Checks whether f is at least as preferred as f2
	 * @param f some formula
	 * @param f2 some formula
	 * @return "true" iff f is at least as preferred as f2
	 */
	protected boolean isAtLeastAsPreferredAs(PropositionalFormula f, PropositionalFormula f2){
		// Retrieve all agents that uttered f
		Set<Agent> agents1 = new HashSet<Agent>();
		for(InformationObject<PropositionalFormula> i: this.formulas)
			if(i.getFormula().equals(f))
				agents1.add(i.getSource());
		// Retrieve all agents that uttered f2
		Set<Agent> agents2 = new HashSet<Agent>();
		for(InformationObject<PropositionalFormula> i: this.formulas)
			if(i.getFormula().equals(f2))
				agents2.add(i.getSource());
		// f is at least as preferred as f2 if there is one agent in agents1 such that no agent in
		// agents2 is more credible than that one.
		for(Agent a: agents1){
			boolean mostCredible = true;
			for(Agent b: agents2){
				if(this.credOrder.isOrderedBefore(b, a) && !this.credOrder.isOrderedBefore(a, b)){
					mostCredible = false;
					break;
				}
			}
			if(mostCredible)
				return true;
		}		
		return false;
	}
}
