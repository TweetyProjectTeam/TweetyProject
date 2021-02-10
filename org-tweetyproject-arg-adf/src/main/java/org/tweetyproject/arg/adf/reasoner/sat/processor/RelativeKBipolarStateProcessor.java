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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.BipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeBipolarSatEncoding;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.util.InterpretationTrieSet;

/**
 * Decides if an ADF becomes k-bipolar relative to some truth assignments.
 * 
 * @author Mathias Hofer
 *
 */
public final class RelativeKBipolarStateProcessor implements StateProcessor {

	private final int maxDepth;

	private final LinkStrategy linkStrategy;

	/**
	 * @param maxDepth maxDepth
	 * @param solver solver
	 */
	public RelativeKBipolarStateProcessor(int maxDepth, LinkStrategy linkStrategy) {
		this.maxDepth = maxDepth;
		this.linkStrategy = Objects.requireNonNull(linkStrategy);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.sat.processor.StateProcessor#process(java.util.function.Consumer, net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping, net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void process(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		new BipolarSatEncoding().encode(consumer, adf, mapping);
		Map<Link, Set<Interpretation>> bipolarIn = checkLinks(adf);
		for (Entry<Link, Set<Interpretation>> entry : bipolarIn.entrySet()) {
			Link link = entry.getKey();
			for (Interpretation interpretation : entry.getValue()) {
				new RelativeBipolarSatEncoding(interpretation, link).encode(consumer, adf, mapping);
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
		Set<Interpretation> prefixes = new InterpretationTrieSet();
		
		Queue<Interpretation> guesses = initializeGuesses(arguments, adf);
		while (!guesses.isEmpty()) {
			Interpretation guess = guesses.poll();
			LinkType type = linkStrategy.compute(link.getFrom(), acc, guess);
			
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
		if (arguments.size() > 1) {
			for (Argument arg : arguments) {
				guesses.add(Interpretation.singleValued(arg, true, adf));
				guesses.add(Interpretation.singleValued(arg, false, adf));
			}			
		}
		return guesses;
	}

}