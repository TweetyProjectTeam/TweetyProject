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
package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.TwoValuedInterpretationIterator;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Atom;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.syntax.pl.Negation;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public class KBipolarSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.strategy.sat.SatEncoding#encode(org.tweetyproject.arg.adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public void encode(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		// use these propositions as a substitute for the special formulas
		// Tautology and Contradiction
		final Atom TAUT = createTrue(consumer);
		final Atom CONT = createFalse(consumer);

		final Interpretation EMPTY = Interpretation.empty(adf);

		for (Argument to : adf.getArguments()) {
			Set<Argument> undecidedDependees = dependsOnUndecided(to, EMPTY, adf);

			if (!undecidedDependees.isEmpty()) {
				AcceptanceCondition acc = adf.getAcceptanceCondition(to);
				
				Iterator<Interpretation> completionIterator = new TwoValuedInterpretationIterator(List.copyOf(undecidedDependees));				
				while (completionIterator.hasNext()) {
					// true if some argument is in the set, false otherwise
					Interpretation completion = completionIterator.next();
					
					Function<Argument, Atom> fn = from -> !undecidedDependees.contains(from) ? mapping.getLink(from, to) : (completion.satisfied(from) ? TAUT : CONT);
					TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(fn, false);	
					Atom accName = transformer.collect(acc, consumer);
					
					// first implication
					Collection<Literal> clause1 = new LinkedList<>();
					clause1.add(new Negation(mapping.getTrue(to)));
					clause1.add(accName);

					// second implication
					Collection<Literal> clause2 = new LinkedList<>();
					clause2.add(new Negation(mapping.getFalse(to)));
					clause2.add(new Negation(accName));

					// stuff for both implications
					for (Argument r : undecidedDependees) {
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
	private static Set<Argument> dependsOnUndecided(Argument s, Interpretation interpretation, AbstractDialecticalFramework adf) {
		return adf.linksTo(s).stream()
				.filter(l -> l.getType().isDependent())
				.map(Link::getFrom)
				.filter(interpretation::undecided)
				.collect(Collectors.toSet());
	}
//	
//	private static Atom argumentMapping(Argument arg, Interpretation completion, Set<Argument> undecidedDependees) {
//		if (undecidedDependees.contains(arg))
//	}
}
