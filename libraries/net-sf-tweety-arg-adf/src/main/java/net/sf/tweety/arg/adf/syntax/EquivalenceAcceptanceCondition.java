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
package net.sf.tweety.arg.adf.syntax;

import java.util.function.Function;
import java.util.stream.Stream;

import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class EquivalenceAcceptanceCondition implements AcceptanceCondition{

	private AcceptanceCondition first;

	private AcceptanceCondition second;

	/**
	 * @param first
	 * @param second
	 */
	public EquivalenceAcceptanceCondition(AcceptanceCondition first, AcceptanceCondition second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public Stream<Argument> arguments() {
		return Stream.concat(first.arguments(), second.arguments());
	}

	@Override
	public PlFormula toPlFormula(Function<Argument, PlFormula> argumentMap) {
		PlFormula a = first.toPlFormula(argumentMap);
		PlFormula b = second.toPlFormula(argumentMap);
		Conjunction conj = new Conjunction();
		conj.add(new Disjunction(new Negation(a), b));
		conj.add(new Disjunction(a, new Negation(b)));
		return conj;
	}
	
}
