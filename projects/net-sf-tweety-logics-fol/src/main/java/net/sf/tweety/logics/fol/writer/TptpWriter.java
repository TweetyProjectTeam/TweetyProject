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
package net.sf.tweety.logics.fol.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.AssociativeFOLFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.QuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * Prints single fol formulas and full knowledge bases to TPTP
 * 
 * @author Nils Geilen
 *
 */

public class TptpWriter implements FolWriter {

	final Writer writer;
	

	public TptpWriter(Writer writer) {
		super();
		this.writer = writer;
	}
	
	public TptpWriter() {
		super();
		this.writer = new StringWriter();
	}

	/**
	 * Creates a TPTP conjecture.
	 * 
	 * @param name
	 *            the identifying name of the conjecture
	 * @param query
	 *            the formula to be queried
	 * @return the query as TPTP
	 */
	public void printQuery( FolFormula query) throws IOException {
		writer.write( "fof(" + "query" + ", conjecture, " + printFormula(query) + ").\n");
	}
	
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException {
		writer.write( "fof(" + "equation" + ", conjecture, " + printFormula(a) + " <=> "+ printFormula(b) + ").\n");
	}

	/**
	 * Prints TPTP representation of a knowledge base to w.
	 * 
	 * @param w
	 *            a writer
	 * @param b
	 *            a knowledge base
	 */
	public void printBase(FolBeliefSet b) throws IOException {
			// print types
			FolSignature sig = (FolSignature) b.getSignature();
			for (Constant c : sig.getConstants())
				writer.write(makeAxiom(c + "_type", c.getSort() + "(" + c + ")"));

			// print facts
			int axiom_id = 0;
			for (FolFormula f : b)
				writer.write(makeAxiom("axiom_" + ++axiom_id, printFormula(f)));
	}

	/**
	 * Creates an TPTP axiom out of body.
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
	 *            a variable
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
	 * @return f in TPTP format
	 */
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return parens("~ " + printFormula(n.getFormula()));
		}
		if (f instanceof QuantifiedFormula) {
			QuantifiedFormula fqf = (QuantifiedFormula) f;
			boolean existential = f instanceof ExistsQuantifiedFormula;
			String result = existential ? "? [" : "! [";
			result += join(fqf.getQuantifierVariables(), ", ") + "]: (";
			// check if variables are of correct type
			Iterator<Variable> i = fqf.getQuantifierVariables().iterator();
			result += printVar(i.next());
			while (i.hasNext())
				result += " & " + printVar(i.next());
			result += existential ? " & " : " => ";
			return parens(result + printFormula(fqf.getFormula()))+")";
		}
		if (f instanceof AssociativeFOLFormula) {
			AssociativeFOLFormula d = (AssociativeFOLFormula) f;
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
	
	public void close() throws IOException {
		writer.close();
	}
	

	@Override
	public String toString() {
		return writer.toString();
	}

}
