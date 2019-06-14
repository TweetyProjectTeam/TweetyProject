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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.arg.adf.util.PlCollectors;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Implication;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.Tautology;

/**
 * TODO: generalize?
 * 
 * TODO: rewrite encoding in cnf
 * 
 * @author Mathias Hofer
 *
 */
public class SatEncoding {

	private Cache<Argument, Proposition> falses = new Cache<Argument, Proposition>(
			a -> new Proposition(a.getName() + "_f"));

	private Cache<Argument, Proposition> trues = new Cache<Argument, Proposition>(
			a -> new Proposition(a.getName() + "_t"));

	private Cache<Link, Proposition> links = new Cache<Link, Proposition>(
			l -> new Proposition("p_" + l.getFrom().getName() + "_" + l.getTo().getName()));

	/**
	 * TODO: the adf is not immutable, therefore adding stuff to it after
	 * SatEncoding was created may fuck things up?
	 */
	private AbstractDialecticalFramework adf;

	/**
	 * This formula ensures that the propositions s_t and s_f for an argument s
	 * cannot both be true at the same time.
	 * 
	 * Store globally, since it does not depend on interpretations.
	 * 
	 * TODO find better name, since it is not really xor (both can be false)
	 */
	private Collection<Disjunction> argsTrueXorFalse;

	public SatEncoding(AbstractDialecticalFramework adf) {
		this.adf = adf;
		this.argsTrueXorFalse = adf.arguments()
				.map(a -> new Disjunction(new Negation(trues.apply(a)), new Negation(falses.apply(a))))
				.collect(Collectors.toList());
	}

	public Disjunction refineLarger(Interpretation interpretation) {
		Disjunction sats = interpretation.satisfied().map(falses).collect(PlCollectors.toDisjunction());
		PlFormula unsats = interpretation.unsatisfied().map(trues).collect(PlCollectors.toDisjunction());
		PlFormula undec = interpretation.undecided().flatMap(a -> Stream.of(trues.apply(a), falses.apply(a)))
				.collect(PlCollectors.toDisjunction());
		return (Disjunction) new Disjunction(Arrays.asList(sats, unsats, undec)).collapseAssociativeFormulas();
	}

	/**
	 * 
	 * @param interpretation
	 * @return a clause
	 */
	public Disjunction refineUnequal(Interpretation interpretation) {
		Disjunction clause = new Disjunction();
		for (Argument a : adf) {
			if (interpretation.isSatisfied(a)) {
				clause.add(new Negation(trues.apply(a)));
			} else if (interpretation.isUnsatisfied(a)) {
				clause.add(new Negation(falses.apply(a)));
			} else {
				clause.add(trues.apply(a));
				clause.add(falses.apply(a));
			}
		}
		return clause;
	}

	public Collection<Disjunction> conflictFreeInterpretation() {
		Conjunction encoding = new Conjunction();
		for (Argument s : adf) {
			// link the arguments to their acceptance conditions
			PlFormula acc = adf.getAcceptanceCondition(s).toPlFormula(r -> links.apply(adf.link(r, s)));
			Implication acSat = new Implication(trues.apply(s), acc);
			Implication acUnsat = new Implication(falses.apply(s), new Negation(acc));
			encoding.add(acSat);
			encoding.add(acUnsat);

			Conjunction posLinks = new Conjunction();
			Conjunction negLinks = new Conjunction();
			for (Link relation : (Iterable<Link>) adf.linksToChildren(s)::iterator) {
				posLinks.add(links.apply(relation));
				negLinks.add(new Negation(links.apply(relation)));
			}
			encoding.add(new Implication(trues.apply(s), posLinks));
			encoding.add(new Implication(falses.apply(s), negLinks));
		}

		return encoding.toCnf().stream().map(x -> (Disjunction) x).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param interpretation
	 * @return a collection of clauses
	 */
	public Collection<Disjunction> largerInterpretation(Interpretation interpretation) {
		// fix values based on the given interpretation
		List<Disjunction> truesClauses = interpretation.satisfied().map(trues).map(x -> new Disjunction(Arrays.asList(x)))
				.collect(Collectors.toList());
		List<Disjunction> falsesClauses = interpretation.unsatisfied().map(falses)
				.map(x -> new Disjunction(Arrays.asList(x))).collect(Collectors.toList());

		// try to fix a truth value for the undecided arguments in the given
		// interpretation. if it is possible, then the resulting interpretation
		// is strictly "larger".
		Disjunction undecidedClause = interpretation.undecided().flatMap(a -> Stream.of(trues.apply(a), falses.apply(a)))
				.collect(PlCollectors.toDisjunction());

		Collection<Disjunction> clauses = new ArrayList<Disjunction>(truesClauses.size() + falsesClauses.size() + argsTrueXorFalse.size() + 1);
		clauses.addAll(truesClauses);
		clauses.addAll(falsesClauses);
		clauses.addAll(argsTrueXorFalse);
		clauses.add(undecidedClause);
		return clauses;
	}

	public Collection<Disjunction> bipolar() {
		Conjunction encoding = new Conjunction();
		for (Argument r : adf) {
			Conjunction conj1 = adf.linksToParent(r).filter(l -> l.getLinkType() == LinkType.ATTACKING)
					.map(l -> new Implication(new Negation(falses.apply(l.getFrom())), links.apply(l)))
					.collect(PlCollectors.toConjunction());
			Conjunction conj2 = adf.linksToParent(r).filter(l -> l.getLinkType() == LinkType.SUPPORTING)
					.map(l -> new Implication(new Negation(trues.apply(l.getFrom())), new Negation(links.apply(l))))
					.collect(PlCollectors.toConjunction());
			Conjunction conj3 = adf.linksToParent(r).filter(l -> l.getLinkType() == LinkType.ATTACKING)
					.map(l -> new Implication(new Negation(trues.apply(l.getFrom())), new Negation(links.apply(l))))
					.collect(PlCollectors.toConjunction());
			Conjunction conj4 = adf.linksToParent(r).filter(l -> l.getLinkType() == LinkType.SUPPORTING)
					.map(l -> new Implication(new Negation(falses.apply(l.getFrom())), links.apply(l)))
					.collect(PlCollectors.toConjunction());

			encoding.add(new Implication(trues.apply(r), new Conjunction(conj1, conj2)));
			encoding.add(new Implication(falses.apply(r), new Conjunction(conj3, conj4)));
		}
		
		return encoding.toCnf().stream().map(x -> (Disjunction) x).collect(Collectors.toList());
	}

	public Collection<Disjunction> kBipolar(Interpretation interpretation) {
		Conjunction encoding = new Conjunction();
		for (Argument s : adf) {
			// set of arguments r with (r,s) non-bipolar and I(r) = u
			Set<Argument> xs = adf.linksToParent(s).filter(l -> l.getLinkType() == LinkType.DEPENDENT)
					.map(l -> l.getFrom()).filter(r -> interpretation.isUndecided(r)).collect(Collectors.toSet());

			SubsetIterator<Argument> subsetIterator = new DefaultSubsetIterator<Argument>(xs);

			Conjunction conj1 = new Conjunction();
			Conjunction conj2 = new Conjunction();
			while (subsetIterator.hasNext()) {
				Set<Argument> subset = subsetIterator.next();
				PlFormula acc = adf.getAcceptanceCondition(s).toPlFormula(r -> xs.contains(r)
						? links.apply(adf.link(r, s)) : (subset.contains(r) ? new Tautology() : new Contradiction()));

				Disjunction disj1 = subset.stream().map(falses).collect(PlCollectors.toDisjunction());
				Disjunction disj2 = xs.stream().filter(r -> !subset.contains(r)).map(trues)
						.collect(PlCollectors.toDisjunction());
				conj1.add(new Disjunction(Arrays.asList(acc, disj1, disj2)));
				conj2.add(new Disjunction(Arrays.asList(new Negation(acc), disj1, disj2)));
			}
			encoding.add(new Implication(trues.apply(s), conj1));
			encoding.add(new Implication(falses.apply(s), conj2));
		}

		return encoding.toCnf().stream().map(x -> (Disjunction) x).collect(Collectors.toList());
	}

	public Collection<Disjunction> verifyAdmissible(Interpretation interpretation) {
		Cache<Argument, PlFormula> vars = new Cache<Argument, PlFormula>(s -> new Proposition(s.getName()));
		Conjunction conj = new Conjunction();
		Disjunction disj = new Disjunction();
		for (Argument s : adf) {
			PlFormula acc = adf.getAcceptanceCondition(s).toPlFormula(vars);
			if (interpretation.isSatisfied(s)) {
				conj.add(vars.apply(s));
				disj.add(new Negation(acc));
			} else if (interpretation.isUnsatisfied(s)) {
				conj.add(new Negation(vars.apply(s)));
				disj.add(acc);
			}
		}
		PlFormula encoding = new Conjunction(conj, disj);
		return encoding.toCnf().stream().map(x -> (Disjunction) x).collect(Collectors.toList());
	}

	public Interpretation interpretationFromWitness(
			net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness) {
		Map<Argument, Boolean> assignment = new HashMap<Argument, Boolean>();
		for (Argument a : adf) {
			if (witness.satisfies(trues.apply(a))) {
				assignment.put(a, true);
			} else if (witness.satisfies(falses.apply(a))) {
				assignment.put(a, false);
			} else {
				assignment.put(a, null);
			}
		}

		return new Interpretation(assignment);
	}

}
