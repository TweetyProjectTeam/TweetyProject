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
package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.TermAdapter;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * this class models a set term, which can be used
 * for sets in dlv complex programs.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class SetTerm extends TermAdapter<Set<Term<?>>> {
	
	public SetTerm() {
		super(new HashSet<Term<?>>());
	}
	
	public SetTerm(SetTerm other) {
		super(new HashSet<Term<?>>(other.value));
	}
	
	public SetTerm(Collection<Term<?>> terms) {
		super(new HashSet<Term<?>>(terms));
	}
	
	@Override
	public SetTerm clone() {
		return new SetTerm(this);
	}

	@Override
	public String toString() {
		String ret = "{";
		Iterator<Term<?>> iter = value.iterator();
		if (iter.hasNext())
			ret += iter.next();
		while (iter.hasNext())
			ret += ", "+iter.next();
		ret +="}";
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SetTerm) {
			SetTerm os = (SetTerm) o;
			
			// both sets must be same size, and
			// every element from here should be
			// in there.
			for (Term<?> t : value)
				if (!os.value.contains(t))
					return false;
			
			return true;
		} else {
			return false;
		}
	}
}
