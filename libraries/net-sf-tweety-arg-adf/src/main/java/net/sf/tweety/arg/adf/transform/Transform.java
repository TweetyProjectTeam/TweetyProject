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
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ContradictionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.TautologyAcceptanceCondition;

/**
 * An interface which allows transform operations on the recursive structure
 * of AcceptanceCondition.
 * <p>
 * This transform works via the visitor-pattern. Via dynamic dispatch on
 * {@link AcceptanceCondition#transform(Transform)} the concrete
 * {@link AcceptanceCondition}-transform implementation visits its matching
 * {@link Transform}-transform method. Before that, it however calls
 * {@link AcceptanceCondition}-transform on its sub-conditions and passes its
 * return values (of type R) to the specific {@link Transform}-transform of this
 * condition. This way information is passed from the bottom to the top of the
 * acceptance condition structure.
 * 
 * @author Mathias Hofer
 *
 * @param <R>
 *            the type of the bottom-up information, i.e. the values returned by
 *            the children of a node
 * @param <C>
 *            the type of the additional results the implementing transform
 *            operation may return
 */
public interface Transform<C, R> {

	/**
	 * This method is visited by the {@link DisjunctionAcceptanceCondition
	 * DisjunctionAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param subconditions
	 *            the subconditions of this disjunction, e.g. {a, b} if this =
	 *            or(a,b)
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this disjunction,
	 *            e.g. this -&gt; a<br>
	 *            polarity = 0: neutral global position of this disjunction,
	 *            e.g. this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this disjunction,
	 *            e.g. a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         disjunction
	 */
	public R transformDisjunction(Consumer<C> consumer, Collection<R> subconditions, int polarity);

	/**
	 * This method is visited by the {@link ConjunctionAcceptanceCondition
	 * ConjunctionAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param subconditions
	 *            the subconditions of this conjunction, e.g. {a, b} if this =
	 *            and(a,b)
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this conjunction,
	 *            e.g. this -&gt; a<br>
	 *            polarity = 0: neutral global position of this conjunction,
	 *            e.g. this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this conjunction,
	 *            e.g. a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         conjunction
	 */
	public R transformConjunction(Consumer<C> consumer, Collection<R> subconditions, int polarity);

	/**
	 * This method is visited by the {@link ImplicationAcceptanceCondition
	 * ImplicationAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param left
	 *            the left part of the implication
	 * @param right
	 *            the right part of the implication
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this implication,
	 *            e.g. this -&gt; a<br>
	 *            polarity = 0: neutral global position of this implication,
	 *            e.g. this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this implication,
	 *            e.g. a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         implication
	 */
	public R transformImplication(Consumer<C> consumer, R left, R right, int polarity);

	/**
	 * This method is visited by the {@link EquivalenceAcceptanceCondition
	 * EquivalenceAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param left
	 *            the left part of the equivalence
	 * @param right
	 *            the right part of the equivalence
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this equivalence,
	 *            e.g. this -&gt; a<br>
	 *            polarity = 0: neutral global position of this equivalence,
	 *            e.g. this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this equivalence,
	 *            e.g. a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         equivalence
	 */
	public R transformEquivalence(Consumer<C> consumer, R left, R right, int polarity);

	/**
	 * This method is visited by the
	 * {@link ExclusiveDisjunctionAcceptanceCondition
	 * ExclusiveDisjunctionAcceptanceConditions} of the acceptance condition on
	 * which we apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param left
	 *            the left part of the xor
	 * @param right
	 *            the right part of the xor
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this xor, e.g. this
	 *            -&gt; a<br>
	 *            polarity = 0: neutral global position of this xor, e.g. this
	 *            &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this xor, e.g. a -&gt;
	 *            this
	 * @return the result which we want to return to the parent-formula of this
	 *         xor
	 */
	public R transformExclusiveDisjunction(Consumer<C> consumer, R left, R right, int polarity);

	/**
	 * This method is visited by the {@link NegationAcceptanceCondition
	 * NegationAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param sub
	 *            the subformula of this negation, i.e. this = neg(sub)
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this negation, e.g.
	 *            this -&gt; a<br>
	 *            polarity = 0: neutral global position of this negation, e.g.
	 *            this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this negation, e.g.
	 *            a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         negation
	 */
	public R transformNegation(Consumer<C> consumer, R sub, int polarity);

	/**
	 * This method is visited by the {@link Argument Arguments} of the
	 * acceptance condition on which we apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param argument
	 *            the argument which calls this method
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this argument, e.g.
	 *            this -&gt; a<br>
	 *            polarity = 0: neutral global position of this argument, e.g.
	 *            this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this argument, e.g.
	 *            a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         argument
	 */
	public R transformArgument(Consumer<C> consumer, Argument argument, int polarity);

	/**
	 * This method is visited by the {@link ContradictionAcceptanceCondition
	 * ContradictionAcceptanceConditions} of the acceptance condition on which
	 * we apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this contradiction,
	 *            e.g. this -&gt; a<br>
	 *            polarity = 0: neutral global position of this contradiction,
	 *            e.g. this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this contradiction,
	 *            e.g. a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         contradiction
	 */
	public R transformContradiction(Consumer<C> consumer, int polarity);

	/**
	 * This method is visited by the {@link TautologyAcceptanceCondition
	 * TautologyAcceptanceConditions} of the acceptance condition on which we
	 * apply this {@link Transform}.
	 * 
	 * @param consumer
	 *            the consumer of the computed return values
	 * @param polarity
	 *            polarity &lt; 0: negative global position of this tautology, e.g.
	 *            this -&gt; a<br>
	 *            polarity = 0: neutral global position of this tautology, e.g.
	 *            this &lt;-&gt; a<br>
	 *            polarity &gt; 0: positive global position of this tautology, e.g.
	 *            a -&gt; this
	 * @return the result which we want to return to the parent-formula of this
	 *         tautology
	 */
	public R transformTautology(Consumer<C> consumer, int polarity);

}
