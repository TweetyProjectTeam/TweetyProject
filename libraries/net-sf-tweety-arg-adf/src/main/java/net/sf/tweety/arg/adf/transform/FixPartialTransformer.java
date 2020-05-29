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

import static net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition.CONTRADICTION;
import static net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition.TAUTOLOGY;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ContradictionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.TautologyAcceptanceCondition;

/**
 * Syntactically rewrites the acceptance condition s.t. the arguments are
 * replaced with the corresponding constants
 * {@link TautologyAcceptanceCondition} or
 * {@link ContradictionAcceptanceCondition} according to the given (partial)
 * interpretation.
 * <p>
 * Some examples:<br>
 * and(a,b,c) with {t(a), u(b), u(c)} becomes and(b,c).<br>
 * or(a,b,c) with {t(a), u(b), u(c)} becomes T.<br>
 * or(a,b,c) with {f(a), u(b), u(c)} becomes or(b,c).<br>
 * 
 * @author Mathias Hofer
 *
 */
public final class FixPartialTransformer extends AbstractTransformer<AcceptanceCondition, Void, AcceptanceCondition>{

	private final Interpretation interpretation;
	
	/**
	 * @param interpretation the interpretation which is used
	 */
	public FixPartialTransformer(Interpretation interpretation) {
		this.interpretation = Objects.requireNonNull(interpretation);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#initialize()
	 */
	@Override
	protected Void initialize() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#finish(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected AcceptanceCondition finish(AcceptanceCondition bottomUpData, Void topDownData) {
		return bottomUpData;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformDisjunction(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformDisjunction(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		Collection<AcceptanceCondition> filtered = new LinkedList<AcceptanceCondition>();
		for (AcceptanceCondition child : children) {
			if (child == TAUTOLOGY) {
				// propagate the constant further, since the disjunction is a tautology
				return child;
			}
			if (child != CONTRADICTION) {
				filtered.add(child);
			}
		}
		
		if (filtered.size() == 1) {
			return filtered.iterator().next();
		}
		
		return new DisjunctionAcceptanceCondition(filtered);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformConjunction(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformConjunction(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		Collection<AcceptanceCondition> filtered = new LinkedList<AcceptanceCondition>();
		for (AcceptanceCondition child : children) {
			if (child == CONTRADICTION) {
				// propagate the constant further, since the conjunction is a contradiction 
				return child;
			}
			if (child != TAUTOLOGY) {
				filtered.add(child);
			}
		}
		
		if (filtered.size() == 1) {
			return filtered.iterator().next();
		}
		
		return new ConjunctionAcceptanceCondition(filtered);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformImplication(java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformImplication(AcceptanceCondition left, AcceptanceCondition right,
			Void topDownData, int polarity) {
		if (left == CONTRADICTION || right == TAUTOLOGY) {
			return TAUTOLOGY;
		} else if (left == TAUTOLOGY && right == CONTRADICTION) {
			return CONTRADICTION;
		} else if (left == TAUTOLOGY) {
			return right;
		} else if (right == CONTRADICTION) {
			return new NegationAcceptanceCondition(left);
		}
		return new ImplicationAcceptanceCondition(left, right);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformEquivalence(java.util.Collection, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformEquivalence(Collection<AcceptanceCondition> children, Void topDownData,
			int polarity) {
		Iterator<AcceptanceCondition> iterator = children.iterator();
		AcceptanceCondition first = iterator.next();
		boolean containsContradiction = first == CONTRADICTION;
		boolean containsTautology = first == TAUTOLOGY;
		boolean allEqual = true;
		while (iterator.hasNext()) {
			AcceptanceCondition child = iterator.next();
			
			// if everything is equal to the first element, then everything is equal
			if (!first.equals(child)) {
				allEqual = false;
			}
			
			// the further rewriting depends on the existence and type of the logical constant
			if (child == CONTRADICTION) {
				containsContradiction = true;
			} else if (child == TAUTOLOGY) {
				containsTautology = true;
			}
		}
		
		// all the children are syntactical equivalent
		if (allEqual) {
			return TAUTOLOGY;
		}
		
		// if we contain both constants, we are clearly contradicting
		if (containsContradiction && containsTautology) {
			return CONTRADICTION;
		}
		
		// if we contain neither constant, we cannot rewrite anything
		if (!containsContradiction && !containsTautology) {
			return new EquivalenceAcceptanceCondition(children);
		}

		// now we know how to rewrite the children
		Collection<AcceptanceCondition> filtered = new LinkedList<AcceptanceCondition>();
		for (AcceptanceCondition child : children) {
			if (containsContradiction) {
				filtered.add(new NegationAcceptanceCondition(child));
			} else if (containsTautology) {
				filtered.add(child);
			}
		}
		
		return new ConjunctionAcceptanceCondition(filtered);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformExclusiveDisjunction(java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformExclusiveDisjunction(AcceptanceCondition left, AcceptanceCondition right,
			Void topDownData, int polarity) {
		if (left.equals(right)) {
			return CONTRADICTION;
		} else if ((left == CONTRADICTION && right == TAUTOLOGY) || (left == TAUTOLOGY && right == CONTRADICTION)) {
			return TAUTOLOGY;
		} else if (left == CONTRADICTION) {
			return right;
		} else if (right == CONTRADICTION) {
			return left;
		} else if (left == TAUTOLOGY) {
			return new NegationAcceptanceCondition(right);
		} else if (right == TAUTOLOGY) {
			return new NegationAcceptanceCondition(left);
		}
		return new ExclusiveDisjunctionAcceptanceCondition(left, right);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformNegation(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformNegation(AcceptanceCondition child, Void topDownData, int polarity) {
		if (child == CONTRADICTION) {
			return TAUTOLOGY;
		} else if (child == TAUTOLOGY) {
			return CONTRADICTION;
		}
		return new NegationAcceptanceCondition(child);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformArgument(net.sf.tweety.arg.adf.syntax.Argument, java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformArgument(Argument argument, Void topDownData, int polarity) {
		if (interpretation.satisfied(argument)) {
			return TAUTOLOGY;
		} else if (interpretation.unsatisfied(argument)) {
			return CONTRADICTION;
		}
		return argument;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformContradiction(java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformContradiction(Void topDownData, int polarity) {
		return CONTRADICTION;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.transform.AbstractTransformer#transformTautology(java.lang.Object, int)
	 */
	@Override
	protected AcceptanceCondition transformTautology(Void topDownData, int polarity) {
		return TAUTOLOGY;
	}

}
