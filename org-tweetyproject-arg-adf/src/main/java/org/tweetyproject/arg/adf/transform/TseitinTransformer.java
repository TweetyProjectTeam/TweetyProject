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
package org.tweetyproject.arg.adf.transform;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.pl.Atom;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.syntax.pl.Negation;
import org.tweetyproject.arg.adf.util.CacheMap;
import org.tweetyproject.arg.adf.util.Pair;

/**
 * 
 * @author Mathias Hofer
 *
 */
public final class TseitinTransformer extends AbstractCollector<Atom, Clause, Pair<Atom, Collection<Clause>>> {

	private final boolean optimize;
	
	private final Function<Argument, Atom> mapping;
	
	/**
	 * 
	 * @param optimize
	 * @param topLevelPolarity
	 */
	private TseitinTransformer(Function<Argument, Atom> mapping, boolean optimize, int topLevelPolarity) {
		super(topLevelPolarity);
		this.mapping = Objects.requireNonNull(mapping);
		this.optimize = optimize;
	}
	
	public static TseitinTransformer ofPositivePolarity(boolean optimize) {
		return ofPositivePolarity(new CacheMap<>(arg -> Atom.of(arg.getName())), optimize);
	}
	
	public static TseitinTransformer ofNegativePolarity(boolean optimize) {
		return ofNegativePolarity(new CacheMap<>(arg -> Atom.of(arg.getName())), optimize);
	}
	
	public static TseitinTransformer ofPositivePolarity(Function<Argument, Atom> mapping, boolean optimize) {
		return new TseitinTransformer(mapping, optimize, 1);
	}
	
	public static TseitinTransformer ofNegativePolarity(Function<Argument, Atom> mapping, boolean optimize) {
		return new TseitinTransformer(mapping, optimize, -1);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.adf.transform.AbstractCollector#initialize()
	 */
	@Override
	protected Collection<Clause> initialize() {
		return new LinkedList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.adf.transform.AbstractCollector#finish(java.lang.
	 * Object, java.util.Collection)
	 */
	@Override
	protected Pair<Atom, Collection<Clause>> finish(Atom bottomUpData, Collection<Clause> collection) {
		return Pair.of(bottomUpData, collection);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformDisjunction(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformDisjunction(Collection<Atom> children, Consumer<Clause> clauses, int polarity) {
		Atom name = Atom.of("or_" + children);
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(children, new Negation(name)));
		}
		if (polarity <= 0 || !optimize) {
			for (Atom atom : children) {
				clauses.accept(Clause.of(new Negation(atom), name));
			}
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformConjunction(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformConjunction(Collection<Atom> children, Consumer<Clause> clauses, int polarity) {
		Atom name = Atom.of("and_" + children);
		if (polarity >= 0 || !optimize) {
			for (Atom atom : children) {
				clauses.accept(Clause.of(atom, new Negation(name)));
			}
		}
		if (polarity <= 0 || !optimize) {
			Set<Literal> literals = new HashSet<>(children.size() + 1);
			for (Atom atom : children) {
				literals.add(new Negation(atom));
			}
			literals.add(name);
			clauses.accept(Clause.of(literals));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformImplication(
	 * java.lang.Object, java.lang.Object, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformImplication(Atom left, Atom right, Consumer<Clause> clauses, int polarity) {
		Atom name = Atom.of(left + "_impl_" + right);
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(new Negation(name), new Negation(left), right));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, left));
			clauses.accept(Clause.of(name, new Negation(right)));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformEquivalence(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformEquivalence(Collection<Atom> children, Consumer<Clause> clauses, int polarity) {
		// we generate a circle of implications instead of pairwise equivalences
		Atom name = Atom.of("equiv_" + children.hashCode());
		if (polarity >= 0 || !optimize) {
			Iterator<Atom> iterator = children.iterator();
			Atom first = iterator.next();
			Atom left = first;
			while (iterator.hasNext()) {
				Atom right = iterator.next();
				clauses.accept(Clause.of(new Negation(name), new Negation(left), right));
				left = right;
			}
			// left is now the last child
			// complete the circle
			clauses.accept(Clause.of(new Negation(name), new Negation(left), first));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(children, name));

			Set<Literal> literals = new HashSet<>(children.size() + 1);
			for (Atom child : children) {
				literals.add(new Negation(child));
			}
			literals.add(name);
			clauses.accept(Clause.of(literals));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.arg.adf.transform.AbstractCollector#
	 * transformExclusiveDisjunction(java.lang.Object, java.lang.Object,
	 * java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformExclusiveDisjunction(Atom left, Atom right, Consumer<Clause> clauses, int polarity) {
		Atom name = Atom.of(left + "_xor_" + right);
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(new Negation(name), left, right));
			clauses.accept(Clause.of(new Negation(name), new Negation(left), new Negation(right)));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, new Negation(left), right));
			clauses.accept(Clause.of(name, left, new Negation(right)));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformNegation(java.
	 * lang.Object, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformNegation(Atom child, Consumer<Clause> clauses, int polarity) {
		Atom name = Atom.of("neg_" + child);
		if (polarity >= 0 || !optimize) {
			clauses.accept(Clause.of(new Negation(name), new Negation(child)));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(Clause.of(name, child));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformArgument(net.
	 * sf.tweety.arg.adf.syntax.Argument, java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformArgument(Argument argument, Consumer<Clause> collection, int polarity) {
		return mapping.apply(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformContradiction(
	 * java.util.function.Consumer, int)
	 */
	@Override
	protected Atom transformContradiction(Consumer<Clause> clauses, int polarity) {
		// TODO use same proposition
		Atom name = Atom.of("F");
		// forces name to be 0
		clauses.accept(Clause.of(new Negation(name)));
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tweetyproject.arg.adf.transform.AbstractCollector#transformTautology(java
	 * .util.function.Consumer, int)
	 */
	@Override
	protected Atom transformTautology(Consumer<Clause> clauses, int polarity) {
		// TODO use same proposition
		Atom name = Atom.of("T");
		// forces name to be 1
		clauses.accept(Clause.of(name));
		return name;
	}

}
