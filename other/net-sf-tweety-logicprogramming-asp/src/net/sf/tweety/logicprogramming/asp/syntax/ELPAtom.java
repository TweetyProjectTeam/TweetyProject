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
package net.sf.tweety.logicprogramming.asp.syntax;

import java.util.*;

/**
 * this class models an atom, a common element for
 * building literals and rules in extended logic programs.
 * it allows textual representation of an atom compatible
 * with dlv or lparse.
 * 
 * @author Thomas Vengels
 *
 */
public class ELPAtom implements ELPLiteral {

	String	pred;
	String	terms[];

	
	/**
	 * default copy constructor
	 */
	public ELPAtom(ELPAtom other) {
		this.pred = other.pred;
		if (other.terms != null) {
			this.terms = new String[other.terms.length];
			for (int i = 0; i<other.terms.length; i++)
				this.terms[i] = new String(other.terms[i]);
		}
	}
	
	/**
	 * instantiates a new atom with a predicate
	 * of given arity, but does not set any terms.
	 * 
	 * @param predicate		predicate symbol
	 * @param arity			arity of the predicate
	 */
	public ELPAtom(String predicate, int arity) {
		this.pred = predicate;
		terms = (arity>0)?new String[arity]:null;
	}
	
	/**
	 * creates a new atom
	 * @param predicate		predicate symbol
	 * @param terms			optional term symbol
	 */
	public ELPAtom(String predicate, String ...terms) {
		this.pred = predicate;
		this.terms = (terms.length==0)?null:terms;
	}	
	
	/**
	 * create a new atom from a given list of
	 * string symbols.
	 * 
	 * @param symbols	source strings
	 * @param nterm		number of strings to read from symbols
	 */
	public ELPAtom(Collection<String> symbols) {
		Iterator<String> s = symbols.iterator();
		this.pred = s.next();
		if (symbols.size() > 1) {
			this.terms = new String[symbols.size()-1];			
			for (int i = 1; i < symbols.size(); i++) {				
				this.terms[i-1] = s.next();
			}
		} else {
			terms = null;
		}
	}
	
	/**
	 * returns the predicate symbol of this atom
	 * @return	predicate symbol (as string)
	 */
	public String getPredicate() {
		return this.pred;
	}
	
	/**
	 * returns the arity of the predicate
	 * @return	arity (as integer)
	 */
	public int getArity() {
		return (terms==null)?0:terms.length;
	}
	
	/**
	 * returns a term at a given index
	 * @param i		term number i
	 * @return		term i
	 */
	public String getTerm(int i) {
		return this.terms[i];
	}
	
	/**
	 * returns a term as an integer value (if casting
	 * to an integer is possible).
	 * 
	 * @param i		term index i
	 * @return		integer value of term i
	 */
	public int getTermAsInt(int i) {
		return Integer.parseInt(this.terms[i]);
	}
	
	
	/**
	 * create an atom based on this atom's predicate
	 * symbol and number of terms.
	 * 
	 * @param terms		predicate arguments 
	 * @return		atom made of this object's predicate and given term arguments
	 */
	public ELPAtom instantiate(String ...terms) {
		if ((this.terms == null) && (terms == null))
			return new ELPAtom(this.pred);
		
		if ((this.terms != null) && (terms != null))
			if (this.terms.length != terms.length)
				return null;
		
		return new ELPAtom(this.pred, terms);
	}
	
	
	/**
	 * set all terms according to atom other.
	 * 
	 * @param other atom to set terms from
	 */
	public void setTerms(ELPAtom other) {
		if (this.getArity() != other.getArity())
			return;
		for(int i = 0; i < this.getArity(); i++)
			this.setTerm(i, other.getTerm(i));
	}
	
	/**
	 * returns all terms 
	 * 
	 * @return array of terms (may be null if arity is 0).
	 */
	public String[] getTerms() {
		return this.terms;
	}
	
	/**
	 * returns the predicate symbol and the arity.
	 * 
	 * @return	string composed of predicate name, followed by "/" and arity
	 */
	public String getType() {
		return this.getPredicate() + "/" + this.getArity();
	}
	
	/**
	 * translates the atom into a human readable string.
	 * the output conforms datalog syntax (dlv or lparse
	 * compatible).
	 */
	public String toString() {
		if (terms == null)
			return pred;
		else {
			String ret = pred +"("+terms[0];
			for (int i = 1; i < terms.length; i++) {
				ret += ","+terms[i];
			}
			ret += ")";
			
			return ret;
		}
	}
	
	@Override
	public boolean isAtom() {
		return true;
	}

	@Override
	public boolean isDefaultNegated() {
		return false;
	}

	@Override
	public boolean isStrictNegated() {
		return false;
	}

	@Override
	public ELPLiteral getLiteral() {
		return this;
	}

	@Override
	public ELPAtom getAtom() {
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ELPAtom))
			return false;
		
		ELPAtom a = (ELPAtom) o;
		
		return a.toString().equals(this.toString());
	}

	public int hashCode() {
		return this.toString().hashCode();
	}
	
	
	/**
	 * assigns the i-th term to given argument. this method does
	 * not check i against the predicate arity.
	 * @param i		index of term to set
	 * @param s		term string
	 */
	public void setTerm(int i, String s) {
		if ((i >= 0) && (i < terms.length))
			terms[i] = s;
	}

	/**
	 * factory method to create an arithmetic plus literal
	 * @param op1		first operand
	 * @param op2		second operand
	 * @param result	destination variable
	 * @return			predicate representing "RESULT = OP1 + OP2" 
	 */
	static public ELPAtom Plus(String op1, String op2, String result) {
		return new ArithmeticPredicate("+", op1, op2, result);
	}
	
	/**
	 * factory method to create an arithmetic mul literal
	 * @param op1		first operand
	 * @param op2		second operand
	 * @param result	destination variable
	 * @return			predicate representing "RESULT = OP1 * OP2" 
	 */
	static public ELPAtom Mul(String op1, String op2, String result) {
		return new ArithmeticPredicate("*", op1, op2, result);
	}
	
	/**
	 * factory method to create a greater relation literal
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND > RIGHTHAND" 
	 */
	static public ELPAtom Gtr(String lefthand, String righthand) {
		return new RelationPredicate(">",lefthand,righthand);
	}
	
	/**
	 * factory method to create a greater relation literal
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND < RIGHTHAND" 
	 */
	static public ELPAtom Less(String lefthand, String righthand) {
		return new RelationPredicate("<",lefthand,righthand);
	}
	
	/**
	 * factory method to create a greater equal relation
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND >= RIGHTHAND" 
	 */
	static public ELPAtom GtrEq(String lefthand, String righthand) {
		return new RelationPredicate(">=",lefthand,righthand);
	}
	
	/**
	 * factory method to create a less equal relation
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND <= RIGHTHAND" 
	 */
	static public ELPAtom LessEq(String lefthand, String righthand) {
		return new RelationPredicate("<=",lefthand,righthand);
	}
	
	/**
	 * factory method to create an equality relation
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND == RIGHTHAND" 
	 */
	static public ELPAtom Equal(String lefthand, String righthand) {
		return new RelationPredicate("==",lefthand,righthand);
	}			

	/**
	 * factory method to create an unequality relation
	 * @param lefthand		first operand
	 * @param righthand		second operand
	 * @return			predicate representing "LEFTHAND != RIGHTHAND" 
	 */
	static public ELPAtom UnEq(String lefthand, String righthand) {
		return new RelationPredicate("!=",lefthand,righthand);
	}
	
	/**
	 * factory method to create a builtin aggregate fact
	 * like "#max{X:a(A)} = Y"
	 */
	static public ELPAtom Aggregate(String pred, SymbolicSet symset, String rightoperator, String rightguard) {
		return new AggregatePredicate(pred,symset,rightoperator,rightguard);
	}
	
	/**
	 * factory method to create a guarded builtin aggregate fact
	 * like "1 < #count{X:a(A)} < 3"
	 */
	static public ELPAtom Aggregate(String pred, String lguard, String lgop, String rguard, String rgop, SymbolicSet symset) {
		return new AggregatePredicate(pred, lguard,lgop, rguard,rgop,symset);
	}
	
	/**
	 * factory method to create #int predicates
	 */
	static public ELPAtom Int(String value) {
		return new IntPredicate(value);
	}

	@Override
	public boolean isPredicate() {
		return false;
	}
}
