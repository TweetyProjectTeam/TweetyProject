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
package org.tweetyproject.arg.lp.semantics.attack;

import org.tweetyproject.arg.lp.syntax.Argument;


/**
 * This notion of attack models the confident attack relation. 
 * A attacks B iff A undercuts or confidently rebuts B.
 *  
 * @author Sebastian Homann
 *
 */
public class ConfidentAttack implements AttackStrategy {

	/** Singleton instance. */
	private static ConfidentAttack instance = new ConfidentAttack();
	
	private ConfidentRebut confidentRebut = ConfidentRebut.getInstance();
	private Undercut undercut = Undercut.getInstance();
	
	/** Private constructor. */
	private ConfidentAttack(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static ConfidentAttack getInstance(){
		return ConfidentAttack.instance;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.tweetyproject.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(org.tweetyproject.argumentation.parameterisedhierarchy.syntax.Argument, org.tweetyproject.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		return confidentRebut.attacks(a, b) || undercut.attacks(a, b);
	}
	
	@Override
	public String toString() {
		return "confident attack";
	}
	
	public String toAbbreviation() {
		return "ca";
	}
}
