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

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * 
 * @author Mathias Hofer
 *
 */
public class DefinitionalCNFTransform implements Transform<Disjunction, Proposition> {
	
	private Function<Argument, Proposition> argumentToProposition;
	
	private boolean optimize = false;

	/**
	 * @param argumentToProposition
	 */
	public DefinitionalCNFTransform(Function<Argument, Proposition> argumentToProposition) {
		super();
		this.argumentToProposition = argumentToProposition;
	}

	/**
	 * @param argumentToProposition
	 * @param optimize
	 */
	public DefinitionalCNFTransform(Function<Argument, Proposition> argumentToProposition, boolean optimize) {
		super();
		this.argumentToProposition = argumentToProposition;
		this.optimize = optimize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformDisjunction(java.util.
	 * function.Consumer, java.util.Collection, int)
	 */
	@Override
	public Proposition transformDisjunction(Consumer<Disjunction> consumer, Collection<Proposition> subconditions,
			int polarity) {
		Proposition name = new Proposition("or_" + subconditions.hashCode());
		if (polarity >= 0 || !optimize) {
			Disjunction clause = new Disjunction();
			clause.addAll(subconditions);
			clause.add(new Negation(name));
			consumer.accept(clause);
		}
		if (polarity <= 0  || !optimize) {
			for (Proposition p : subconditions) {
				Disjunction clause = new Disjunction(new Negation(p), name);
				consumer.accept(clause);
			}
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformConjunction(java.util.
	 * function.Consumer, java.util.Collection, int)
	 */
	@Override
	public Proposition transformConjunction(Consumer<Disjunction> consumer, Collection<Proposition> subconditions,
			int polarity) {
		Proposition name = new Proposition("and_" + subconditions.hashCode());
		if (polarity >= 0  || !optimize) {
			for (Proposition p : subconditions) {
				Disjunction clause = new Disjunction(p, new Negation(name));
				consumer.accept(clause);
			}
		}
		if (polarity <= 0  || !optimize) {
			Disjunction clause = new Disjunction();
			for (Proposition p : subconditions) {
				clause.add(new Negation(p));
			}
			clause.add(name);
			consumer.accept(clause);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformImplication(java.util.
	 * function.Consumer, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public Proposition transformImplication(Consumer<Disjunction> consumer, Proposition left, Proposition right,
			int polarity) {
		Proposition name = new Proposition(left.getName() + "_impl_" + right.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause = new Disjunction();
			clause.add(new Negation(name));
			clause.add(new Negation(left));
			clause.add(right);
			consumer.accept(clause);
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause1 = new Disjunction(name, left);
			Disjunction clause2 = new Disjunction(name, new Negation(right));
			consumer.accept(clause1);
			consumer.accept(clause2);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformEquivalence(java.util.
	 * function.Consumer, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public Proposition transformEquivalence(Consumer<Disjunction> consumer, Proposition left, Proposition right,
			int polarity) {
		Proposition name = new Proposition(left.getName() + "_equiv_" + right.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(new Negation(name));
			clause1.add(new Negation(left));
			clause1.add(right);
			Disjunction clause2 = new Disjunction();
			clause2.add(new Negation(name));
			clause2.add(left);
			clause2.add(new Negation(right));
			consumer.accept(clause1);
			consumer.accept(clause2);
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(name);
			clause1.add(left);
			clause1.add(right);
			Disjunction clause2 = new Disjunction();
			clause2.add(name);
			clause2.add(new Negation(left));
			clause2.add(new Negation(right));
			consumer.accept(clause1);
			consumer.accept(clause2);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformExclusiveDisjunction(java
	 * .util.function.Consumer, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public Proposition transformExclusiveDisjunction(Consumer<Disjunction> consumer, Proposition left,
			Proposition right, int polarity) {
		Proposition name = new Proposition(left.getName() + "_xor_" + right.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(new Negation(name));
			clause1.add(left);
			clause1.add(right);
			Disjunction clause2 = new Disjunction();
			clause2.add(new Negation(name));
			clause2.add(new Negation(left));
			clause2.add(new Negation(right));
			consumer.accept(clause1);
			consumer.accept(clause2);
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause1 = new Disjunction();
			clause1.add(name);
			clause1.add(new Negation(left));
			clause1.add(right);
			Disjunction clause2 = new Disjunction();
			clause2.add(name);
			clause2.add(left);
			clause2.add(new Negation(right));
			consumer.accept(clause1);
			consumer.accept(clause2);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformNegation(java.util.
	 * function.Consumer, java.lang.Object, int)
	 */
	@Override
	public Proposition transformNegation(Consumer<Disjunction> consumer, Proposition sub, int polarity) {
		Proposition name = new Proposition("neg_" + sub.getName());
		if (polarity >= 0 || !optimize) {
			Disjunction clause = new Disjunction(new Negation(name), new Negation(sub));
			consumer.accept(clause);
		}
		if (polarity <= 0 || !optimize) {
			Disjunction clause = new Disjunction(name, sub);
			consumer.accept(clause);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformArgument(java.util.
	 * function.Consumer, net.sf.tweety.arg.adf.syntax.Argument, int)
	 */
	@Override
	public Proposition transformArgument(Consumer<Disjunction> consumer, Argument argument, int polarity) {
		return argumentToProposition.apply(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.syntax.Transform#transformContradiction(java.util.
	 * function.Consumer, int)
	 */
	@Override
	public Proposition transformContradiction(Consumer<Disjunction> consumer, int polarity) {
		Proposition name = new Proposition("F");
		// forces name to be 0
		Disjunction clause = new Disjunction();
		clause.add(new Negation(name));
		consumer.accept(clause);
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.Transform#transformTautology(java.util.
	 * function.Consumer, int)
	 */
	@Override
	public Proposition transformTautology(Consumer<Disjunction> consumer, int polarity) {
		Proposition name = new Proposition("T");
		// forces name to be 1
		Disjunction clause = new Disjunction();
		clause.add(name);
		consumer.accept(clause);
		return name;
	}

}
