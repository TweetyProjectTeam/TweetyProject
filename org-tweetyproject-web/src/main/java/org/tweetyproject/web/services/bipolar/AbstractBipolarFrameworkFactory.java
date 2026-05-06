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
package org.tweetyproject.web.services.bipolar;

import org.tweetyproject.arg.bipolar.reasoner.deductive.*;
import org.tweetyproject.arg.bipolar.reasoner.necessity.*;
import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for construction bipolar argumentation framework from web requests.
 * 
 * @author Oleksandr Dzhychko
 */
public abstract class AbstractBipolarFrameworkFactory {


	/**
	 * Creates a new bipolar argumentation framework required fo the given semantics.
	 *
	 * @param semantics specified semantics
	 * @param numberOfArguments number of arguments
	 * @param attacks attacks
	 * @param supports supports
	 * @return the requested reasoner.
	 */
	public static AbstractBipolarFramework getArgumentationFramework(BipolarSemantics semantics,
																	 int numberOfArguments,
																	 List<List<Integer>> attacks,
																	 List<List<Integer>> supports) {
		var argumentationFramework = switch (semantics.input) {
			case DeductiveArgumentationFramework -> new DeductiveArgumentationFramework();
			case NecessityArgumentationFramework -> new NecessityArgumentationFramework();
		};

		var arguments = new ArrayList<BArgument>();
		for (int i = 1; i <= numberOfArguments; i++){
			var argument = new BArgument(Integer.toString(i));
			arguments.add(argument);
			argumentationFramework.add(argument);
		}

		for (List<Integer> attackInput : attacks) {
			var attacker = arguments.get(attackInput.get(0) - 1);
			var attacked = arguments.get(attackInput.get(1) - 1);
			Attack attack = new BinaryAttack(attacker, attacked);
			argumentationFramework.add(attack);
		}

		for (List<Integer> supportInput : supports) {
			var supporter = arguments.get(supportInput.get(0) - 1);
			var supported = arguments.get(supportInput.get(1) - 1);
			Support attack = new BinarySupport(supporter, supported);
			argumentationFramework.add(attack);
		}

		return argumentationFramework;
	}
}
