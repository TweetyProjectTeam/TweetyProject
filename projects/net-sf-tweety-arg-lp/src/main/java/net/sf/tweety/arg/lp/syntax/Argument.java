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
package net.sf.tweety.arg.lp.syntax;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.lp.asp.syntax.*;


/**
 * Instances of this class represent arguments in the sense
 * of Definition 2 in [1].
 * 
 * [1] Ralf Schweimeier and Michael Schroeder: A Parameterised Hierarchy of 
 *  Argumentation Semantics for Extended Logic Programming and its 
 *  Application to the Well-founded Semantics. 
 *  In: Theory and Practice of Logic Programming, 5(1-2):207-242, 2003.
 * 
 * @author Sebastian Homann
 */
public class Argument extends LinkedList<Rule> implements Formula {
	
	private static final long serialVersionUID = 6017406379850600902L;

	public Argument(Rule rule) {
		this.add(rule);
	}
	
	public Argument(List<Rule> rules) {
		this.addAll(rules);
	}
	
	/**
	 * Returns the set of conclusions of this argument, which is made up of
	 * the heads of all rules contained in this argument.
	 * @return the set of conclusions
	 */
	public Set<DLPLiteral> getConclusions() {
		Set<DLPLiteral> result = new HashSet<DLPLiteral>();
		for(Rule r : this) {
			result.add(r.getConclusion().iterator().next());
		}
		return result;
	}
	
	/**
	 * Returns the set of assumptions, i.e. the set of DLPLiterals, that appear
	 * default-negated in the premise of some rule in this argument.
	 * Ex.: Let a <- b, not -c. be a rule in argument A. Then A.getAssumptions() 
	 *      will contain -c.
	 * 
	 * @return a set of literals which this argument assumes not to be true
	 */
	public Set<DLPLiteral> getAssumptions() {
		Set<DLPLiteral> result = new HashSet<DLPLiteral>();
		for(Rule r : this) {
			for(DLPElement elem : r.getPremise()) {
				if(elem instanceof DLPNot) {
					DLPLiteral assumption = elem.getLiterals().iterator().next();
					result.add(assumption);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * An argument may only contain nonempty ground rules (i.e. no constraints).
	 * Also, for an argument A = [r1, r2, ..., rn] for each literal Lj in the body
	 * of a rule ri there has to be a rule rk with k>i with head(rk) = Lj.  
	 */
	public boolean checkValid() {
		Set<DLPLiteral> foundLiterals = new HashSet<DLPLiteral>();
		@SuppressWarnings("unchecked")
		LinkedList<Rule> reversed = (LinkedList<Rule>)this.clone();
		Collections.reverse(reversed);
		for(Rule r : reversed) {
			if(!r.isGround()) {
				return false;
			}
			if(r.isConstraint()) {
				return false;
			}
			if(r.isFact()) {
				foundLiterals.add(r.getConclusion().getFormulas().iterator().next());
			}
			for(DLPElement element : r.getPremise()) {
				if(element instanceof DLPNot) {
					continue;
				}
				if(! foundLiterals.containsAll(element.getLiterals())) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString() {
		String result = new String();
		
		String delimiter = "";
		for(Rule r : this) {
			result += delimiter + r.toString();
			delimiter = ",";
		}
		
		return "[" + result + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.Formula#getSignature()
	 */
	public Signature getSignature() {
		DLPSignature result = new DLPSignature();
		for(Rule r : this) {
			result.addSignature(r.getSignature());
		}
		return result;
	}
}
