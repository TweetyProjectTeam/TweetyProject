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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.DefinitionalCNFTransform;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

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

	public SatEncoding(AbstractDialecticalFramework adf) {
		this.adf = adf;
	}

	public Disjunction refineLarger(Interpretation interpretation) {
		Disjunction encoding = new Disjunction();
		interpretation.satisfied().map(falses).forEach(encoding::add);
		interpretation.unsatisfied().map(trues).forEach(encoding::add);
		interpretation.undecided().flatMap(a -> Stream.of(trues.apply(a), falses.apply(a))).forEach(encoding::add);
		return encoding;
	}

	/**
	 * 
	 * @param interpretation
	 * @return a clause
	 */
	public Disjunction refineUnequal(Interpretation interpretation) {
		Disjunction encoding = new Disjunction();
		interpretation.satisfied().map(trues).map(Negation::new).forEach(encoding::add);
		interpretation.unsatisfied().map(falses).map(Negation::new).forEach(encoding::add);
		interpretation.undecided().flatMap(x -> Stream.of(trues.apply(x), falses.apply(x))).forEach(encoding::add);
		return encoding;
	}

	public Collection<Disjunction> conflictFreeInterpretation() {
		List<Disjunction> encoding = new LinkedList<Disjunction>();
		for (Argument s : adf) {
			DefinitionalCNFTransform transform = new DefinitionalCNFTransform(r -> links.apply(adf.link(r, s)));
			AcceptanceCondition acc = adf.getAcceptanceCondition(s);
			Proposition accName = acc.collect(transform, List::add, encoding);

			// link the arguments to their acceptance conditions
			Disjunction acSat = new Disjunction(new Negation(trues.apply(s)), accName);
			Disjunction acUnsat = new Disjunction(new Negation(falses.apply(s)), new Negation(accName));
			encoding.add(acSat);
			encoding.add(acUnsat);

			// draw connection between argument and outgoing links
			for (Link relation : (Iterable<Link>) adf.linksToChildren(s)::iterator) {
				encoding.add(new Disjunction(new Negation(trues.apply(s)), links.apply(relation)));
				encoding.add(new Disjunction(new Negation(falses.apply(s)), new Negation(links.apply(relation))));
			}

			// make sure that we never satisfy s_t and s_f at the same time
			Disjunction eitherTrueOrFalse = new Disjunction(new Negation(trues.apply(s)),
					new Negation(falses.apply(s)));
			encoding.add(eitherTrueOrFalse);
		}

		return encoding;
	}

	public Collection<Disjunction> fixTwoValued(Interpretation interpretation) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();

		for (Argument a : (Iterable<Argument>) interpretation.satisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(trues.apply(a));
			encoding.add(clause);
		}

		for (Argument a : (Iterable<Argument>) interpretation.unsatisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(falses.apply(a));
			encoding.add(clause);
		}

		return encoding;
	}

	public Proposition getTrueRepresentation(Argument s) {
		return trues.apply(s);
	}

	public Proposition getFalseRepresentation(Argument s) {
		return falses.apply(s);
	}

	public Pair<Proposition, Collection<Disjunction>> definitionalAcc(Argument s) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		DefinitionalCNFTransform transform = new DefinitionalCNFTransform(r -> links.apply(adf.link(r, s)));
		AcceptanceCondition acc = adf.getAcceptanceCondition(s);
		Proposition accName = acc.collect(transform, Collection::add, encoding);

		return new Pair<Proposition, Collection<Disjunction>>(accName, encoding);
	}

	public Pair<Proposition, Collection<Disjunction>> verifyComplete(Interpretation interpretation, Argument s) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		// Cache<Argument, Proposition> vars = new Cache<Argument,
		// Proposition>(a -> new Proposition(a.getName()));
		Proposition resultAccName = new Proposition();
		for (Argument a : adf) {
			if (interpretation.isSatisfied(a)) {
				Disjunction clause = new Disjunction();
				clause.add(trues.apply(a));
				encoding.add(clause);
			}

			if (interpretation.isUnsatisfied(a)) {
				Disjunction clause = new Disjunction();
				clause.add(falses.apply(a));
				encoding.add(clause);
			}

			DefinitionalCNFTransform transform = new DefinitionalCNFTransform(r -> links.apply(adf.link(r, a)));
			AcceptanceCondition acc = adf.getAcceptanceCondition(a);
			Proposition accName = acc.collect(transform, Collection::add, encoding);
			if (a == s) {
				resultAccName = accName;
			}
			// link the arguments to their acceptance conditions
			Disjunction acSat = new Disjunction(new Negation(trues.apply(a)), accName);
			Disjunction acUnsat = new Disjunction(new Negation(falses.apply(a)), new Negation(accName));
			encoding.add(acSat);
			encoding.add(acUnsat);

			// draw connection between argument and outgoing links
			for (Link relation : (Iterable<Link>) adf.linksToChildren(a)::iterator) {
				encoding.add(new Disjunction(new Negation(trues.apply(a)), links.apply(relation)));
				encoding.add(new Disjunction(new Negation(falses.apply(a)), new Negation(links.apply(relation))));
			}

			// make sure that we never satisfy s_t and s_f at the same time
			Disjunction eitherTrueOrFalse = new Disjunction(new Negation(trues.apply(a)),
					new Negation(falses.apply(a)));
			encoding.add(eitherTrueOrFalse);
		}
		return new Pair<Proposition, Collection<Disjunction>>(resultAccName, encoding);
	}

	public Collection<Disjunction> smallerInterpretation(Interpretation interpretation) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		Disjunction tryUndecided = new Disjunction();
		for (Argument a : interpretation) {
			// fix undecided
			if (interpretation.isUndecided(a)) {
				Disjunction clause1 = new Disjunction();
				clause1.add(new Negation(trues.apply(a)));
				encoding.add(clause1);

				Disjunction clause2 = new Disjunction();
				clause2.add(new Negation(falses.apply(a)));
				encoding.add(clause2);
			}
			else {
				//try to make undecided
				Proposition undecideA = new Proposition("tu_"+a.getName());
				tryUndecided.add(undecideA);
				
				Disjunction clause1 = new Disjunction();
				clause1.add(new Negation(undecideA));
				clause1.add(new Negation(trues.apply(a)));
				clause1.add(falses.apply(a));
				encoding.add(clause1);
				
				Disjunction clause2 = new Disjunction();
				clause2.add(new Negation(undecideA));
				clause2.add(trues.apply(a));
				clause2.add(new Negation(falses.apply(a)));
				encoding.add(clause1);
			}
		}
		encoding.add(tryUndecided);
		return encoding;
	}

	/**
	 * 
	 * @param interpretation
	 * @return a collection of clauses
	 */
	public Collection<Disjunction> largerInterpretation(Interpretation interpretation) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();

		for (Argument a : (Iterable<Argument>) interpretation.satisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(trues.apply(a));
			encoding.add(clause);
		}

		for (Argument a : (Iterable<Argument>) interpretation.unsatisfied()::iterator) {
			Disjunction clause = new Disjunction();
			clause.add(falses.apply(a));
			encoding.add(clause);
		}

		Disjunction undecided = new Disjunction();
		for (Argument a : (Iterable<Argument>) interpretation.undecided()::iterator) {
			undecided.add(trues.apply(a));
			undecided.add(falses.apply(a));
		}
		encoding.add(undecided);
		return encoding;
	}

	public Collection<Disjunction> bipolar() {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		for (Argument r : adf) {
			Proposition rTrue = trues.apply(r);
			Proposition rFalse = falses.apply(r);
			for (Link l : (Iterable<Link>) adf.linksToParent(r)::iterator) {
				if (l.isAttacking()) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(falses.apply(l.getFrom()));
					clause1.add(links.apply(l));
					encoding.add(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(trues.apply(l.getFrom()));
					clause2.add(new Negation(links.apply(l)));
					encoding.add(clause2);
				} else if (l.isSupporting()) {
					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(rTrue));
					clause1.add(trues.apply(l.getFrom()));
					clause1.add(new Negation(links.apply(l)));
					encoding.add(clause1);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(rFalse));
					clause2.add(falses.apply(l.getFrom()));
					clause2.add(links.apply(l));
					encoding.add(clause2);
				}
			}
		}
		return encoding;
	}

	public Collection<Disjunction> kBipolar(Interpretation interpretation) {
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();

		// use these proposition as a substitution for the special formulas
		// Tautology and Contradiction
		final Proposition TAUT = new Proposition("T");
		final Proposition CONT = new Proposition("F");

		// fix values for TAUT and CONT
		Disjunction tautClause = new Disjunction();
		tautClause.add(TAUT);
		Disjunction contClause = new Disjunction();
		contClause.add(new Negation(CONT));
		encoding.add(tautClause);
		encoding.add(contClause);

		for (Argument s : adf) {
			// set of arguments r with (r,s) non-bipolar and I(r) = u
			Set<Argument> xs = adf.linksToParent(s).filter(Link::isDependent).map(Link::getFrom)
					.filter(interpretation::isUndecided).collect(Collectors.toSet());

			SubsetIterator<Argument> subsetIterator = new DefaultSubsetIterator<Argument>(xs);

			while (subsetIterator.hasNext()) {
				Set<Argument> subset = subsetIterator.next();

				DefinitionalCNFTransform transform = new DefinitionalCNFTransform(
						r -> !xs.contains(r) ? links.apply(adf.link(r, s)) : (subset.contains(r) ? TAUT : CONT));
				AcceptanceCondition acc = adf.getAcceptanceCondition(s);
				Proposition accName = acc.collect(transform, Collection::add, encoding);

				// first implication
				Disjunction clause1 = new Disjunction();
				clause1.add(new Negation(trues.apply(s)));
				clause1.add(accName);

				// second implication
				Disjunction clause2 = new Disjunction();
				clause2.add(new Negation(falses.apply(s)));
				clause2.add(new Negation(accName));

				// stuff for both implications
				subset.stream().map(falses).forEach(rFalse -> {
					clause1.add(rFalse);
					clause2.add(rFalse);
				});
				xs.stream().filter(r -> !subset.contains(r)).map(trues).forEach(rTrue -> {
					clause1.add(rTrue);
					clause2.add(rTrue);
				});

				encoding.add(clause1);
				encoding.add(clause2);
			}
		}

		return encoding;
	}

	public Collection<Disjunction> verifyAdmissible(Interpretation interpretation) {
		Cache<Argument, Proposition> vars = new Cache<Argument, Proposition>(s -> new Proposition(s.getName()));
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();
		Disjunction accs = new Disjunction();
		for (Argument s : adf) {
			DefinitionalCNFTransform transform = new DefinitionalCNFTransform(vars);
			AcceptanceCondition acc = adf.getAcceptanceCondition(s);
			Proposition accName = acc.collect(transform, Collection::add, encoding);
			if (interpretation.isSatisfied(s)) {
				Disjunction clause = new Disjunction();
				clause.add(vars.apply(s));
				encoding.add(clause);
				accs.add(new Negation(accName));
			} else if (interpretation.isUnsatisfied(s)) {
				Disjunction clause = new Disjunction();
				clause.add(new Negation(vars.apply(s)));
				encoding.add(clause);
				accs.add(accName);
			}
		}
		encoding.add(accs);
		return encoding;
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
