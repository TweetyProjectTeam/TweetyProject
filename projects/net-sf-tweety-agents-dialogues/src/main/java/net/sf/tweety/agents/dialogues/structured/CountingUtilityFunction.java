/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class represents a counting utility function, i.e. a function
 * that ranks a set of propositions to the number of common propositions
 * with this function's focal set.
 * 
 * @author Matthias Thimm
 *
 */
public class CountingUtilityFunction implements UtilityFunction {

	/**
	 * The focal set of this function.
	 */
	private Set<Proposition> focalSet;
	
	/**
	 * Creates a new counting utility function for the given focal set.
	 * @param focalSet a collection of propositions.
	 */
	public CountingUtilityFunction(Collection<? extends Proposition> focalSet){
		this.focalSet = new HashSet<Proposition>(focalSet);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.UtilityFunction#rank(java.util.Collection)
	 */
	@Override
	public int rank(Collection<? extends Proposition> propositions) {
		Set<Proposition> s = new HashSet<Proposition>(propositions);
		s.retainAll(this.focalSet);
		return s.size();
	}

}
