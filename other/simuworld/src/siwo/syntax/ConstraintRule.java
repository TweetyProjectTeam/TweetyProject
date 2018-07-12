/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
package siwo.syntax;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logicprogramming.asplibrary.syntax.*;

/**
 * this class models a simuworld constraint rule. constraints
 * are used to reject actions leading to unwant world states.
 * 
 * @author Thomas Vengels
 *
 */
public class ConstraintRule {
	public ConstraintRule(List<Atom> constraints) {
		this.C = (constraints == null)?new LinkedList<Atom>() : constraints;
	}
	
	public List<Atom> C;

	
	public Set<Term> getAllTerms() {
		Set<Term> ret = new LinkedHashSet<Term>(); 
		Set<String> already_known = new HashSet<String>();
		for (Atom a : this.C) {
			for (Term t : a.getTerms())
				if (already_known.add(t.toString()))
					ret.add(t);
		}
		return ret;
	}
}
