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
package org.tweetyproject.arg.deductive.semantics.attacks;

import org.tweetyproject.arg.deductive.semantics.DeductiveArgument;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.Negation;


/**
 * This attack notion models the defeating rebuttal relation; A is defeated by B iff claim(B) |- \neg claim(A).
 * @author Matthias Thimm
 */
public class DefeatingRebuttal implements Attack{

	/** Singleton instance. */
	private static DefeatingRebuttal instance = new DefeatingRebuttal();
	
	/** Private constructor. */
	private DefeatingRebuttal(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static DefeatingRebuttal getInstance(){
		return DefeatingRebuttal.instance;
	}	
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.deductive.semantics.attacks.Attack#isAttackedBy(org.tweetyproject.argumentation.deductive.semantics.DeductiveArgument, org.tweetyproject.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b) {
		SimplePlReasoner reasoner = new SimplePlReasoner();
		if(reasoner.query(b.getClaim(), new Negation(a.getClaim())))
			return true;		
		return false;
	}

}
