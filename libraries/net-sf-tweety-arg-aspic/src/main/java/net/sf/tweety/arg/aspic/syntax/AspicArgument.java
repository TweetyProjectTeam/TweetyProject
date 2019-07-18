/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * 
 * @author Nils Geilen
 *
 * An argument according to the ASPIC+ specification
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */

public class AspicArgument<T extends Invertable> extends Argument {
	
	/** The conclusion of the argument's top rule **/
	private T conc = null;;
	/** The argument's direct children, whose conclusions fit its prerequisites **/
	private List<AspicArgument<T>> directsubs = new ArrayList<>();
	/** The srgument's top rule **/
	private InferenceRule<T> toprule = null;
	
	
	/**
	 * Creates an empty Argument 
	 * @param toprule the argument's TopRule
	 * @param directsubs the direct sub arguments
	 */
	public AspicArgument(InferenceRule<T> toprule, Collection<AspicArgument<T>> directsubs) {
		super(null);
		this.toprule = toprule;
		conc = toprule.getConclusion();	
		this.directsubs = new LinkedList<AspicArgument<T>>(directsubs);
		
		generateName();
	}
	
	/**
	 * Creates an empty Argument 
	 * @param toprule the argument's TopRule
	 */
	public AspicArgument(InferenceRule<T> toprule) {
		super(null);
		this.toprule = toprule;
		conc = toprule.getConclusion();	
		
		generateName();
	}
	
	/**
	 * Generates a unique identifying name for this argument, which describes it 
	 * and is used to determine equality 
	 */
	private void generateName() {
		Collections.sort(directsubs, new Comparator<AspicArgument<T>>() {
			@Override
			public int compare(AspicArgument<T> o1, AspicArgument<T> o2) {
				return o1.hashCode() - o2.hashCode();
			}
		});
		setName(toprule + (directsubs.isEmpty()  ? "": " "+directsubs ));
	}
		
	/**
	 * Checks whether this has a defeasible subrule, premises do not count as subrules
	 * @return whether this has a defeasible subrule
	 */
	public boolean hasDefeasibleSub() {
		return !getDefeasibleRules().isEmpty();
	}
	
	/**
	 * An argument is strict iff it does not contain defeasible subrules
	 * @return	true iff this argument is strict
	 */
	public boolean isStrict() {
		return !hasDefeasibleSub();
	}
	
	/**
	 * An argument is firm iff it does not contain ordinary premises
	 * @return	iff this is firm
	 */
	public boolean isFirm() {
		return getOrdinaryPremises().isEmpty();
	}
	
	/**
	 * Checks wheter this is a direct or indirect subargument of <code>arg</code>
	 * @param arg	an ASPIC argument
	 * @return	true iff this is in Sub(arg)
	 */
	public boolean isSubArgumentOf (AspicArgument<T> arg) {
		if (equals(arg))
			return true;
		for (AspicArgument<T> sub : arg.getDirectSubs())
			if (equals(sub))
				return true;
		return false;
	}
	
/*	public Collection<AspicInferenceRule> getPrems() {
		Collection<AspicInferenceRule> result = new HashSet<>();
		if(toprule.isFact())
			result.add(toprule);
		for(AspicArgument arg : directsubs)
			result.addAll(arg.getPrems());
		return result;
	}*/
	
	/**
	 * @return all ordinary premises
	 */
	public Collection<AspicArgument<T>> getOrdinaryPremises() {
		Collection<AspicArgument<T>> result = new HashSet<>();
		if (toprule.isFact() && toprule.isDefeasible()) {
			result.add(this);
			return result;
		}
		for(AspicArgument<T> a: directsubs)
			result.addAll(a.getOrdinaryPremises());
		return result;
	}
	
	/**
	 * Returns Conc according to the ASPIC+ specification
	 * @return the top rule's conclusion
	 */
	public T getConclusion() {
		return conc;
	}
	
	/**
	 * Change the conclusion
	 * @param conc the new conclusion
	 */
	public void setConclusion(T conc) {
		this.conc = conc;
		generateName();
	}
	
	/**
	 * returns the Subs according to the ASPIC+ specification
	 * @return all subarguments including this
	 */
	public Collection<AspicArgument<T>> getAllSubs() {
		Collection<AspicArgument<T>> result = new HashSet<>();
		result.add(this);
		for(AspicArgument<T> a : directsubs)
			result.addAll(a.getAllSubs());
		return result;
	}
	
	/**
	 * Determines subarguments with defeasible toprules, premises do not count as subrules
	 * @return all arguments in Subs with defeasible top rules
	 */
	public Collection<AspicArgument<T>> getDefeasibleSubs() {
		Collection<AspicArgument<T>> result = new HashSet<>();
		if(toprule.isFact())
			return result;
		if(toprule.isDefeasible())
			result.add(this);
		for(AspicArgument<T> arg : directsubs)
			result.addAll(arg.getDefeasibleSubs());
		return result;
	}
	
	/**
	 * Returns all defeasible subrules of the level where the first defeasible subrule appears,
	 * i.e. the set of defeasible subrules with the smallest depth
	 * @return	the last defeasible rules
	 */
	public Collection<InferenceRule<T>> getListLastDefeasibleRules() {
		List<AspicArgument<T>> list = new ArrayList<>();
		list.add(this);
		while (true) {
			List<InferenceRule<T>> result = new ArrayList<>();
			for(AspicArgument<T> arg : list) {
				if(arg.getTopRule().isDefeasible())
					result.add(arg.getTopRule());
			}
			if(! result.isEmpty())
				return result;
			
			List<AspicArgument<T>> next = new ArrayList<>();
			for(AspicArgument<T> arg : list) {
				next.addAll(arg.getDirectSubs());
			}
			list = next;
			if(list.isEmpty())
				return new ArrayList<>();
		}
	}
	
	/**
	 * Returns the DefRules according to ASPIC+ specification,
	 * i.e. the defeasible toprules of subarguments 
	 * @return this argument's defeasible rules
	 */
	public Collection<InferenceRule<T>> getDefeasibleRules() {
		Collection<InferenceRule<T>> result = new HashSet<>();
		for(AspicArgument<T> a : getDefeasibleSubs())
			result.add(a.toprule);
		return result;
	}
	
	/**
	 * Returns all rules appearing in this argument.
	 * @return all rules appearing in this argument.
	 */
	public Collection<InferenceRule<T>> getAllRules(){
		Collection<InferenceRule<T>> result = new HashSet<>();
		result.add(this.toprule);
		for(AspicArgument<T> sub: this.directsubs)
			result.addAll(sub.getAllRules());
		return result;
	}
	
	/**
	 * Returns all conclusions appearing in this argument.
	 * @return all conclusions appearing in this argument.
	 */
	public Collection<T> getAllConclusions(){
		Collection<T> conc = new HashSet<>();
		for(InferenceRule<T> rule : this.getAllRules())
			conc.add(rule.getConclusion());
		return conc;
	}
	
	/**
	 * The argument's direct children, whose conclusions fit its prerequisites
	 * @return  the direct subrules
	 */
	public Collection<AspicArgument<T>> getDirectSubs() {
		return directsubs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((conc == null) ? 0 : conc.hashCode());
		result = prime * result + ((directsubs == null) ? 0 : directsubs.hashCode());
		result = prime * result + ((toprule == null) ? 0 : toprule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AspicArgument<?> other = (AspicArgument<?>) obj;
		if (conc == null) {
			if (other.conc != null)
				return false;
		} else if (!conc.equals(other.conc))
			return false;
		if (directsubs == null) {
			if (other.directsubs != null)
				return false;
		} else if (!directsubs.equals(other.directsubs))
			return false;
		if (toprule == null) {
			if (other.toprule != null)
				return false;
		} else if (!toprule.equals(other.toprule))
			return false;
		return true;
	}

	/**
	 * Adds a subargument
	 * @param sub	to be added
	 */
	public void addDirectSub(AspicArgument<T> sub) {
		directsubs.add(sub);
		generateName();
	}

	/**
	 * Retruns the TopRule according to ASPIC+ specification
	 * @return the top rule
	 */
	public InferenceRule<T> getTopRule() {
		return toprule;
	}
	
	/**
	 * Changes the TopRule
	 * @param toprule the new TopRule
	 */
	public void setTopRule(InferenceRule<T> toprule) {
		this.toprule = toprule;
		generateName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	
	
	/**
	 * Determines whether the attack is successful
	 * @param active the active argument
	 * @param passive  the passive argument
	 * @param rfgen  a rule formula generator 
	 * @param order a comparator
	 * @param <T> the type of formulas
	 * @return true iff the attack is successful
	 */
	public static <T extends Invertable> boolean isAttack(AspicArgument<T> active, AspicArgument<T> passive, RuleFormulaGenerator<T> rfgen,Comparator<AspicArgument<T>> order) {
		Collection<AspicArgument<T>> defargs = passive.getDefeasibleSubs();		
		// default order
		if(order == null)
			order = new Comparator<AspicArgument<T>>() {
				@Override
				public int compare(AspicArgument<T> o1, AspicArgument<T> o2) {
					return 0;
				}
			};		
		/*
		 * Undercutting
		 */
		for (AspicArgument<T> a : defargs){
			if(rfgen == null)
				throw new NullPointerException("AspicAttack: RuleFormulaGenerator missing");
			if(active.getConclusion().equals(rfgen.getRuleFormula((DefeasibleInferenceRule<T>)a.getTopRule()).complement())) {
				return true;
			}
		}
		/*
		 * Rebuttal
		 */
		for (AspicArgument<T> a : defargs)
			if(active.getConclusion().equals(a.getConclusion().complement())) {
				if(order.compare(active, a) >= 0) 
					return true;				
			}
		/*
		 * Undemining
		 */
		for (AspicArgument<T> a : passive.getOrdinaryPremises())
				if(active.getConclusion().equals(a.getConclusion().complement())) {
					if(order.compare(active, a) >= 0)
						return true;					
				}
		return false;
	}
	
	public AspicArgument<T> shallowCopy() {
		AspicArgument<T> copy = new AspicArgument<T>(toprule);
		directsubs.forEach(sub -> copy.addDirectSub(sub));
		return copy;
	}

}
