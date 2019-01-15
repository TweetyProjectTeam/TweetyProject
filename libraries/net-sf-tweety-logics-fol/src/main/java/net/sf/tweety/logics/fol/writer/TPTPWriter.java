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
package net.sf.tweety.logics.fol.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.AssociativeFolFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.EqualityPredicate;
import net.sf.tweety.logics.fol.syntax.Equivalence;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolAtom;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.InequalityPredicate;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * Prints single first-order logic formulas and full knowledge bases to TPTP format.
 * 
 * @see net.sf.tweety.logics.fol.reasoner.EFOLReasoner
 * @author Nils Geilen
 * @author Anna Gessler
 *
 */

public class TPTPWriter implements FolWriter {

	/**
	 * Output is redirected to this writer
	 */
	final Writer writer;
	
	/**
	 * Creates new TPTPWriter
	 * @param writer output is redirected to this writer
	 */
	public TPTPWriter(Writer writer) {
		super();
		this.writer = writer;
	}
	
	/**
	 * Creates new TPTPWriter
	 */
	public TPTPWriter() {
		super();
		this.writer = new StringWriter();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printQuery(net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	public void printQuery( FolFormula query) throws IOException {
		writer.write( "fof(" + "query" + ", conjecture, " + printFormula(query) + ").\n");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printEquivalence(net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException {
		writer.write( "fof(" + "equation" + ", conjecture, " + printFormula(a) + " <=> "+ printFormula(b) + ").\n");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printBase(net.sf.tweety.logics.fol.FolBeliefSet)
	 */
	public void printBase(FolBeliefSet b) throws IOException {
			// print types
			FolSignature sig = (FolSignature) b.getSignature();
			for (Constant c : sig.getConstants())
				writer.write(makeAxiom(c + "_type", c.getSort() + "(" + c + ")"));

			// print types of functions
			for(Functor f: sig.getFunctors()){
				String vars_str = "";
				boolean isFirst = true;
				for(int i = 0; i < f.getArity(); i++)
					if(isFirst){
						vars_str = "X" + i;
						isFirst = false;
					}else vars_str += ",X"+i;
				writer.write(makeAxiom(f.getName() + "_type", "(! [" + vars_str + "]: (" + f.getTargetSort() + "(" + f.getName() + "(" + vars_str + "))))"));
			}
			
			// print facts
			int axiom_id = 0;
			for (FolFormula f : b)
				writer.write(makeAxiom("axiom_" + ++axiom_id, printFormula(f)));
	}

	/**
	 * Creates a TPTP axiom out of a given name and formula.
	 * 
	 * @param name
	 *            the identifying name of the axiom
	 * @param body
	 *            the axiom's formula in TPTP format
	 * @return the axiom as a string
	 */
	private String makeAxiom(String name, String body) {
		return "fof(" + name + ", axiom, " + body + ").\n";
	}

	/**
	 * Crates a type check or type def for a variable
	 * 
	 * @param v
	 *           a variable
	 * @return type(var)
	 */
	private String printVar(Variable v) {
		return v.getSort() + parens(v.toString());
	}
	
	/**
	 * Creates a TPTP representation of a formula.
	 * 
	 * @param f
	 *            a formula
	 * @return a 
	 * 			  string that represents the formula in TPTP format
	 */
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return parens("~ " + parens(printFormula(n.getFormula())));
		}
		if (f instanceof ExistsQuantifiedFormula || f instanceof ForallQuantifiedFormula) {
			FolFormula fqf = (FolFormula) f;
			boolean existential = f instanceof ExistsQuantifiedFormula;
			String result = existential ? "? [" : "! [";
			result += join(fqf.getQuantifierVariables(), ", ") + "]: (";
			Iterator<Variable> vars = fqf.getQuantifierVariables().iterator();
			
			//Do not print the sort "_Any" (which is used by Tweety internally only)
			//For variables of other sorts, enforce correct sorts by adding an
			//implication "type(var) => (rest of quantified formula)" for forall quantified formulas 
			//or a conjunct "type(var) & (rest of quantified formula)" for exists quantified formulas
			boolean first = true;
			while (vars.hasNext()) {
				Variable v = vars.next();
				if (first) {
					if (!(v.getSort().toString().equals("_Any"))) {
						result += "(";
						first = false;
						result +=  printVar(v);
						continue;
						}
				}
				if (!(v.getSort().toString().equals("_Any"))) 
					result += " & "+ printVar(v);  
				}
			if (!first) {
				result += ")";
				result += existential ? " & " : " => "; 
				}
			
			return parens(result + printFormula(fqf.getFormula()))+")";
		}
		if (f instanceof AssociativeFolFormula) {
			AssociativeFolFormula d = (AssociativeFolFormula) f;
			Iterator<RelationalFormula> i = d.getFormulas().iterator();
			String result = printFormula(i.next());
			String delimiter = (f instanceof Conjunction) ? " & " : " | ";
			while (i.hasNext())
				result += delimiter + printFormula(i.next());
			return parens(result);
		}
		if(f instanceof Tautology) {
			return "$true";
		}
		if(f instanceof Contradiction) {
			return "$false";
		}	
		if (f instanceof Implication) {
			Implication e = (Implication) f;
			return parens(printFormula(e.getFormulas().getFirst()) + "=>" + printFormula(e.getFormulas().getSecond()));
		}
		if (f instanceof Equivalence) {
			Equivalence e = (Equivalence) f;
			return parens(printFormula(e.getFormulas().getFirst()) + "<=>" + printFormula(e.getFormulas().getSecond()));
		}
		if (f instanceof FolAtom) {
			FolAtom at = (FolAtom) f;
			Predicate p = at.getPredicate();
			if (p instanceof EqualityPredicate) {
				Iterator<Term<?>> it = at.getArguments().iterator();
				return parens(it.next().toString() + "=" + it.next().toString());
			}
			else if (p instanceof InequalityPredicate) {
				Iterator<Term<?>> it = at.getArguments().iterator();
				return parens(it.next().toString() + "!=" + it.next().toString());
			}
		}
		return f.toString();
	}

	/**
	 * Puts str in parentheses.
	 * 
	 * @param str
	 *            a string
	 * @return (str)
	 */
	private String parens(String str) {
		return "(" + str + ")";
	}

	/**
	 * Joins the elements of c.
	 * 
	 * @param c
	 *            a collection
	 * @param delimiter
	 *            will separate elements
	 * @return a string representation of the elements of c separated by the
	 *         delimiter
	 */
	private <T> String join(Collection<T> c, String delimiter) {
		String result = "";
		boolean first = true;
		for (T o : c) {
			if (first)
				first = false;
			else
				result += delimiter;
			result += o;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#close()
	 */
	public void close() throws IOException {
		writer.close();
	}
	

	@Override
	public String toString() {
		return writer.toString();
	}

}
