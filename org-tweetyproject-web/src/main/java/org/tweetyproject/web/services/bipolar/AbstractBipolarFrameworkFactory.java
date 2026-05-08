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

import org.tweetyproject.arg.bipolar.syntax.*;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;

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
	public static BipolarArgumentationFramework getArgumentationFramework(BipolarSemantics semantics,
																	 int numberOfArguments,
																	 List<List<Integer>> attacks,
																	 List<List<Integer>> supports) {
		var argumentationFramework = switch (semantics.type) {
			case DEDUCTIVE,SIMPLE_DEDUCTIVE,DEFAULT,NECESSITY,SIMPLE_NECESSITY -> new BipolarArgumentationFramework();
			case EVIDENTIAL -> throw new UnsupportedOperationException("Not implemented yet");
		};

		var arguments = new ArrayList<Argument>();
		for (int i = 1; i <= numberOfArguments; i++){
			var argument = new Argument(Integer.toString(i));
			arguments.add(argument);
			argumentationFramework.add(argument);
		}

		for (List<Integer> attackInput : attacks) {
			var attacker = arguments.get(attackInput.get(0) - 1);
			var attacked = arguments.get(attackInput.get(1) - 1);
			Attack attack = new Attack(attacker, attacked);
			argumentationFramework.add(attack);
		}

		for (List<Integer> supportInput : supports) {
			var supporter = arguments.get(supportInput.get(0) - 1);
			var supported = arguments.get(supportInput.get(1) - 1);
			Support attack = new Support(supporter, supported);
			argumentationFramework.add(attack);
		}

		return argumentationFramework;
	}
}
