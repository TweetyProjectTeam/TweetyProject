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

import org.tweetyproject.arg.bipolar.reasoner.*;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.bipolar.syntax.Support;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;

import java.util.ArrayList;
import java.util.List;

/**
 * Main factory for retrieving bipolar extension reasoners as supported by the web service
 * 
 * @author Lars Bengel
 */
public abstract class AbstractBipolarFactory {
	/**
	 * Returns an array of all available bipolar semantics.
	 *
	 * @return An array of all available bipolar semantics.
	 */
	public static Semantics[] getSemantics() {
		return Semantics.values();
	}

	public static Support.Type[] getSupportTypes() {
		return new Support.Type[] {Support.Type.DEFAULT, Support.Type.DEDUCTIVE, Support.Type.NECESSITY};
	}

	/**
	 * Creates a new reasoner measure of the given semantics with default
	 * settings.
	 * 
	 * @param semantics some identifier of a semantics.
	 * @return the requested reasoner.
	 */
	public static AbstractBipolarExtensionReasoner getReasoner(Semantics semantics, String type) {
		Support.Type support_type = Support.Type.getType(type);
		switch (support_type) {
			case DEFAULT -> {
				return new SimpleCoalitionReasoner(semantics);
			} case DEDUCTIVE,SIMPLE_DEDUCTIVE -> {
				return new SimpleDeductiveReasoner(semantics);
			} case NECESSITY,SIMPLE_NECESSITY -> {
				return new SimpleNecessityReasoner(semantics);
			} default -> throw new IllegalArgumentException("unsupported combination of support type and semantics " + type + " and " + semantics);
        }
	}

	/**
	 * Creates a new bipolar argumentation framework.
	 *
	 * @param numberOfArguments number of arguments
	 * @param attacks attacks
	 * @param supports supports
	 * @return the requested reasoner.
	 */
	public static BipolarArgumentationFramework getBAF(int numberOfArguments,
													   List<List<Integer>> attacks,
													   List<List<Integer>> supports) {
		BipolarArgumentationFramework argumentationFramework = new BipolarArgumentationFramework();

		List<Argument> arguments = new ArrayList<Argument>();
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
