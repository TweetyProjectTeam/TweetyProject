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
package org.tweetyproject.arg.adf.semantics.interpretation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretations.EmptyInterpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretations.SetInterpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretations.SingleValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.util.MinusSetView;

/**
 * This class represents a three-valued interpretation of an Abstract
 * Dialectical Framework (ADF).
 * 
 * @author Mathias Hofer
 *
 */
public interface Interpretation {

	static Interpretation empty(AbstractDialecticalFramework adf) {
		return new EmptyInterpretation(adf);
	}

	static Interpretation fromMap(Map<Argument, Boolean> assignment) {
		Set<Argument> satisfied = new HashSet<>();
		Set<Argument> unsatisfied = new HashSet<>();
		Set<Argument> undecided = new HashSet<>();
		for (Argument arg : assignment.keySet()) {
			if (assignment.get(arg) == null) {
				undecided.add(arg);
			} else if (assignment.get(arg)) {
				satisfied.add(arg);
			} else {
				unsatisfied.add(arg);
			}
		}
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
	
	static Interpretation fromSet(Set<Argument> satisfied, AbstractDialecticalFramework adf) {
		return new SetInterpretation(satisfied, Set.of(), new MinusSetView<>(adf.getArguments(), satisfied));
	}
		
	static Interpretation fromSets(Set<Argument> satisfied, Set<Argument> unsatisfied, AbstractDialecticalFramework adf) {
		Set<Argument> undecided = new HashSet<>();
		for (Argument arg : adf.getArguments()) {
			if (!satisfied.contains(arg) && !unsatisfied.contains(arg)) {
				undecided.add(arg);
			}
		}
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
	
	/**
	 * Creates the union of two disjunct interpretations.
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 * @throws IllegalArgumentException if the two interpretations are not disjunct
	 */
	static Interpretation union(Interpretation i1, Interpretation i2) {
		if (!Collections.disjoint(i1.arguments(), i2.arguments())) throw new IllegalArgumentException("The given interpretations are not disjunct!");
		
		Set<Argument> satisfied = new HashSet<>(i1.satisfied());
		satisfied.addAll(i2.satisfied());
		
		Set<Argument> unsatisfied = new HashSet<>(i1.unsatisfied());
		unsatisfied.addAll(i2.unsatisfied());
		
		Set<Argument> undecided = new HashSet<>(i1.undecided());
		undecided.addAll(i2.undecided());
		
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
	
	/**
	 * Extends the given interpretation by deciding a currently undecided argument. If the argument is not undecided in the given interpretation, an exception is thrown.
	 * 
	 * @param toExtend toExtend
	 * @param argument argument
	 * @param value value
	 * @return return value
	 */
	static Interpretation extend(Interpretation toExtend, Argument argument, boolean value) {
		if (!toExtend.undecided(argument)) {
			throw new IllegalArgumentException("Given argument must be undecided!");
		}
		Set<Argument> undecided = new HashSet<Argument>(toExtend.undecided());
		undecided.remove(argument);
		if (value) {
			Set<Argument> satisfied = new HashSet<>(toExtend.satisfied());
			satisfied.add(argument);
			return new SetInterpretation(satisfied, toExtend.unsatisfied(), undecided);
		} else {
			Set<Argument> unsatisfied = new HashSet<>(toExtend.unsatisfied());
			unsatisfied.add(argument);
			return new SetInterpretation(toExtend.satisfied(), unsatisfied, undecided);
		}
	}

	static Interpretation fromSets(Set<Argument> satisfied, Set<Argument> unsatisfied, Set<Argument> undecided) {
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
	
	/**
	 * Constructs a three-valued ADF interpretation from a witness of a propositional sat encoding.
	 * 
	 * @param witness the propositional sat witness
	 * @param mapping the mapping of the propositional variables and the adf
	 * @throws NullPointerException if any of the arguments are null
	 * @return an ADF interpretation
	 */
	static Interpretation fromWitness(Set<Literal> witness,	PropositionalMapping mapping) {
		Set<Argument> satisfied = new HashSet<>();
		Set<Argument> unsatisfied = new HashSet<>();
		Set<Argument> undecided = new HashSet<>();
		for (Argument a : mapping.getArguments()) {
			if (witness.contains(mapping.getTrue(a))) {
				satisfied.add(a);
			} else if (witness.contains(mapping.getFalse(a))) {
				unsatisfied.add(a);
			} else {
				undecided.add(a);
			}
		}
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
			
	static Interpretation partial(Set<Argument> satisfied, Set<Argument> unsatisfied, AbstractDialecticalFramework adf) {
		Set<Argument> undecided = new HashSet<Argument>();
		for (Argument arg : adf.getArguments()) {
			if (!satisfied.contains(arg) && !unsatisfied.contains(arg)) {
				undecided.add(arg);
			}
		}
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}

	static Map<Argument, Boolean> toMap(Interpretation interpretation) {
		Map<Argument, Boolean> assignment = new HashMap<>();
		for (Argument a : interpretation.satisfied()) {
			assignment.put(a, true);
		}
		for (Argument a : interpretation.unsatisfied()) {
			assignment.put(a, false);
		}
		for (Argument a : interpretation.undecided()) {
			assignment.put(a, null);
		}
		return assignment;
	}
	
	/**
	 * Creates a new interpretation with the same assignments as in the given interpretation, but only uses the arguments contained in <code>restriction</code>.
	 * 
	 * @param interpretation the interpretation to restrict
	 * @param restriction the arguments that act as a restriction/filter
	 * @return an interpretation which only contains arguments in <code>restriction</code>
	 */
	static Interpretation restrict(Interpretation interpretation, Collection<Argument> restriction) {
		Set<Argument> satisfied = new HashSet<>();
		Set<Argument> unsatisfied = new HashSet<>();
		Set<Argument> undecided = new HashSet<>();
		for (Argument arg : restriction) {
			if (interpretation.satisfied(arg)) {
				satisfied.add(arg);
			} else if (interpretation.unsatisfied(arg)) {
				unsatisfied.add(arg);
			} else {
				undecided.add(arg);
			}
		}
		return new SetInterpretation(satisfied, unsatisfied, undecided);
	}
	
	/**
	 * Checks if, and only if, the two valued assignments for both of the
	 * interpretations are the same, ignores differences in the undecided
	 * assignments.
	 * 
	 * @param i1 an interpretation
	 * @param i2 an interpretation to be compared with <code>i1</code> for the two-valued assignments
	 * @return true iff the values of the decided arguments of both interpretations are the same
	 */
	static boolean equalsTwoValued(Interpretation i1, Interpretation i2) {
		return Objects.equals(i1, i2) ||
				(i1.satisfied().containsAll(i2.satisfied()) && i2.satisfied().containsAll(i1.satisfied()) && 
				i1.unsatisfied().containsAll(i2.unsatisfied()) && i2.unsatisfied().containsAll(i1.unsatisfied()));
	}
	
	/**
	 * Returns an interpretation relative to <code>adf</code> with a single truth value decided.
	 * <p>
	 * This may be useful for some decision-level based algorithms.
	 * 
	 * @param argument the argument to decide
	 * @param value the value of the argument
	 * @param adf the contextual ADF
	 * @return an interpretation with a single argument decided
	 */
	static Interpretation singleValued(Argument argument, boolean value, AbstractDialecticalFramework adf) {
		return new SingleValuedInterpretation(argument, value, adf);
	}
	
	/**
	 * Goes through all possible partial interpretations respecting the order of the given list of arguments.
	 * <p>
	 * This returns exponentially many in the size of <code>arguments</code> interpretations.
	 * 
	 * @param arguments the arguments for which we compute the interpretations
	 * @param adf the contextual ADF
	 * @return the partial interpretations
	 */
	static Iterator<Interpretation> partials(List<Argument> arguments, AbstractDialecticalFramework adf) {
		return new Iterator<Interpretation>() {
			
			private final Iterator<Interpretation> iter = new InterpretationIterator(arguments);
			
			@Override
			public Interpretation next() {
				Interpretation partial = iter.next();
				return partial(partial.satisfied(), partial.unsatisfied(), adf);
			}

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}
			
		};
	}
	
	static Builder builder(AbstractDialecticalFramework adf) {
		return new Builder(adf);
	}
	
	static Builder builder(Collection<Argument> arguments) {
		return new Builder(arguments);
	}
	
	static final class Builder {
		private final Map<Argument, Boolean> assignment = new HashMap<>();
		
		private Builder(Collection<Argument> arguments) {
			for (Argument arg : arguments) {
				// initialize as undecided
				assignment.put(arg, null);
			}
		}
				
		private Builder(AbstractDialecticalFramework adf) {
			this(adf.getArguments());
		}
		
		public Builder put(Argument arg, Boolean value) {
			if (!assignment.containsKey(arg)) {
				throw new IllegalArgumentException("The given argument is unknown to the provided ADF!");
			}
			assignment.put(arg, value);
			return this;
		}
		
		public Interpretation build() {
			return Interpretation.fromMap(assignment);
		}
	}
	
	boolean satisfied(Argument arg);

	boolean unsatisfied(Argument arg);

	boolean undecided(Argument arg);

	Set<Argument> satisfied();

	Set<Argument> unsatisfied();

	Set<Argument> undecided();

	/**
	 * Returns the union of {@link #satisfied()}, {@link #unsatisfied()} and
	 * {@link #undecided()}. Must not return additional arguments.
	 * 
	 * @return all the assigned arguments
	 */
	Set<Argument> arguments();
	
	/**
	 * Returns the number of arguments in this interpretation.
	 * 
	 * @return the number of arguments
	 */
	default int size() {
		return arguments().size();
	}
	
	default boolean containsAll(Collection<Argument> arguments) {
		return arguments().containsAll(arguments);
	}
	
	default boolean contains(Argument argument) {
		return arguments().contains(argument);
	}
	
	/**
	 * @param arg some argument
	 * @return true iff the argument is either satisfied or unsatisfied
	 */
	default boolean decided(Argument arg) {
		return satisfied(arg) || unsatisfied(arg);
	}
	
	/**
	 * Returns the number of decided arguments, i.e. satisfied or unsatisfied, in this interpretation.
	 *  
	 * @return the number of decided arguments
	 */
	default int numDecided() {
		return satisfied().size() + unsatisfied().size();
	}
	
	default boolean isSubsetOf(Interpretation superset) {
		return superset.satisfied().containsAll(satisfied()) && superset.unsatisfied().containsAll(unsatisfied());
	}
	
	default boolean isStrictSubsetOf(Interpretation superset) {
		return isSubsetOf(superset) && numDecided() != superset.numDecided();
	}
	
	default boolean isSupersetOf(Interpretation subset) {
		return satisfied().containsAll(subset.satisfied()) && unsatisfied().containsAll(subset.unsatisfied());
	}
	
	default boolean isStrictSupersetOf(Interpretation subset) {
		return isSupersetOf(subset) && numDecided() != subset.numDecided();
	}

}
