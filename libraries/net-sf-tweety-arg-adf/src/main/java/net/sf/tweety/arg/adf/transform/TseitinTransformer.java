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
package net.sf.tweety.arg.adf.transform;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public final class TseitinTransformer
		extends AbstractCollector<Proposition, Disjunction, Pair<Proposition, Collection<Disjunction>>> {

	private final boolean optimize;

	private final Function<Argument, Proposition> argumentMapping;

	/**
	 * Constructs a possibly optimized version of the definitional (resp.
	 * Tseitin) CNF transformation algorithm.
	 * <p>
	 * The optimization generates only the necessary parts of the definitions
	 * based on the polarity of the subformulas, i.e. <- or -> (or both for
	 * polarity = 0) instead of always <->.
	 * 
	 * @param argumentMapping
	 * @param optimize
	 */
	public TseitinTransformer(Function<Argument, Proposition> argumentMapping, boolean optimize) {
		this.argumentMapping = argumentMapping;
		this.optimize = optimize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractCollector#initialize()
	 */
	@Override
	protected Collection<Disjunction> initialize() {
		return new LinkedList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractCollector#finish(java.lang.
	 * Object, java.util.Collection)
	 */
	@Override
	protected Pair<Proposition, Collection<Disjunction>> finish(Proposition bottomUpData,
			Collection<Disjunction> collection) {
		return new Pair<>(bottomUpData, collection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformDisjunction(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformDisjunction(Collection<Proposition> children, Consumer<Disjunction> clauses,
			int polarity) {
		Proposition name = new Proposition("or_" + children);
		if (polarity >= 0 || !optimize) {
			Disjunction clause = new Disjunction();
			clause.addAll(children);
			clause.add(new Negation(name));
			clauses.accept(clause);
		}
		if (polarity <= 0 || !optimize) {
			for (Proposition p : children) {
				clauses.accept(new Disjunction(new Negation(p), name));
			}
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformConjunction(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformConjunction(Collection<Proposition> children, Consumer<Disjunction> clauses,
			int polarity) {
		Proposition name = new Proposition("and_" + children);
		if (polarity >= 0 || !optimize) {
			for (Proposition p : children) {
				clauses.accept(new Disjunction(p, new Negation(name)));
			}
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause = new Disjunction();
			for (Proposition p : children) {
				clause.add(new Negation(p));
			}
			clause.add(name);
			clauses.accept(clause);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformImplication(
	 * java.lang.Object, java.lang.Object, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformImplication(Proposition left, Proposition right, Consumer<Disjunction> clauses,
			int polarity) {
		Proposition name = new Proposition(left.getName() + "_impl_" + right.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause = new Disjunction();
			clause.add(new Negation(name));
			clause.add(new Negation(left));
			clause.add(right);
			clauses.accept(clause);
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(new Disjunction(name, left));
			clauses.accept(new Disjunction(name, new Negation(right)));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformEquivalence(
	 * java.util.Collection, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformEquivalence(Collection<Proposition> children, Consumer<Disjunction> clauses,
			int polarity) {
		// we generate a circle of implications instead of pairwise equivalences
		Proposition name = new Proposition("equiv_" + children.hashCode());
		if (polarity >= 0 || !optimize) {
			Iterator<Proposition> iterator = children.iterator();
			Proposition first = iterator.next();
			Proposition left = first;
			while (iterator.hasNext()) {
				Proposition right = iterator.next();
				clauses.accept(new Disjunction(Set.of(new Negation(name), new Negation(left), right)));
				left = right;
			}
			// left is now the last child
			// complete the circle
			clauses.accept(new Disjunction(Set.of(new Negation(name), new Negation(left), first)));
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause1 = new Disjunction(children);
			clause1.add(name);
			clauses.accept(clause1);

			Disjunction clause2 = new Disjunction();
			for (Proposition child : children) {
				clause2.add(new Negation(child));
			}
			clause2.add(name);
			clauses.accept(clause2);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.transform.AbstractCollector#
	 * transformExclusiveDisjunction(java.lang.Object, java.lang.Object,
	 * java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformExclusiveDisjunction(Proposition left, Proposition right,
			Consumer<Disjunction> clauses, int polarity) {
		Proposition name = new Proposition(left.getName() + "_xor_" + right.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(new Negation(name));
			clause1.add(left);
			clause1.add(right);
			clauses.accept(clause1);

			Disjunction clause2 = new Disjunction();
			clause2.add(new Negation(name));
			clause2.add(new Negation(left));
			clause2.add(new Negation(right));
			clauses.accept(clause2);
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(name);
			clause1.add(new Negation(left));
			clause1.add(right);
			clauses.accept(clause1);

			Disjunction clause2 = new Disjunction();
			clause2.add(name);
			clause2.add(left);
			clause2.add(new Negation(right));
			clauses.accept(clause2);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformNegation(java.
	 * lang.Object, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformNegation(Proposition child, Consumer<Disjunction> clauses, int polarity) {
		Proposition name = new Proposition("neg_" + child.getName());
		if (polarity >= 0 || !optimize) {
			clauses.accept(new Disjunction(new Negation(name), new Negation(child)));
		}
		if (polarity <= 0 || !optimize) {
			clauses.accept(new Disjunction(name, child));
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformArgument(net.
	 * sf.tweety.arg.adf.syntax.Argument, java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformArgument(Argument argument, Consumer<Disjunction> collection, int polarity) {
		return argumentMapping.apply(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformContradiction(
	 * java.util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformContradiction(Consumer<Disjunction> clauses, int polarity) {
		// TODO use same proposition
		Proposition name = new Proposition("F");
		// forces name to be 0
		Disjunction clause = new Disjunction();
		clause.add(new Negation(name));
		clauses.accept(clause);
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.transform.AbstractCollector#transformTautology(java
	 * .util.function.Consumer, int)
	 */
	@Override
	protected Proposition transformTautology(Consumer<Disjunction> clauses, int polarity) {
		// TODO use same proposition
		Proposition name = new Proposition("T");
		// forces name to be 1
		Disjunction clause = new Disjunction();
		clause.add(name);
		clauses.accept(clause);
		return name;
	}
}
