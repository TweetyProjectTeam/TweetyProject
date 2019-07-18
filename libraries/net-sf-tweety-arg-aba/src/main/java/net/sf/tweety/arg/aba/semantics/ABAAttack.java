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
 package net.sf.tweety.arg.aba.semantics;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.arg.aba.syntax.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.aba.syntax.Deduction;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 *
 * @param <T>	is the type of the language that the ABA theory's rules range over 
 */
public class ABAAttack<T extends Formula> extends Attack {

	/**
	 * Creates a new ABA attack
	 * @param attacker	the attacking argument
	 * @param attacked	the attacked argument
	 */
	public ABAAttack(Argument attacker, Argument attacked) {
		super(attacker, attacked);
	}

	/**
	 * @param attacker	the attacking argument
	 * @param attacked	the attacked argument
	 * @return	true iff attacker attacks attacked
	 */
	/*public static <T extends Invertable> boolean attacks(Deduction<T> attacker, Assumption<T> attacked) {
		return attacked instanceof Assumption && attacker.getConclusion().equals(attacked.getConclusion().complement());
	}*/

	/**
	 * @param from	the attacking set
	 * @param to	the attacked set
	 * @param abat	the ABA theory used to determine attacks
	 * @return	the set of attacks from the attacking set to the attacked set
	 * @param <T> the type of formulas
	 */
	public static <T extends Formula> Collection<ABAAttack<T>> allAttacks(Collection<Assumption<T>> from,
			Collection<Assumption<T>> to, ABATheory<T> abat) {
		Collection<ABAAttack<T>> result = new ArrayList<>();
		for (Deduction<T> deduction : abat.getAllDeductions(from))
			for (Assumption<T> assumption : to)
				if (abat.attacks(deduction, assumption.getConclusion())) 
				{
					Deduction<T> ass = new Deduction<>("");
					ass.setRule(assumption);
					result.add(new ABAAttack<>(deduction, ass));
				}
		return result;
	}

	/**
	 * @param abat	the ABA theory used to determine attacks
	 * @return	all attacks between arguments in abat
	 * @param <T> the type of formulas
	 */
	public static <T extends Formula> Collection<ABAAttack<T>> allAttacks(ABATheory<T> abat) {
		return allAttacks(abat.getAssumptions(), abat.getAssumptions(), abat);
	}

}
