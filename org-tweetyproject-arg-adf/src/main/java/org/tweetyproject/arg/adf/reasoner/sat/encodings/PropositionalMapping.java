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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.util.Pair;
import org.tweetyproject.arg.adf.util.UnionCollectionView;

/**
 * Contains the propositional representation of the arguments and links of some
 * ADF.
 * <p>
 * This is especially needed if we want to interconnect different sat encodings,
 * since they have to share the same propositional variables.
 * <p>
 * Since we are dealing with three valued interpretations, in order to represent
 * an undecided argument we need two propositional variables for each argument.
 * One represents satisfied, one unsatisfied and if both are false the argument
 * should be interpreted as undecided. It should be prevented by the encoding
 * that both are true.
 * 
 * @author Mathias Hofer
 *
 */
public final class PropositionalMapping {

	private final Map<Argument, Literal> falses = new HashMap<>();

	private final Map<Argument, Literal> trues = new HashMap<>();

	private final Map<Pair<Argument, Argument>, Literal> links = new HashMap<>();

	/**
	 * Creates propositional representations for the arguments and links of the
	 * provided ADF. It does not compute any links in the process.
	 * <p>
	 * The propositional variables are eagerly computed and not changed anymore.
	 * Which makes this class usable in a parallel context.
	 * <p>
	 * It does not store a reference to the provided ADF, therefore it can be
	 * reused even by different ADFs as long as they are a subset, in terms of arguments and links, of the
	 * provided one.
	 * 
	 * @param adf
	 *            the ADF for which we need a propositional representation
	 */
	public PropositionalMapping(AbstractDialecticalFramework adf) {
		for (Argument child : adf.getArguments()) {
			falses.put(child, Literal.create(child.getName() + "_f"));
			trues.put(child, Literal.create(child.getName() + "_t"));
			Set<Argument> parents = adf.parents(child);
			for (Argument parent : parents) {
				links.put(Pair.of(parent, child), linkToProposition(parent, child));
			}
		}
	}

	private static Literal linkToProposition(Argument from, Argument to) {
		StringBuilder name = new StringBuilder("p_")
				.append(from.getName())
				.append("_")
				.append(to.getName());
		return Literal.create(name.toString());
	}

	public Literal getFalse(Argument argument) {
		if (!falses.containsKey(argument)) {
			throw new IllegalArgumentException("The given argument is unknown to this mapping.");
		}

		return falses.get(argument);
	}

	public Literal getTrue(Argument argument) {
		if (!trues.containsKey(argument)) {
			throw new IllegalArgumentException("The given argument is unknown to this mapping.");
		}

		return trues.get(argument);
	}

	public Literal getLink(Argument from, Argument to) {
		Pair<Argument, Argument> pair = Pair.of(from, to);
		if (!links.containsKey(pair)) {
			throw new IllegalArgumentException("The given link is unknown to this mapping.");
		}
		return links.get(pair);
	}
	
	public Collection<Literal> getArgumentLiterals() {
		return new UnionCollectionView<>(falses.values(), trues.values());
	}
	
	public Set<Argument> getArguments() {
		return Collections.unmodifiableSet(trues.keySet()); // could also return falses.keySet(), does not matter
	}

	public Literal getLink(Link link) {
		return getLink(link.getFrom(), link.getTo());
	}

}
