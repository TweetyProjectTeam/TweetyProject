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
package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.BipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeBipolarSatEncoding;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.util.InterpretationTrieSet;

/**
 * Decides if an ADF becomes k-bipolar relative to some truth assignments.
 * 
 * @author Mathias Hofer
 *
 */
public final class RelativeKBipolarStateProcessor implements StateProcessor {

	private final int maxDepth;

	private final IncrementalSatSolver solver;

	/**
	 * @param k
	 * @param comparatorFactory
	 */
	public RelativeKBipolarStateProcessor(int maxDepth, IncrementalSatSolver solver) {
		this.maxDepth = maxDepth;
		this.solver = Objects.requireNonNull(solver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor#process(net.
	 * sf.tweety.arg.adf.sat.SatSolverState,
	 * org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncodingContext,
	 * org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void process(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		new BipolarSatEncoding().encode(state::add, mapping, adf);
		Map<Link, Set<Interpretation>> bipolarIn = checkLinks(adf);
		for (Entry<Link, Set<Interpretation>> entry : bipolarIn.entrySet()) {
			Link link = entry.getKey();
			for (Interpretation interpretation : entry.getValue()) {
				new RelativeBipolarSatEncoding(interpretation, link).encode(state::add, mapping, adf);
			}
		}
	}

	private Map<Link, Set<Interpretation>> checkLinks(AbstractDialecticalFramework adf) {
		Map<Link, Set<Interpretation>> bipolarIn = new HashMap<>();
				
		List<Link> dependentLinks = adf.linksStream()
				.filter(l -> l.getType().isDependent())
				.collect(Collectors.toList());
		
		for (Link link : dependentLinks) {
			bipolarIn.putAll(bipolarRelativeTo(link, adf));
		}

		return bipolarIn;
	}
	
	private Map<Link, Set<Interpretation>> bipolarRelativeTo(Link link, AbstractDialecticalFramework adf) {
		Map<Link, Set<Interpretation>> bipolarIn = new HashMap<>();

		AcceptanceCondition acc = adf.getAcceptanceCondition(link.getTo());
		List<Argument> arguments = acc.arguments().collect(Collectors.toList());
		
		// use this set to eliminate symmetries, e.g. {t(a), t(b)} and {t(b), t(a)} are equivalent interpretations
		InterpretationTrieSet prefixes = new InterpretationTrieSet();
		
		Queue<Interpretation> guesses = initializeGuesses(arguments, adf);
		while (!guesses.isEmpty()) {
			Interpretation guess = guesses.poll();
			LinkStrategy strategy = new SatLinkStrategy(solver, guess);
			LinkType type = strategy.compute(link.getFrom(), acc);
			
			if (!type.isDependent()) {
				Link bipolarized = Link.of(link.getFrom(), link.getTo(), type);
				prefixes.add(guess);
				bipolarIn.computeIfAbsent(bipolarized, l -> new HashSet<>()).add(guess);
			}
			
			if (guess.numDecided() < maxDepth && type.isDependent()) {
				for (Argument arg : guess.undecided()) {
					Interpretation trueExtension = Interpretation.extend(guess, arg, true);
					if (!prefixes.contains(trueExtension)) {
						guesses.add(trueExtension);
					}
					Interpretation falseExtension = Interpretation.extend(guess, arg, false);
					if (!prefixes.contains(falseExtension)) {
						guesses.add(falseExtension);
					}
				}
			}
		}
				
		return bipolarIn;
	}
	
	private static Queue<Interpretation> initializeGuesses(List<Argument> arguments, AbstractDialecticalFramework adf) {
		Queue<Interpretation> guesses = new LinkedList<>();
		for (Argument arg : arguments) {
			guesses.add(Interpretation.singleValued(arg, true, adf));
			guesses.add(Interpretation.singleValued(arg, false, adf));
		}
		return guesses;
	}

}