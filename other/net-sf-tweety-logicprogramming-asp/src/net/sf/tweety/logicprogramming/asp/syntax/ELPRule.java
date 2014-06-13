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

import java.util.*;

/**
 * this class models a rule used in extended logics programs.
 * each rule consists of two list: Hl, list of head literals,
 * and Bl, list of body literals.
 * if Bl is empty, we call the rule a fact. if Hl is empty,
 * we call the rule a constraint.
 * Hl is always a (possibly empty) list of positive literals 
 * (ELPAtom instances), and if |Hl| > 1 we call the 
 * rule disjunctive. Hl may not contain any instances
 * derived from ELPArithExpr.
 * Bl is always a (possibly empty) list of literals 
 * (ELPLiteral instances), where both classical and
 * default negation is allowed.
 * 
 * @author Thomas Vengels
 *
 */
public class ELPRule {
	ArrayList<ELPLiteral>	head;
	ArrayList<ELPLiteral>	body;
	
	/**
	 * returns true if the rule has at least on head, but
	 * no body literals.
	 * @return
	 */
	public boolean isFact() {
		return body.size() == 0;
	}
	
	/**
	 * returns true if the rule is a constraint (no head literals,
	 * and at least on body literal
	 * @return
	 */
	public boolean isConstraint() {
		return head.size() == 0;
	}
	
	/**
	 * indicated wether there is more than one (or zero) literals
	 * in the read of the rule.
	 * @return
	 */
	public boolean isDisjunctive() {
		if (head != null)
			return head.size() > 1;
		else
			return false;
	}
	
	/**
	 * create a new elp rule
	 */
	public ELPRule() {
		this.head = new ArrayList<ELPLiteral>(1);
		this.body = new ArrayList<ELPLiteral>(1);
	}
	
	
	/**
	 * create a rule with preallocated memory for
	 * body and head literals.
	 * @param maxHead
	 * @param maxBody
	 */
	public ELPRule(int maxHead, int maxBody) {
		this.head = new ArrayList<ELPLiteral>(maxHead);
		this.body = new ArrayList<ELPLiteral>(maxBody);
	}
	
	/**
	 * creates new rule based on an existing on.
	 * 
	 * todo: clone literals
	 * @param other	rule to copy
	 */

	public ELPRule( ELPRule other ) {
		this.head = new ArrayList<ELPLiteral>(other.nHead());
		for (int i = 0; i < other.nHead(); i++) {
			this.head.set(i, other.head.get(i));
		}
		this.body = new ArrayList<ELPLiteral>(other.nBody());
		for (int i = 0; i < other.nBody(); i++) {
			this.body.set(i, other.body.get(i));
		}
	}
	
	
	/**
	 * add a literal to Hl
	 * @param l		atom to add to Hl
	 */
	public void addHead(ELPLiteral l) {
		this.head.add(l);
	}
	
	/**
	 * add a literal to Bl
	 * @param l
	 */
	public void addBody(ELPLiteral l) {
		this.body.add(l);
	}
	
	
	public ArrayList<ELPLiteral> getBody() {
		return this.body;	
	}
	
	public void addBody(Collection<ELPLiteral> lits) {
		this.body.addAll(lits);
	}
	
	
	/**
	 * converts the rule into a human readable string
	 * representing this rule in a datalog-like syntax.
	 */
	public String toString() {
		String ret = "";
		String hs = dumpLitList(head, " v ");
		String bs = dumpLitList(body, ", ");
		if (hs != null)
			ret += hs;
		if (bs != null) {
			if (hs != null)
				ret += " ";
			ret += ":- " +bs;
		}
		ret += ".";
		return ret;
	}
	
	/**
	 * internal helper function to dump a list of literals
	 * @param ll	list of literals
	 * @param sep	literal separator 
	 * @return		text representation of ll
	 */
	protected String dumpLitList(ArrayList<ELPLiteral> ll, String sep) {
		if (ll == null)
			return null;
		if (ll.size() < 1)
			return null;
		String ret = ll.get(0).toString();
		for (int i = 1; i < ll.size(); i++) {
			ret +=sep+ll.get(i).toString();
		}
		return ret;
	}
	
	/**
	 * returns the size of |Hl|
	 * @return	number of head literals
	 */
	public int nHead() {
		return head.size();
	}
	
	/**
	 * returns the size of |Bl|, including arithmetical expressions.
	 * @return	number of body literals
	 */
	public int nBody() {
		return body.size();
	}
	
	/**
	 * returns the i-th literal in Hl
	 * @param i		literal index
	 * @return		i-th literal in head
	 */
	public ELPLiteral getHead(int i) {
		return head.get(i);
	}
	
	/**
	 * return the i-th literal in Bl
	 * @param i		literal index
	 * @return		i-th literal in body
	 */
	public ELPLiteral getBody(int i) {
		return body.get(i);
	}
}
