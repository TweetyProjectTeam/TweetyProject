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
package org.tweetyproject.arg.aspic.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.tweetyproject.arg.aspic.ruleformulagenerator.RuleFormulaGenerator;
import org.tweetyproject.arg.aspic.semantics.AspicAttack;
import org.tweetyproject.arg.aspic.syntax.AspicArgument;
import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.syntax.DefeasibleInferenceRule;
import org.tweetyproject.arg.aspic.syntax.InferenceRule;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 * Computes a restricted AF by only considering relevant arguments to a query. An optional 
 * probability value for including only a certain subset of relevant arguments turns this
 * reasoner into an approximative reasoner
 *  
 * @author Tjitze Rienstra, Matthias Thimm
 *
 * @param <T> the type of formulas
 */
public class DirectionalAspicReasoner<T extends Invertable> extends AbstractAspicReasoner<T> {

	private double prob = 1.0d;
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 */
	public DirectionalAspicReasoner(AbstractExtensionReasoner aafReasoner) {
		this(aafReasoner,1d);
	}
	
	/**
	 * Creates a new instance
	 * @param aafReasoner Underlying reasoner for AAFs. 
	 * @param prob the probability of including a certain relevant argument
	 */
	public DirectionalAspicReasoner(AbstractExtensionReasoner aafReasoner, double prob) {
		super(aafReasoner);
		this.prob = prob;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aspic.reasoner.AbstractAspicReasoner#getDungTheory(org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory, org.tweetyproject.logics.commons.syntax.interfaces.Invertable)
	 */
	@Override
	public DungTheory getDungTheory(AspicArgumentationTheory<T> aat, T query) {
		return asRestrictedDungTheory(aat, false, query);
	}
	
	/**
	 * Generate restricted AF based on given theory and conclusion. 
	 * 
	 * The restricted AF contains only arguments with the given concusion, all the
	 * attackers of this argument, the attackers of those arguments, and so on.
	 * 
	 * As long as the semantics that is employed satisfies directionality, this 
	 * restricted AF is sufficient to determine the correct status of the conclusion.
	 * 
	 * @param aat The aspic theory to generate arguments from
	 * @param simplifyArgumentStructure a boolean, see org.tweetyproject.arg.aspic.AbstractAspicReasoner#getDungTheory(org.tweetyproject.arg.aspic.AspicArgumentationTheory, org.tweetyproject.commons.Formula)
	 * @param conc Conclusion to generate restricted AF for
	 * @return The restricted AF
	 */
	public DungTheory asRestrictedDungTheory(AspicArgumentationTheory<T> aat, boolean simplifyArgumentStructure, T conc) {
		Collection<AspicArgument<T>> args = getArgsRec(aat, conc);
		DungTheory dung_theory = new DungTheory();
		dung_theory.addAll(args);
		dung_theory.addAllAttacks(AspicAttack.determineAttackRelations(args, aat.getOrder(), aat.getRuleFormulaGenerator()));
		if(!simplifyArgumentStructure)
			return dung_theory;
		DungTheory dung_theory2 = new DungTheory();
		Map<Argument,Argument> old2new = new HashMap<>();
		int idx = 0;
		for(Argument a: dung_theory) {
			Argument b = new Argument("A"+idx++);
			old2new.put(a, b);
			dung_theory2.add(b);
		}
		for(Attack att: dung_theory.getAttacks()) {
			dung_theory2.add(new Attack(old2new.get(att.getAttacker()),old2new.get(att.getAttacked())));
		}		
		return dung_theory2;
	}

	private Set<AspicArgument<T>> constructArgsWithConclusion(AspicArgumentationTheory<T> aat, T conc, Set<T> conclusions) {
	
		conclusions = new HashSet<>(conclusions);
		conclusions.add(conc);

		Set<AspicArgument<T>> result = new LinkedHashSet<>();
		outer: for (InferenceRule<T> rule: aat.getRulesWithConclusion(conc)) {
			// Skip rules where conclusion is repeated as premise
			for (T premise: rule.getPremise()) {
				if (conclusions.contains(premise)) {
					continue outer;
				}
			}
			// Set up list of partial args (i.e. may not contain necessary sub args)
			Set<AspicArgument<T>> newPartialArgs = new LinkedHashSet<>();
			Set<AspicArgument<T>> partialArgs = new LinkedHashSet<>();
			partialArgs.add(new AspicArgument<>(rule));

			// For each premise ...
			for (T premise: rule.getPremise()) {
				// Construct all sub args
				Set<AspicArgument<T>> subArgs = constructArgsWithConclusion(aat, premise, conclusions);
				
				// Update and replace all partial args
				for (AspicArgument<T> partialArg: partialArgs) {
					for (AspicArgument<T> subArg: subArgs) {
						AspicArgument<T> partialArgCopy = partialArg.shallowCopy();
						partialArgCopy.addDirectSub(subArg);
						newPartialArgs.add(partialArgCopy);
					}
				}
				partialArgs.clear();
				partialArgs.addAll(newPartialArgs);
				newPartialArgs.clear();

			}
			result.addAll(partialArgs);
		}
		return result;
	}
	
	
	/**
	 * Gets the arguments recursively
	 * @param aat the argumentation theory
	 * @param conc the conclusion
	 * @return	arguments with given conclusion plus recursively all attackers 
	 */
	private Collection<AspicArgument<T>> getArgsRec(AspicArgumentationTheory<T> aat, T conc) {
		
		Random r = new Random();
		
		Set<AspicArgument<T>> newArgs = new LinkedHashSet<>();
		Set<AspicArgument<T>> argsDone = new HashSet<>();
		Set<T> conclusionsDone = new HashSet<>();

		Set<AspicArgument<T>> args = new LinkedHashSet<>(constructArgsWithConclusion(aat, conc, new HashSet<>()));
		conclusionsDone.add(conc);
		
		boolean repeat = true;
		while (repeat) {
			repeat = false;
			newArgs.clear();
			for (AspicArgument<T> argument: args) {
				if (argsDone.contains(argument)) continue;
				argsDone.add(argument);
				if (r.nextFloat() < prob) {
					for (T conclusion: getAttackingConclusions(argument, aat.getRuleFormulaGenerator())) {
						if (!conclusionsDone.contains(conclusion)) {
							conclusionsDone.add(conclusion);
							newArgs.addAll(constructArgsWithConclusion(aat, conclusion, new HashSet<>()));
						}
					}
				}
			}
			if (!newArgs.isEmpty()) {
				args.addAll(newArgs);
				repeat = true;
			}
		}
		
		return args;
	}
	
	/**
	 * Returns all "attacking conclusions" of the given argument.
	 * 
	 * An attacking conclusion of an argument is a conclusion that, if it is a conclusion of 
 	 * another argument, then this argument will attack it.
	 * 
	 * @param arg Argument to generate attacking conclusions
	 * @param rfgen Rule formula generator in use.
	 * @return Attacking conclusions of argument.
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> getAttackingConclusions(AspicArgument<T> arg, RuleFormulaGenerator<T> rfgen) {
		Collection<T> cs = new ArrayList<>();
		Objects.requireNonNull(rfgen);
		// Add undercutters
		for (InferenceRule<T> dr: arg.getDefeasibleRules()) {
			cs.add((T)rfgen.getRuleFormula((DefeasibleInferenceRule<T>) dr).complement());
		}
		// Add rebutters
		for (AspicArgument<T> a : arg.getDefeasibleSubs()) {
			cs.add((T)a.getTopRule().getConclusion().complement());
		}
		// Add underminers
		for (AspicArgument<T> a : arg.getOrdinaryPremises()) {
			cs.add((T)a.getTopRule().getConclusion().complement());
		}
		return cs;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "dirRand-" + this.prob;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
