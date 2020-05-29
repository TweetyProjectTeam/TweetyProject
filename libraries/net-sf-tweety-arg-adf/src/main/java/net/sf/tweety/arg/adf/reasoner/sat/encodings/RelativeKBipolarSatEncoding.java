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

import java.util.Iterator;
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
import net.sf.tweety.arg.adf.transform.TseitinTransformer;
import net.sf.tweety.arg.adf.util.ExcludeTwoValuedSubinterpretationsIterator;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
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
	public void encode(Consumer<Disjunction> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		// use these propositions as a substitute for the special formulas Tautology and Contradiction
		final Proposition TAUT = createTrue(consumer);
		final Proposition CONT = createFalse(consumer);
		for (Argument s : adf.getArguments()) {
			Set<Argument> undecidedDependees = dependsOnUndecided(s, Interpretation.empty(adf), adf);

			List<Interpretation> relativeInterpretations =  interpretations.entrySet().stream()
					.filter(e -> e.getKey().getTo() == s)
					.flatMap(e -> e.getValue().stream())
					.map(i -> Interpretation.restrict(i, undecidedDependees))
					.collect(Collectors.toList());	

			if (!undecidedDependees.isEmpty()) {				
				Iterator<Interpretation> completionIterator = null;
				if (relativeInterpretations.isEmpty()) {
					completionIterator = new TwoValuedInterpretationIterator(undecidedDependees);
				} else {
					completionIterator = new ExcludeTwoValuedSubinterpretationsIterator(relativeInterpretations);
				}

				while (completionIterator.hasNext()) {
					Interpretation completion = completionIterator.next();
					
					TseitinTransformer transformer = TseitinTransformer
							.builder(r -> !undecidedDependees.contains(r) ? mapping.getLink(r, s) : (completion.satisfied(r) ? TAUT : CONT))
							.build();
					AcceptanceCondition acc = adf.getAcceptanceCondition(s);

					Proposition accName = transformer.collect(acc, consumer);

					// first implication
					Disjunction clause1 = new Disjunction();
					clause1.add(new Negation(mapping.getTrue(s)));
					clause1.add(accName);

					// second implication
					Disjunction clause2 = new Disjunction();
					clause2.add(new Negation(mapping.getFalse(s)));
					clause2.add(new Negation(accName));

					// stuff for both implications
					for (Argument r : undecidedDependees) {
						if (completion.satisfied(r)) {
							Proposition rFalse = mapping.getFalse(r);
							clause1.add(rFalse);
							clause2.add(rFalse);
						} else {
							Proposition rTrue = mapping.getTrue(r);
							clause1.add(rTrue);
							clause2.add(rTrue);
						}
					}

					consumer.accept(clause1);
					consumer.accept(clause2);
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
	private static Proposition createTrue(Consumer<Disjunction> encoding) {
		Proposition trueProp = new Proposition("T");
		Disjunction clause = new Disjunction();
		clause.add(trueProp);
		encoding.accept(clause);
		return trueProp;
	}

	/**
	 * Creates a proposition which is always false in the context of the given
	 * encoding
	 * 
	 * @param encoding
	 * @return a proposition which is false in <code>encoding</code>
	 */
	private static Proposition createFalse(Consumer<Disjunction> encoding) {
		Proposition falseProp = new Proposition("F");
		Disjunction clause = new Disjunction();
		clause.add(new Negation(falseProp));
		encoding.accept(clause);
		return falseProp;
	}

	/**
	 * @param s
	 * @param interpretation
	 * @param adf
	 * @return the arguments r s.t. there exists a dependent link (r,s) with r
	 *         being undecided in <code>interpretation</code>
	 */
	private Set<Argument> dependsOnUndecided(Argument s, Interpretation interpretation, AbstractDialecticalFramework adf) {
		return adf.linksTo(s)
				.stream()
				.filter(l -> l.getType().isDependent())
				.map(Link::getFrom)
				.filter(interpretation::undecided)
				.collect(Collectors.toSet());
	}
}
