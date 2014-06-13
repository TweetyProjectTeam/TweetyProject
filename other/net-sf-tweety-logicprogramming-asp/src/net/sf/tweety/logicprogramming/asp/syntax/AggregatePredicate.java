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
package net.sf.tweety.logicprogramming.asp.syntax;

import java.util.Iterator;

/**
* private class to model aggregate predicates like
* "#times", "#max", "#count", "#min".
* aggregate predicates are dlv-specific.
* 
* @author Thomas Vengels
*
*/
public class AggregatePredicate extends ELPAtom {
	// symbolic set
	SymbolicSet				symset;
	String					lguard,lop,rguard,rop;
	
	/**
	 * constructor to model aggregate expression like
	 * #time{vars:lits} = assignment
	 * 
	 * @param pred	aggregate functor name
	 * @param vars	local variables
	 * @param lits	literals to aggregate over
	 * @param assignment righthand-side assignment
	 */
	public AggregatePredicate(String pred, SymbolicSet	symset, String rop, String rsym) {
		super(pred);
		lguard = null;
		this.lop = null;
		this.rguard = rsym;
		this.rop = rop;
		this.symset = symset;
	}
	
	/**
	 * constructor to model any kind of guarded aggregate expression
	 * @param pred		aggregate functor name
	 * @param lguard	left guard
	 * @param lgop		left guard relation symbol
	 * @param rguard	right guard
	 * @param rgop		right guard relation symbol
	 * @param vars		variables of aggregate's symbolic set
	 * @param lits		literals of aggregate's symbolic set
	 */
	public AggregatePredicate(String pred, String lguard, String lgop, String rguard, String rgop, SymbolicSet symset) {
		super(pred);
		this.lguard = lguard;
		this.lop = lgop;
		this.rguard = rguard;
		this.rop = rgop;
		this.symset = symset;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		if (lguard != null) {
			ret += lguard + " " + lop + " ";
		}
		ret += "#"+pred + "{";
		
		boolean first = true;
		Iterator<String> iter = symset.getVariables().iterator();
		while (iter.hasNext()) {
			if (!first)
				ret += ",";
			ret += iter.next();
			first = false;
		}
		
		ret+=":";
		
		first = true;
		Iterator<ELPLiteral> iter2 = symset.getLiterals().iterator();
		while (iter2.hasNext()) {
			if (!first)
				ret += ",";
			ret += iter2.next();
			first = false;
		}
		
		ret += "}";
		
		if (rguard != null) {
			ret += " " + rop + " " + rguard;
		}
		
		return ret;
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
}