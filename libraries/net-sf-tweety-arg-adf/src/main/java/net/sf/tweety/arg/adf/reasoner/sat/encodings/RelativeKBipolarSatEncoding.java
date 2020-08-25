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
package net.sf.tweety.arg.adf.reasoner.sat.encodings;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.interpretation.TwoValuedInterpretationIterator;
import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Atom;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Literal;
import net.sf.tweety.arg.adf.syntax.pl.Negation;
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.InterpretationTrieSet;

/**
 * @author Mathias Hofer
 *
 */
@Deprecated
public class RelativeKBipolarSatEncoding implements SatEncoding {

	private final Map<Link, Set<Interpretation>> interpretations;

	public RelativeKBipolarSatEncoding(Map<Link, Set<Interpretation>> interpretations) {
		this.interpretations = Objects.requireNonNull(interpretations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.
	 * tweety.arg. adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		// use these propositions as a substitute for the special formulas
		// Tautology and Contradiction
		final Atom TAUT = createTrue(consumer);
		final Atom CONT = createFalse(consumer);
		
		int count1 = 0;
		int count2 = 0;
		
		for (Argument s : adf.getArguments()) {
			List<Argument> undecidedDependees = dependsOnUndecided(s, Interpretation.empty(adf), adf);

			Set<Argument> relevantFrom = interpretations.entrySet().stream()
					.map(e -> e.getKey())
					.filter(e -> e.getTo() == s)
					.map(Link::getFrom)
					.filter(undecidedDependees::contains)
					.collect(Collectors.toSet());
			
			List<Interpretation> relativeInterpretations = interpretations.entrySet().stream()
					.filter(e -> e.getKey().getTo() == s)
					.flatMap(e -> e.getValue().stream())
//					.map(i -> Interpretation.restrict(i, undecidedDependees))
					.filter(i -> i.numDecided() > 0)
					.collect(Collectors.toList());

			if (!undecidedDependees.isEmpty()) {
				Iterator<Interpretation> completionIterator = new TwoValuedInterpretationIterator(undecidedDependees);

				InterpretationTrieSet relevantPrefixes = null;
				if (!relativeInterpretations.isEmpty()) {
					relevantPrefixes = new InterpretationTrieSet(relativeInterpretations);
				}

				while (completionIterator.hasNext()) {
					Interpretation completion = completionIterator.next();

					TseitinTransformer transformer = null;
					if (relevantPrefixes != null && relevantPrefixes.contains(completion)) {
						transformer = TseitinTransformer
								.ofPositivePolarity(r -> !undecidedDependees.contains(r) || relevantFrom.contains(r) ? mapping.getLink(r, s)
										: (completion.satisfied(r) ? TAUT : CONT), false);
						count1++;
					} else {
						transformer = TseitinTransformer.ofPositivePolarity(r -> !undecidedDependees.contains(r) ? mapping.getLink(r, s)
										: (completion.satisfied(r) ? TAUT : CONT), false);
						count2++;
					}
					AcceptanceCondition acc = adf.getAcceptanceCondition(s);

					Atom accName = transformer.collect(acc, consumer);

					// first implication
					Collection<Literal> clause1 = new LinkedList<>();
					clause1.add(new Negation(mapping.getTrue(s)));
					clause1.add(accName);

					// second implication
					Collection<Literal> clause2 = new LinkedList<>();
					clause2.add(new Negation(mapping.getFalse(s)));
					clause2.add(new Negation(accName));

					// stuff for both implications
					for (Argument r : completion.arguments()) {
						if (completion.satisfied(r)) {
							Atom rFalse = mapping.getFalse(r);
							clause1.add(rFalse);
							clause2.add(rFalse);
						} else {
							Atom rTrue = mapping.getTrue(r);
							clause1.add(rTrue);
							clause2.add(rTrue);
						}
					}

					consumer.accept(Clause.of(clause1));
					consumer.accept(Clause.of(clause2));
				}
			}
		}
		System.out.println("Bipolarized: " + count1 + "; Not: " + count2);
	}

	/**
	 * Creates a proposition which is always true in the context of the given
	 * encoding
	 * 
	 * @param encoding
	 * @return a proposition which is true in <code>encoding</code>
	 */
	private static Atom createTrue(Consumer<Clause> encoding) {
		Atom trueProp = Atom.of("T");
		encoding.accept(Clause.of(trueProp));
		return trueProp;
	}

	/**
	 * Creates a proposition which is always false in the context of the given
	 * encoding
	 * 
	 * @param encoding
	 * @return a proposition which is false in <code>encoding</code>
	 */
	private static Atom createFalse(Consumer<Clause> encoding) {
		Atom falseProp = Atom.of("F");
		encoding.accept(Clause.of(new Negation(falseProp)));
		return falseProp;
	}

	/**
	 * @param s
	 * @param interpretation
	 * @param adf
	 * @return the arguments r s.t. there exists a dependent link (r,s) with r
	 *         being undecided in <code>interpretation</code>
	 */
	private List<Argument> dependsOnUndecided(Argument s, Interpretation interpretation, AbstractDialecticalFramework adf) {
		return adf.linksTo(s).stream()
				.filter(l -> l.getType().isDependent())
				.map(Link::getFrom)
				.filter(interpretation::undecided)
				.collect(Collectors.toList());
	}
}
