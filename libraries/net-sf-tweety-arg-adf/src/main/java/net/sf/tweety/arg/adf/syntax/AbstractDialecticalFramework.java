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

import net.sf.tweety.arg.adf.sat.NativeLingelingSolver;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.SatLinkStrategy;
import net.sf.tweety.arg.adf.transform.OmegaReductTransform;
import net.sf.tweety.arg.adf.transform.Transform;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.Pair;

/**
 * This class implements abstract dialectical frameworks, cf. [Brewka,
 * Ellmauthaler, Strass, Wallner, Woltran. Abstract Dialectical Frameworks
 * Revisited. IJCAI'13]
 * 
 * @author Mathias Hofer
 */
public class AbstractDialecticalFramework
		implements BeliefBase, Comparable<AbstractDialecticalFramework>, Iterable<Argument> {

	private LinkStrategy linkStrategy = new SatLinkStrategy(new NativeLingelingSolver());

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
	 * @return true iff all of the links are bipolar
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
		return arguments().map(b -> linksFromParents(b)).flatMap(Function.identity());
	}
	
	public boolean containsArgument(Argument a) {
		return accByArgument.containsKey(a);
	}

	/**
	 * Computes a reduct of this ADF relative to the given interpretation s.t.
	 * all unsatisfied arguments are replaced with false.
	 * 
	 * @param interpretation
	 * @return the reduct
	 */
	public AbstractDialecticalFramework omegaReduct(Interpretation interpretation) {
		return transform(new OmegaReductTransform(interpretation));
	}
	
	/**
	 * Creates a copy of this ADF with transformed acceptance conditions.
	 * 
	 * @param transform the transform to apply
	 * @return a copy of this ADF
	 */
	public AbstractDialecticalFramework transform(Transform<AcceptanceCondition, AcceptanceCondition> transform) {
		Map<Argument, AcceptanceCondition> transformed = new HashMap<Argument, AcceptanceCondition>();
		for (Argument a : accByArgument.keySet()) {
			AcceptanceCondition acc = accByArgument.get(a);
			transformed.put(a, acc.transform(transform));
		}
		return new AbstractDialecticalFramework(transformed);
	}

	/**
	 * @param b
	 * @return a stream of links (a,b)
	 */
	public Stream<Link> linksFromParents(Argument b) {
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

	/**
	 * 
	 * @param linkStrategy
	 *            the strategy which is used for computing the links
	 */
	public void setLinkStrategy(LinkStrategy linkStrategy) {
		this.linkStrategy = linkStrategy;
	}

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
