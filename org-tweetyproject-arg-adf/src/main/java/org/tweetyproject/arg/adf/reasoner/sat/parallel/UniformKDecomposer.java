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
package org.tweetyproject.arg.adf.reasoner.sat.parallel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Tries to evenly reduce the non-bipolar links (hence K) of the resulting ADFs.
 * 
 * @author Mathias Hofer
 *
 */
public final class UniformKDecomposer extends AbstractDecomposer {
	
	private final LinkStrategy linkStrategy;
	
	/**
	 * @param satSolver
	 */
	public UniformKDecomposer(LinkStrategy linkStrategy) {
		this.linkStrategy = Objects.requireNonNull(linkStrategy);
	}

	@Override
	Set<Argument> partition(AbstractDialecticalFramework adf, int size) {
		Set<Link> dependentLinks = new HashSet<>();
		Set<Argument> considerableArguments = new HashSet<>(); // the set of arguments which can have an impact on the polarity of some link
		for (Link link : adf.links()) {
			if (link.getType().isDependent()) {
				dependentLinks.add(link);
				considerableArguments.add(link.getFrom());
			}
		}
		
		Iterator<Set<Argument>> subsetIterator = new FixedSizeSubsetIterator(considerableArguments, size);
		
		// TODO optimization: estimate max score and use it to skip bad partitions
		while(subsetIterator.hasNext()) {
			Map<Interpretation, Set<Link>> decidedByPartial = new HashMap<Interpretation, Set<Link>>();
			Set<Argument> subset = subsetIterator.next();
			Set<Link> links = affectedLinks(dependentLinks, subset);
			 
			Iterator<Interpretation> partialsIterator = new InterpretationIterator(subset);
			while (partialsIterator.hasNext()) {
				Interpretation partial = partialsIterator.next();
				Set<Link> decided = new HashSet<>();
				for (Link link : links) {
					linkStrategy.compute(link.getFrom(), adf.getAcceptanceCondition(link.getTo()), partial);					
				}		
			}
		}
		
		return null;
	}
	
	private static Set<Link> affectedLinks(Set<Link> links, Set<Argument> arguments) {
		return links.stream()
				.filter(l -> arguments.contains(l.getFrom()))
				.collect(Collectors.toSet());
	}
	
	private static double score(Set<Argument> arguments) {
		return 0.0;
	}
	
	private static final class FixedSizeSubsetIterator implements Iterator<Set<Argument>> {
		
		private int current;
		
		private final int max;
		
		private List<Argument> arguments;

		public FixedSizeSubsetIterator(Collection<Argument> arguments, int n) {
			this.arguments = List.copyOf(arguments);
			this.current = (2 << n) - 1; // set n lsb to 1
			this.max = current << (arguments.size() - n);		
		}

		@Override
		public boolean hasNext() {
			return current < max;
		}
		
		private static boolean getBit(int n, int k) {
		    return ((n >> k) & 1) == 1;
		}

		@Override
		public Set<Argument> next() {
			Set<Argument> next = new HashSet<>();
			for (int i = 0; i < arguments.size(); i++) {
				Argument arg = arguments.get(i);
				if (getBit(current, i)) {
					next.add(arg);
				}
			}
						
			// next permutation of n set bits in lexicographical order
			int t = (current | (current - 1)) + 1;
			current = t | ((((t & -t) / (current & -current)) >> 1) - 1);
			
			return next;
		}
		
	}

}
