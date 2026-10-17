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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.AbstractInterpretation;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * An immutable three-valued interpretation v : A -> {t, f, u} over a fixed
 * domain A of arguments.
 *
 * @author Lars Bengel
 */
public final class ThreeValuedInterpretation extends AbstractInterpretation<AbstractDialecticalFramework, Argument> {

	/**
	 * The argument names of a domain and their positions. Interpretations derived
	 * from each other share one instance, so their values can be compared
	 * position by position; interpretations over separately created but equal
	 * domains are compared by name.
	 */
	private static final class Domain {
		final String[] names;
		final Map<String, Integer> index;

		Domain(Collection<String> names) {
			this.names = new LinkedHashSet<>(names).toArray(new String[0]);
			this.index = new HashMap<>();
			for (int i = 0; i < this.names.length; i++) {
				index.put(this.names[i], i);
			}
		}

		int indexOf(String name) {
			Integer i = index.get(name);
			if (i == null) {
				throw new IllegalArgumentException("The argument " + name + " is not part of the domain.");
			}
			return i;
		}

		boolean hasSameNames(Domain other) {
			return this == other || names.length == other.names.length && index.keySet().equals(other.index.keySet());
		}
	}

	private final Domain domain;
	/** the truth value of every argument, by its position in the domain */
	private final TruthValue[] values;
	private int hash;

	private ThreeValuedInterpretation(Domain domain, TruthValue[] values) {
		this.domain = domain;
		this.values = values;
	}

	/**
	 * @param domain the arguments
	 * @return the interpretation v_u that maps every argument to u
	 */
	public static ThreeValuedInterpretation allUndecided(Collection<Argument> domain) {
		Domain d = new Domain(names(domain));
		TruthValue[] values = new TruthValue[d.names.length];
		Arrays.fill(values, TruthValue.UNDECIDED);
		return new ThreeValuedInterpretation(d, values);
	}

	/**
	 * @param satisfied   the arguments mapped to t
	 * @param unsatisfied the arguments mapped to f
	 * @param domain      all arguments; the remaining ones are mapped to u
	 * @return the interpretation
	 * @throws IllegalArgumentException if the true and false sets overlap or are
	 *                                  not contained in the domain
	 */
	public static ThreeValuedInterpretation of(Set<Argument> satisfied, Set<Argument> unsatisfied, Collection<Argument> domain) {
		ThreeValuedInterpretation v = allUndecided(domain);
		v.assign(satisfied, TruthValue.TRUE);
		v.assign(unsatisfied, TruthValue.FALSE);
		return v;
	}

	/** only used during construction, before the instance is published */
	private void assign(Set<Argument> arguments, TruthValue value) {
		for (Argument a : arguments) {
			int i = domain.indexOf(nameOf(a));
			if (values[i] != TruthValue.UNDECIDED) {
				throw new IllegalArgumentException("The argument " + a + " cannot be both true and false.");
			}
			values[i] = value;
		}
	}

	/**
	 * @param values the truth value of every argument of the domain
	 * @return the interpretation
	 */
	public static ThreeValuedInterpretation of(Map<Argument, TruthValue> values) {
		Domain d = new Domain(names(values.keySet()));
		TruthValue[] array = new TruthValue[d.names.length];
		for (Map.Entry<Argument, TruthValue> entry : values.entrySet()) {
			array[d.indexOf(nameOf(entry.getKey()))] = Objects.requireNonNull(entry.getValue());
		}
		return new ThreeValuedInterpretation(d, array);
	}

	/**
	 * Returns the interpretation v_S of an extension S of an AF: the arguments of
	 * S are mapped to t, the arguments attacked by S to f, and all others to u.
	 *
	 * @param af        an abstract argumentation framework
	 * @param extension a conflict-free set of arguments
	 * @return the interpretation v_S over the arguments of the AF
	 * @throws IllegalArgumentException if the extension is not conflict-free
	 */
	public static ThreeValuedInterpretation fromExtension(DungTheory af, Extension<?> extension) {
		if (!af.containsAll(extension) || !af.isConflictFree(new ArrayList<Argument>(extension))) {
			throw new IllegalArgumentException("The extension is not conflict-free.");
		}
		Set<Argument> attacked = new LinkedHashSet<>(af.getAttacked(new ArrayList<Argument>(extension)));
		return of(new LinkedHashSet<>(extension), attacked, af);
	}

	/**
	 * @param values the values of a new interpretation over the domain of this
	 *               one, by position; the array is not copied
	 * @return the interpretation
	 */
	ThreeValuedInterpretation withValues(TruthValue[] values) {
		return new ThreeValuedInterpretation(domain, values);
	}

	/**
	 * @param i a position in the domain
	 * @return the argument at that position
	 */
	Argument argumentAt(int i) {
		return new Argument(domain.names[i]);
	}

	/**
	 * @param argument an argument of the domain
	 * @return its truth value
	 * @throws IllegalArgumentException if the argument is not part of the domain
	 */
	public TruthValue get(Argument argument) {
		return values[domain.indexOf(nameOf(argument))];
	}

	/**
	 * @return copies of all arguments of the domain
	 */
	public Set<Argument> getDomain() {
		return arguments(null);
	}

	/**
	 * @return the arguments mapped to t
	 */
	public Set<Argument> satisfied() {
		return arguments(TruthValue.TRUE);
	}

	/**
	 * @return the arguments mapped to f
	 */
	public Set<Argument> unsatisfied() {
		return arguments(TruthValue.FALSE);
	}

	/**
	 * @return the arguments mapped to u
	 */
	public Set<Argument> undecided() {
		return arguments(TruthValue.UNDECIDED);
	}

	private Set<Argument> arguments(TruthValue value) {
		Set<Argument> result = new LinkedHashSet<>();
		for (int i = 0; i < values.length; i++) {
			if (value == null || values[i] == value) {
				result.add(new Argument(domain.names[i]));
			}
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * @return the size of the domain
	 */
	public int size() {
		return values.length;
	}

	/**
	 * @return true if no argument is mapped to u
	 */
	public boolean isTwoValued() {
		for (TruthValue value : values) {
			if (value == TruthValue.UNDECIDED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param argument an argument of the domain
	 * @param value    a truth value
	 * @return a copy of this interpretation in which the argument has the given value
	 * @throws IllegalArgumentException if the argument is not part of the domain
	 */
	public ThreeValuedInterpretation with(Argument argument, TruthValue value) {
		int i = domain.indexOf(nameOf(argument));
		TruthValue[] copy = values.clone();
		copy[i] = Objects.requireNonNull(value);
		return new ThreeValuedInterpretation(domain, copy);
	}

	/**
	 * @param other an interpretation over the same domain
	 * @return true if other ≤_i this, i.e. this interpretation agrees with every
	 *         decided value of other
	 */
	public boolean isAtLeastAsInformativeAs(ThreeValuedInterpretation other) {
		TruthValue[] others = aligned(other);
		for (int i = 0; i < values.length; i++) {
			if (!values[i].isAtLeastAsInformativeAs(others[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param other an interpretation over the same domain
	 * @return true if this interpretation is strictly more informative than
	 *         other, i.e. it is at least as informative as other and differs
	 *         from it
	 */
	public boolean isMoreInformativeThan(ThreeValuedInterpretation other) {
		return isAtLeastAsInformativeAs(other) && !equals(other);
	}

	/**
	 * @param other an interpretation over the same domain
	 * @return true if neither interpretation is at least as informative as the other
	 */
	public boolean isIncomparable(ThreeValuedInterpretation other) {
		return !isAtLeastAsInformativeAs(other) && !other.isAtLeastAsInformativeAs(this);
	}

	/**
	 * @param other an interpretation over the same domain
	 * @return the consensus v ⊓ other, which keeps the values both agree on and maps the rest to u
	 */
	public ThreeValuedInterpretation consensus(ThreeValuedInterpretation other) {
		TruthValue[] others = aligned(other);
		TruthValue[] result = new TruthValue[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = values[i].consensus(others[i]);
		}
		return new ThreeValuedInterpretation(domain, result);
	}

	/**
	 * @param interpretations a non-empty collection of interpretations over the same domain
	 * @return their consensus ⊓
	 * @throws IllegalArgumentException if the collection is empty or the domains differ
	 */
	public static ThreeValuedInterpretation consensus(Collection<ThreeValuedInterpretation> interpretations) {
		Iterator<ThreeValuedInterpretation> it = interpretations.iterator();
		if (!it.hasNext()) {
			throw new IllegalArgumentException("The consensus of no interpretations is undefined.");
		}
		ThreeValuedInterpretation first = it.next();
		TruthValue[] result = first.values.clone();
		while (it.hasNext()) {
			TruthValue[] next = first.aligned(it.next());
			for (int i = 0; i < result.length; i++) {
				result[i] = result[i].consensus(next[i]);
			}
		}
		return new ThreeValuedInterpretation(first.domain, result);
	}

	/**
	 * Evaluates a formula in Kleene's strong three-valued logic. An implication
	 * phi → ψ is evaluated as ¬phi ∨ ψ; equivalences and exclusive disjunctions
	 * are u if some operand is u, and otherwise evaluated classically. Empty
	 * conjunctions are t; empty disjunctions and exclusive disjunctions are f.
	 *
	 * @param formula a formula over the domain
	 * @return its truth value
	 * @throws IllegalArgumentException if the formula mentions an argument outside
	 *                                  the domain or uses an unsupported connective
	 */
	public TruthValue evaluate(PlFormula formula) {
		if (formula instanceof Proposition) {
			Proposition p = (Proposition) formula;
			return values[domain.indexOf(p.getName())];
		} else if (formula instanceof Tautology) {
			return TruthValue.TRUE;
		} else if (formula instanceof Contradiction) {
			return TruthValue.FALSE;
		} else if (formula instanceof Negation) {
			Negation n = (Negation) formula;
			return evaluate(n.getFormula()).not();
		} else if (formula instanceof Conjunction) {
			Conjunction c = (Conjunction) formula;
			TruthValue result = TruthValue.TRUE;
			for (PlFormula f : c) {
				result = result.and(evaluate(f));
			}
			return result;
		} else if (formula instanceof Disjunction) {
			Disjunction d = (Disjunction) formula;
			TruthValue result = TruthValue.FALSE;
			for (PlFormula f : d) {
				result = result.or(evaluate(f));
			}
			return result;
		} else if (formula instanceof ExclusiveDisjunction) {
			ExclusiveDisjunction x = (ExclusiveDisjunction) formula;
			boolean parity = false;
			boolean undecided = false;
			for (PlFormula f : x) {
				TruthValue v = evaluate(f);
				undecided |= v == TruthValue.UNDECIDED;
				parity ^= v == TruthValue.TRUE;
			}
			return undecided ? TruthValue.UNDECIDED : TruthValue.valueOf(parity);
		} else if (formula instanceof Implication) {
			Implication i = (Implication) formula;
			return evaluate(i.getFirstFormula()).not().or(evaluate(i.getSecondFormula()));
		} else if (formula instanceof Equivalence) {
			Equivalence e = (Equivalence) formula;
			TruthValue left = evaluate(e.getFormulas().getFirst());
			TruthValue right = evaluate(e.getFormulas().getSecond());
			if (left == TruthValue.UNDECIDED || right == TruthValue.UNDECIDED) {
				return TruthValue.UNDECIDED;
			}
			return TruthValue.valueOf(left == right);
		}
		throw new IllegalArgumentException("Unsupported connective: "
				+ (formula == null ? "null" : formula.getClass().getSimpleName()));
	}

	/**
	 * Returns a lazy iterator over [v]_2, the two-valued interpretations w with
	 * v ≤_i w. The completions are generated one at a time and never stored.
	 *
	 * @return an iterator over all completions
	 */
	public Iterator<ThreeValuedInterpretation> completions() {
		return completions(getDomain());
	}

	/**
	 * Returns a lazy iterator over the completions of v restricted to the given
	 * arguments: every argument of the given collection that v maps to u is
	 * assigned t or f, all other values are kept.
	 *
	 * @param arguments arguments of the domain
	 * @return an iterator over the partial completions
	 * @throws IllegalArgumentException if an argument is not part of the domain
	 */
	public Iterator<ThreeValuedInterpretation> completions(Collection<Argument> arguments) {
		Set<Integer> positions = new LinkedHashSet<>();
		for (Argument a : arguments) {
			int i = domain.indexOf(nameOf(a));
			if (values[i] == TruthValue.UNDECIDED) {
				positions.add(i);
			}
		}
		int[] open = positions.stream().mapToInt(Integer::intValue).toArray();
		return new Iterator<>() {
			private final boolean[] assignment = new boolean[open.length];
			private boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public ThreeValuedInterpretation next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}
				TruthValue[] completion = values.clone();
				for (int i = 0; i < open.length; i++) {
					completion[open[i]] = TruthValue.valueOf(assignment[i]);
				}
				// binary increment; done after the all-true assignment
				int i = 0;
				while (i < assignment.length && assignment[i]) {
					assignment[i++] = false;
				}
				if (i == assignment.length) {
					hasNext = false;
				} else {
					assignment[i] = true;
				}
				return new ThreeValuedInterpretation(domain, completion);
			}
		};
	}

	/**
	 * @return the set of arguments mapped to t, as an extension
	 */
	public Extension<DungTheory> toExtension() {
		return new Extension<>(satisfied());
	}

	/**
	 * @return the labeling that maps t to in, f to out and u to undec
	 */
	public Labeling toLabeling() {
		Labeling labeling = new Labeling();
		for (int i = 0; i < values.length; i++) {
			labeling.put(new Argument(domain.names[i]), switch (values[i]) {
				case TRUE -> ArgumentStatus.IN;
				case FALSE -> ArgumentStatus.OUT;
				case UNDECIDED -> ArgumentStatus.UNDECIDED;
			});
		}
		return labeling;
	}

	/**
	 * @return true if the argument is mapped to t
	 * @throws IllegalArgumentException if the argument is not part of the domain
	 */
	@Override
	public boolean satisfies(Argument argument) {
		return get(argument) == TruthValue.TRUE;
	}

	/**
	 * Checks whether this interpretation is a three-valued model of the ADF, i.e.
	 * every argument is mapped to u or to the Kleene value of its acceptance
	 * condition.
	 *
	 * @param adf an ADF over the domain of this interpretation
	 * @return true if this interpretation is a three-valued model of the ADF
	 * @throws IllegalArgumentException if the ADF has other arguments
	 */
	@Override
	public boolean satisfies(AbstractDialecticalFramework adf) {
		if (!getDomain().equals(adf.getNodes())) {
			throw new IllegalArgumentException("The ADF is not defined over the domain of this interpretation.");
		}
		for (Argument a : adf) {
			TruthValue value = get(a);
			if (value != TruthValue.UNDECIDED && evaluate(adf.getAcceptanceCondition(a)) != value) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (hash == 0) {
			// independent of the order of the domain, like Map.hashCode
			int h = 0;
			for (int i = 0; i < values.length; i++) {
				h += domain.names[i].hashCode() ^ values[i].ordinal();
			}
			hash = h == 0 ? 1 : h;
		}
		return hash;
	}

	/**
	 * Two interpretations are equal if they have the same domain and assign the
	 * same truth values.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ThreeValuedInterpretation)) {
			return false;
		}
		ThreeValuedInterpretation other = (ThreeValuedInterpretation) obj;
		if (domain == other.domain) {
			return Arrays.equals(values, other.values);
		}
		return domain.hasSameNames(other.domain) && hashCode() == other.hashCode() && Arrays.equals(values, aligned(other));
	}

	/**
	 * @return string representation: true, then false, then undecided arguments, 
	 * 			each sorted by name, e.g. {@code {t(a) t(c) f(b) u(d)}}
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("{");
		for (TruthValue value : TruthValue.values()) {
			Set<String> names = new TreeSet<>();
			for (int i = 0; i < values.length; i++) {
				if (values[i] == value) {
					names.add(domain.names[i]);
				}
			}
			for (String name : names) {
				s.append(s.length() > 1 ? " " : "").append(value).append('(').append(name).append(')');
			}
		}
		return s.append('}').toString();
	}

	/**
	 * @param other an interpretation over the same arguments
	 * @return the values of other, ordered by the positions of this domain
	 * @throws IllegalArgumentException if the domains differ
	 */
	private TruthValue[] aligned(ThreeValuedInterpretation other) {
		if (domain == other.domain) {
			return other.values;
		}
		if (!domain.hasSameNames(other.domain)) {
			throw new IllegalArgumentException("The interpretations have different domains.");
		}
		TruthValue[] result = new TruthValue[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i] = other.values[other.domain.indexOf(domain.names[i])];
		}
		return result;
	}

	private static List<String> names(Collection<Argument> arguments) {
		List<String> names = new ArrayList<>(arguments.size());
		for (Argument a : arguments) {
			names.add(nameOf(a));
		}
		return names;
	}

	private static String nameOf(Argument argument) {
		Objects.requireNonNull(argument, "argument");
		return Objects.requireNonNull(argument.getName(), "argument name");
	}

}
