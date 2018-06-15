/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import net.sf.tweety.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.DefeasibleInferenceRule;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * Checks whether an argument defeats another argument
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class AspicAttack<T extends Invertable> extends Attack {
	
	/**
	 * Creates a new AspicAttack
	 * @param active	the attacking argument
	 * @param passive	the attacked argument
	 */
	public AspicAttack(AspicArgument<T> active, AspicArgument<T> passive) {
		super(active, passive);
	}
		
	/**
	 * Checks for defeats in a list of arguments
	 * @param args	a list of arguments
	 * @param order	an comparator which should compare the arguments in args 
	 * @return a list of all tuples (a,b) with a, b in args where a defeats b
	 */
	public static <T extends Invertable> Collection<AspicAttack<T>> determineAttackRelations(Collection<AspicArgument<T>> args, Comparator<AspicArgument<T>> order, RuleFormulaGenerator<T> rfgen) {
		Collection<AspicAttack<T>> successfull = new ArrayList<>();
		for (AspicArgument<T> active : args) 
			for (AspicArgument<T> passive : args)
				if (active != passive) {
					if(AspicAttack.isAttack(active,passive,rfgen,order)) {
						successfull.add(new AspicAttack<T>(active, passive));
					}
				}
		return successfull;
	}
	
	/**
	 * Determines whether the attack is successfull
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.syntax.Attack#toString()
	 */
	@Override
	public String toString() {
		return getAttacker() + " attacks " + getAttacked();
	}
	
}
