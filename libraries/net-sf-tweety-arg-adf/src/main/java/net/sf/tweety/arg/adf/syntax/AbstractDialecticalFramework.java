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
package net.sf.tweety.arg.adf.syntax;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.reasoner.LinkStrategy;
import net.sf.tweety.arg.adf.reasoner.SatLinkStrategy;
import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.sat.SatSolver;

/**
 * This class implements abstract dialectical frameworks, cf. [Brewka,
 * Ellmauthaler, Strass, Wallner, Woltran. Abstract Dialectical Frameworks
 * Revisited. IJCAI'13]
 * 
 * @author Mathias Hofer
 */
public class AbstractDialecticalFramework
		implements BeliefBase, Comparable<AbstractDialecticalFramework>, Iterable<Argument> {

	private LinkStrategy linkStrategy = new SatLinkStrategy(SatSolver.getDefaultSolver());

	/**
	 * Maps an argument to the arguments of its acceptance condition
	 */
	private Map<Argument, Set<Argument>> parentsByChild;

	/**
	 * Maps an argument to all the arguments in which it occurs in their
	 * acceptance condition.
	 */
	private Map<Argument, Set<Argument>> childrenByParent;

	private Map<Argument, AcceptanceCondition> accByArgument;

	private Cache<Pair<Argument, Argument>, Link> linkCache;

	public AbstractDialecticalFramework(Map<Argument, AcceptanceCondition> accByArgument) {
		this.accByArgument = new HashMap<Argument, AcceptanceCondition>(accByArgument);
		this.parentsByChild = new HashMap<Argument, Set<Argument>>();
		this.childrenByParent = new HashMap<Argument, Set<Argument>>();
		this.linkCache = new Cache<Pair<Argument, Argument>, Link>(
				p -> linkStrategy.compute(this, p.getFirst(), p.getSecond()));
		for (Argument child : this) {
			updateRelations(child);
		}
	}

	private void updateRelations(Argument child) {
		Set<Argument> parents = getAcceptanceCondition(child).arguments().collect(Collectors.toSet());
		parentsByChild.putIfAbsent(child, parents);
		for (Argument parent : parents) {
			childrenByParent.putIfAbsent(parent, new HashSet<Argument>());
			childrenByParent.get(parent).add(child);
		}
	}

	public Stream<Argument> arguments() {
		return accByArgument.keySet().stream();
	}

	/**
	 * Checks if the ADF is bipolar. May compute all links to do so.
	 * 
	 * @return
	 */
	public boolean bipolar() {
		return links().allMatch(Link::isBipolar);
	}
	
	public long kBipolar() {
		return links().filter(l -> !l.isBipolar()).count();
	}

	public Stream<Argument> parents(Argument child) {
		return parentsByChild.getOrDefault(child, Collections.emptySet()).stream();
	}

	public AcceptanceCondition getAcceptanceCondition(Argument argument) {
		return accByArgument.get(argument);
	}

	public Stream<Link> links() {
		return arguments().map(b -> linksToParent(b)).flatMap(Function.identity());
	}

	/**
	 * @param b
	 * @return a stream of links (a,b)
	 */
	public Stream<Link> linksToParent(Argument b) {
		return parentsByChild.getOrDefault(b, Collections.emptySet()).stream().map(a -> link(a, b));
	}

	/**
	 * @param a
	 * @return a stream of links (a,b)
	 */
	public Stream<Link> linksToChildren(Argument a) {
		return childrenByParent.getOrDefault(a, Collections.emptySet()).stream().map(b -> link(a, b));
	}

	/**
	 * Computes the link (a,b) iff necessary and returns it afterwards.
	 * 
	 * @param a
	 * @param b
	 * @return (a,b)
	 */
	public Link link(Argument a, Argument b) {
		return linkCache.apply(new Pair<Argument, Argument>(a, b));
	}

	public void setLink(Link link) {
		Argument a = link.getFrom();
		Argument b = link.getTo();

		if (!accByArgument.containsKey(a) || !accByArgument.containsKey(b)) {
			throw new IllegalArgumentException("Arguments of the given link are unknown to this ADF!");
		}

		parentsByChild.putIfAbsent(b, new HashSet<Argument>());
		parentsByChild.get(b).add(a);
		linkCache.put(new Pair<Argument, Argument>(a, b), link);
	}

	/**
	 * 
	 * @param linkStrategy
	 *            the strategy which is used in order to compute the links
	 *            whenever its necessary
	 */
	public void setLinkStrategy(LinkStrategy linkStrategy) {
		this.linkStrategy = linkStrategy;
	}

	// @Override
	// public boolean add(Argument f) {
	// // TODO currently does not make any sense since we do not add its
	// // acceptance condition, however add is inherited from BeliefSet and
	// // therefore we cannot alter its signature.
	// boolean result = super.add(f);
	// // do the bookkeeping
	// updateRelations(f);
	// return result;
	// }
	//
	// @Override
	// public boolean addAll(Collection<? extends Argument> c) {
	// // override to ensure the bookkeeping is done.
	// boolean result = true;
	// for (Argument a : c) {
	// // implicit bookkeeping by this.add(a)
	// boolean sub = this.add(a);
	// result = result && sub;
	// }
	// return result;
	// }

	@Override
	public int compareTo(AbstractDialecticalFramework o) {
		// AbstractDialecticalFramework implements Comparable in order to
		// have a fixed (but arbitrary) order among all theories
		// for that purpose we just use the hash code.
		return this.hashCode() - o.hashCode();
	}

	@Override
	public Signature getMinimalSignature() {
		return new AbstractDialecticalFrameworkSignature(new HashSet<Argument>(accByArgument.keySet()));
	}

	@Override
	public Iterator<Argument> iterator() {
		return accByArgument.keySet().iterator();
	}

}
