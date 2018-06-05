/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.deductive.semantics.attacks;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.logics.pl.NaiveReasoner;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This attack notion models the undercut relation; A is defeated by B iff there is C subset of support(A) with claim(B) == \neg C.
 * @author Matthias Thimm
 */
public class Undercut implements Attack{

	/** Singleton instance. */
	private static Undercut instance = new Undercut();
	
	/** Private constructor. */
	private Undercut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static Undercut getInstance(){
		return Undercut.instance;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.attacks.Attack#isAttackedBy(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument, net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b) {
		NaiveReasoner reasoner = new NaiveReasoner();
		DefaultSubsetIterator<PropositionalFormula> it = new DefaultSubsetIterator<PropositionalFormula>(new HashSet<PropositionalFormula>(a.getSupport()));
		Set<PropositionalFormula> set = null;
		while(it.hasNext()){
			set = it.next();
			if(reasoner.isEquivalent(b.getClaim(), new Negation(new Conjunction(set))))
				return true;	
		}
		return false;
	}

}
