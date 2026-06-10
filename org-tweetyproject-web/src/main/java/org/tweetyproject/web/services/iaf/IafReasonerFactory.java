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
package org.tweetyproject.web.services.iaf;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main factory for retrieving abstract extension reasoners.
 * 
 * @author Jonas Klein, Lars Bengel
 */
public abstract class IafReasonerFactory {
	/**
	 * Returns an array of all available semantics.
	 *
	 * @return An array of all available semantics.
	 */
	public static Semantics[] getSemantics() {
		return Semantics.values();
	}

	/**
	 * Creates a new reasoner measure of the given semantics with default
	 * settings.
	 * 
	 * @param sem some identifier of an semantics.
	 * @return the requested reasoner.
	 */
	public static IncompleteReasoner getReasoner(Semantics sem) {
		return new IncompleteReasoner(AbstractExtensionReasoner.getSimpleReasonerForSemantics(sem));
	}

    /**
     * Creates a IncompleteTheory from the given number of arguments and attack relations.
     *
     * @param nr_of_arguments The number of arguments in the IncompleteTheory.
     * @param uncertainArguments    The uncertain arguments represented as a list of integers
	 * @param definiteAttacks 		The definite attacks as a list of lists of integers
	 * @param uncertainAttacks 		The uncertain attacks as a list of lists of integers
     * @return A IncompleteTheory constructed from the given arguments and attacks.
     */

    public static IncompleteTheory getIncompleteTheory(int nr_of_arguments, List<Integer> uncertainArguments, List<List<Integer>> definiteAttacks, List<List<Integer>> uncertainAttacks) {
        List<Argument> arguments = new ArrayList<>();

		IncompleteTheory theory = new IncompleteTheory();
        for (int i = 1; i <= nr_of_arguments; i++){
            arguments.add(new Argument(Integer.toString(i)));
			if (uncertainArguments.contains(i)) {
				theory.addPossibleArgument(arguments.get(i-1));
			} else {
				theory.addDefiniteArgument(arguments.get(i-1));
			}
        }

        for (List<Integer> list : definiteAttacks) {
            theory.addDefiniteAttack(arguments.get(list.get(0) - 1),arguments.get(list.get(1) - 1));
        }
		for (List<Integer> list : uncertainAttacks) {
			theory.addPossibleAttack(arguments.get(list.get(0) - 1),arguments.get(list.get(1) - 1));
		}

        return theory;
    }
}
