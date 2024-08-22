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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 package org.tweetyproject.arg.aba.semantics;

import java.util.ArrayList;
import java.util.Collection;

import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.aba.syntax.Deduction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 *  This class models an ABA attack.
 *
 * @param <T>	is the type of the language that the ABA theory's rules range over
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 */
public class AbaAttack<T extends Formula> extends Attack {

	/**
	 * Creates a new ABA attack
	 * @param attacker	the attacking argument
	 * @param attacked	the attacked argument
	 */
	public AbaAttack(Argument attacker, Argument attacked) {
		super(attacker, attacked);
	}

	/**
	 * True iff attacker attacks attacked
	 * @param attacker	the attacking argument
	 * @param attacked	the attacked argument
	 * @param <T> type
	 * @return	true iff attacker attacks attacked
	 */
	public static <T extends Invertable> boolean attacks(Deduction<T> attacker, Assumption<T> attacked) {
		return attacked instanceof Assumption && attacker.getConclusion().equals(attacked.getConclusion().complement());
	}

	/**
	 * Returns all attacks from the given attacking set to the given attacked set.
	 *
	 * @param from	the attacking set
	 * @param to	the attacked set
	 * @param abat	the ABA theory used to determine attacks
	 * @return	the set of attacks from the attacking set to the attacked set
	 * @param <T> the type of formulas
	 */
	public static <T extends Formula> Collection<AbaAttack<T>> allAttacks(Collection<Assumption<T>> from,
			Collection<Assumption<T>> to, AbaTheory<T> abat) {
		Collection<AbaAttack<T>> result = new ArrayList<>();
		for (Deduction<T> deduction : abat.getAllDeductions(from))
			for (Assumption<T> assumption : to)
				if (abat.attacks(deduction, assumption.getConclusion()))
				{
					Deduction<T> ass = new Deduction<>("");
					ass.setRule(assumption);
					result.add(new AbaAttack<>(deduction, ass));
				}
		return result;
	}

	/**
	 * Returns all attacks between arguments in the given AbaTheory.
	 *
	 * @param abat	the ABA theory used to determine attacks
	 * @return	all attacks between arguments in abat
	 * @param <T> the type of formulas
	 */
	public static <T extends Formula> Collection<AbaAttack<T>> allAttacks(AbaTheory<T> abat) {
		return allAttacks(abat.getAssumptions(), abat.getAssumptions(), abat);
	}

}
