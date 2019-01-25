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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.tweety.arg.aspic.semantics.AspicAttack;
import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.syntax.InferenceRule;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * This class implements an approximate reasoner for ASPIC+ that randomly
 * samples arguments.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas.
 */
public class RandomAspicReasoner<T extends Invertable> extends AbstractAspicReasoner<T> {

	/**
	 * Random number generator
	 */
	private Random rand;
	
	/**
	 * The maximal number of arguments constructed by this reasoner.
	 */
	private int maxArguments;
	
	/**
	 * The maximal number of duplicates tolerated before this reasoner 
	 * cancels looking for more arguments (even if <code>maxArguments</code> is
	 * not yet reached).
	 */
	private int maxDuplicates;
	
	/**
	 * For convenience methods on sets. 
	 */
	private SetTools<T> setTools = new SetTools<T>();
	
	/**
	 * Creates a new instance.
	 * @param aafReasoner Underlying reasoner for AAFs.
	 * @param maxArguments  The maximal number of arguments constructed by this reasoner.
	 * @param maxDuplicates The maximal number of duplicates tolerated before this reasoner 
	 * cancels looking for more arguments (even if <code>maxArguments</code> is
	 * not yet reached).
	 */
	public RandomAspicReasoner(AbstractExtensionReasoner aafReasoner, int maxArguments, int maxDuplicates) {
		super(aafReasoner);
		this.maxArguments = maxArguments;
		this.maxDuplicates = maxDuplicates;
		this.rand = new Random();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.reasoner.AbstractAspicReasoner#getDungTheory(net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory, net.sf.tweety.logics.commons.syntax.interfaces.Invertable)
	 */
	@Override
	public DungTheory getDungTheory(AspicArgumentationTheory<T> aat, T query) {		
		// determine part of the theory needed for the query
		AspicArgumentationTheory<T> module = new AspicArgumentationTheory<T>(aat.getRuleFormulaGenerator());
		module.addAll(aat.getSyntacticModule(query));
		// special case: empty module
		if(module.isEmpty())
			return new DungTheory();
		// special case: there are no rules with empty body in the module, so no argument can be constructed
		boolean premiseFound = false;
		for(InferenceRule<T> rule: module) {
			if(rule.getPremise().isEmpty()) {
				premiseFound = true;
				break;
			}				
		}
		if(!premiseFound)
			return new DungTheory();
		Collection<AspicArgument<T>> args = new HashSet<AspicArgument<T>>();		
		int duplicates = 0;
		// prepare rules by indexing them by the head
		Map<T,List<InferenceRule<T>>> rules = new HashMap<>();
		for(InferenceRule<T> rule: module) {
			if(!rules.containsKey(rule.getConclusion()))
				rules.put(rule.getConclusion(), new LinkedList<InferenceRule<T>>());
			rules.get(rule.getConclusion()).add(rule);
		}
		// sample arguments
		for(int i = 0; i < this.maxArguments; i++) {
			if(!args.add(this.sampleArgument(rules)))
				duplicates++;			
			if(duplicates > this.maxDuplicates)
				break;
		}
		DungTheory aaf = new DungTheory();
		aaf.addAll(args);
		aaf.addAllAttacks(AspicAttack.determineAttackRelations(args, aat.getOrder(), aat.getRuleFormulaGenerator()));
		return aaf;
	}

	/**
	 * Samples a single ASPIC argument via a random search.
	 * @param rules some rules 
	 * @return some ASPIC argument
	 */
	private AspicArgument<T> sampleArgument(Map<T,List<InferenceRule<T>>> rules){
		AspicArgument<T> arg = null;
		do {
			T conclusion = this.setTools.randomElement(rules.keySet());
			Set<T> conclusions = new HashSet<T>();
			conclusions.add(conclusion);
			arg = this.sampleArgument(rules,conclusion,conclusions);
		}while(arg==null);
		return arg;
	}
	
	/**
	 * Samples a single ASPIC argument for the given conclusion via a random search.
	 * 
	 * @param rules some rules 
	 * @param conclusion some conclusion
	 * @param conclusions the conclusions accumulated so far (from the top)
	 * @return some ASPIC argument
	 */
	private AspicArgument<T> sampleArgument(Map<T,List<InferenceRule<T>>> rules, T conclusion, Set<T> conclusions){
		if(!rules.containsKey(conclusion))
			return null;
		// candidate rules for the top rule
		List<InferenceRule<T>> candidates = new LinkedList<InferenceRule<T>>();		
		for(InferenceRule<T> rule: rules.get(conclusion)) {
			Set<T> premise = new HashSet<>(rule.getPremise());
			premise.retainAll(conclusions);
			if(premise.isEmpty())
				candidates.add(rule);			
		}
		if(candidates.isEmpty())
			return null;
		AspicArgument<T> arg = new AspicArgument<T>(candidates.get(this.rand.nextInt(candidates.size())));
		for(T prem: arg.getTopRule().getPremise()) {
			Set<T> newConclusions = new HashSet<T>(conclusions);
			newConclusions.add(prem);
			AspicArgument<T> sub = this.sampleArgument(rules,prem,newConclusions);
			if(sub == null)
				return null;
			arg.addDirectSub(sub);
		}		
		return arg;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "rand-" + this.maxArguments +"-"+this.maxDuplicates;
	}
}
