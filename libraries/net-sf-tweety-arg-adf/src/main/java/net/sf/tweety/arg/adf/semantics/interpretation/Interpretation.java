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
package net.sf.tweety.arg.adf.semantics.interpretation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * This class represents an immutable three-valued interpretation of an Abstract
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

	static Interpretation fromSets(Set<Argument> satisfied, Set<Argument> unsatisfied, Set<Argument> undecided) {
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

}
