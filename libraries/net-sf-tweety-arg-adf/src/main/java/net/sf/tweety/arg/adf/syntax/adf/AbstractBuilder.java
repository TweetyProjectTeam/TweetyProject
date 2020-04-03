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
package net.sf.tweety.arg.adf.syntax.adf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.semantics.SimpleLink;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder;

/**
 * 
 * 
 * @author Mathias Hofer
 *
 */
abstract class AbstractBuilder implements Builder {

	protected final Map<Argument, AcceptanceCondition> arguments = new HashMap<>();

	protected LinkStrategy linkStrategy;

	private final Map<Argument, Set<Link>> linksTo = new HashMap<>();

	private boolean eager = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #lazy(net.sf.tweety.arg.adf.semantics.LinkStrategy)
	 */
	@Override
	public Builder lazy(LinkStrategy linkStrategy) {
		eager = false;
		this.linkStrategy = Objects.requireNonNull(linkStrategy);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #eager()
	 */
	@Override
	public Builder provided() {
		eager = true;
		linkStrategy = null;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #eager(net.sf.tweety.arg.adf.semantics.LinkStrategy)
	 */
	@Override
	public Builder eager(LinkStrategy linkStrategy) {
		eager = true;
		this.linkStrategy = Objects.requireNonNull(linkStrategy);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #add(net.sf.tweety.arg.adf.syntax.Argument,
	 * net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition)
	 */
	@Override
	public Builder add(Argument arg, AcceptanceCondition acc) {
		arguments.put(Objects.requireNonNull(arg), Objects.requireNonNull(acc));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #add(net.sf.tweety.arg.adf.semantics.Link)
	 */
	@Override
	public Builder add(Link link) {
		// link implicitly null-checked
		Set<Link> links = linksTo.computeIfAbsent(Objects.requireNonNull(link.getTo()), a -> new HashSet<Link>());
		links.add(link);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder
	 * #remove(net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public Builder remove(Argument arg) {
		arguments.remove(Objects.requireNonNull(arg));
		return this;
	}

	protected Collection<Link> linksTo(Argument child) {
		Set<Link> to = linksTo.computeIfAbsent(child, a -> new HashSet<>());		
		if (eager) {	
			// check if we have to compute missing links
			AcceptanceCondition acc = arguments.get(child);
			Set<Argument> parents = acc.arguments().collect(Collectors.toSet());
			for (Link link : to) {
				parents.remove(link.getFrom());
			}

			if (!parents.isEmpty() && linkStrategy == null) {
				throw new IllegalStateException("Could not build ADF because of missing links!");
			}

			for (Argument parent : parents) {
				LinkType type = linkStrategy.compute(parent, child, acc);
				to.add(new SimpleLink(parent, child, type));
			}
		}
		return to;
	}

}
