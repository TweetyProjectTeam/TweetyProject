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
package net.sf.tweety.arg.lp.semantics.attack;

import net.sf.tweety.arg.lp.syntax.Argument;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;


/**
 * This notion of attack models the undercut relation. 
 * A undercuts B iff there is L in conclusion(A) and not L in assumption(B).
 *  
 * @author Sebastian Homann
 *
 */
public class Undercut implements AttackStrategy {

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
	

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.NotionOfAttack#attacks(net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument, net.sf.tweety.argumentation.parameterisedhierarchy.syntax.Argument)
	 */
	public boolean attacks(Argument a, Argument b) {
		for(DLPLiteral literal : a.getConclusions()) {
			if(b.getAssumptions().contains(literal)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "undercut";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.argumentation.parameterisedhierarchy.semantics.attack.AttackStrategy#toAbbreviation()
	 */
	public String toAbbreviation() {
		return "u";
	}
}
