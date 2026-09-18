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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.setaf;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.reasoners.ReductionBasedSetAfReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;

import java.util.*;

/**
 * Main factory for retrieving SetAF extension reasoners.
 * 
 * @author Lars Bengel
 */
public abstract class AbstractSetAfFactory {
	/**
	 * Returns an array of all available semantics.
	 *
	 * @return An array of all available semantics.
	 */
	public static Semantics[] getSemantics() {
		return Semantics.values();
	}

	/**
	 * Creates a new reasoner of the given semantics with default
	 * settings.
	 * 
	 * @param sem some identifier of a semantics.
	 * @return the requested reasoner.
	 */
	public static AbstractSetAfExtensionReasoner getReasoner(Semantics sem) {
		return new ReductionBasedSetAfReasoner(AbstractExtensionReasoner.getSimpleReasonerForSemantics(sem));
	}

    /**
     * Creates a SetAF from the given number of arguments and attack relations.
     *
     * @param nr_of_arguments The number of arguments in the SetAF.
     * @param attacks         The binary attack relations represented as a list of lists of integers.
     * @return A DungTheory constructed from the given arguments and attacks.
     */
    public static SetAf getSetAf(int nr_of_arguments, List<List<Integer>> attacks) {
        List<Argument> arguments = new ArrayList<Argument>();
		SetAf theory = new SetAf();
        for (int i = 1; i <= nr_of_arguments; i++){
            arguments.add(new Argument(Integer.toString(i)));
        }
		theory.addAll(arguments);

		for (List<Integer> list : attacks) {
			if (list.size() == 2) {
				theory.addAttack(arguments.get(list.get(0) - 1),arguments.get(list.get(1) - 1));
			} else {
				Set<Argument> attackers = new HashSet<>();
				for (int i = 0; i < list.size() - 1; i++) {
					attackers.add(arguments.get(list.get(i) - 1));
				}
				theory.addSetAttack(attackers, arguments.get(list.get(list.size() - 1) - 1));
			}
		}
        return theory;
    }
}
