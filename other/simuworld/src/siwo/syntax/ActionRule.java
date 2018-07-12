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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logicprogramming.asplibrary.syntax.Atom;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Literal;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Term;

/**
 * this class models a simuworld action rule. action rules
 * describe conditions and outcomes of an action.
 * 
 * @author Thomas Vengels
 *
 */
public class ActionRule {
	public ActionRule(Atom name, List<Literal> conditions, List<Atom> add, List<Atom> del) {
		this.name = name;
		this.C = (conditions==null)?new LinkedList<Literal>():conditions;
		this.A = (add==null)?new LinkedList<Atom>():add;
		this.D = (del==null)?new LinkedList<Atom>():del;
	}
	
	public Atom name;
	public List<Literal> C;
	public List<Atom> A,D;
		
	
	public void getFluents(Map<String,Integer> out) {		
		for (Atom a:this.A)
			out.put(a.getSymbol(),a.getArity());
		for (Atom a:this.D)
			out.put(a.getSymbol(),a.getArity());
	}

	/**
	 * this method returns a terms occuring in the name
	 * and condition literals of an action rule.
	 * 
	 * @return set of all terms
	 */
	public LinkedHashSet<Term> getAllTerms() {
		LinkedHashSet<Term> ret = new LinkedHashSet<Term>();
		HashSet<String> already_in = new HashSet<String>();
		// add terms from head
		Term[] t = this.name.getTerms();
		if (t != null)
			for (Term ti : t) {
				String image = ti.toString();
				if (already_in.add(image))
					ret.add(ti);
			}
		
		// add terms from C literals
		for (Literal l : this.C) {
			t = l.getAtom().getTerms();
			if (t != null)
				for (Term ti : t){ 
					String image = ti.toString();
					if (already_in.add(image))
						ret.add(ti);
				}
		}
			
		return ret;
	}
}