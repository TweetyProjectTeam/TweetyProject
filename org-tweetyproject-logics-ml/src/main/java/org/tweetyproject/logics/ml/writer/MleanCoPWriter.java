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
	 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
	 */
package org.tweetyproject.logics.ml.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.fol.syntax.AssociativeFolFormula;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Equivalence;
import org.tweetyproject.logics.fol.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.ForallQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.Implication;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;

/**
 * Prints single FOL and modal formulas to the MleanCoP format:
 *	<br>
 *	<br>Negation: '~'
 *	<br>Conjunction: ','
 *	<br>Disjunction: ';'
 *	<br>Implication: '=&gt;'
 *	<br>Equivalence: '&lt;=&gt;'
 *	<br>Universal quantifier: 'all X:'
 *	<br>Existential quantifier: 'ex X:'
 *	<br>Modal box operator (Necessity): '#'
 *	<br>Modal diamond operator (Possibility): '*'
 *
 * @author Anna Gessler
 * @author Nils Geilen
 */
public class MleanCoPWriter {
	/**
	 * Output is redirected to this writer.
	 */
	final Writer writer;
	
	/**
	 * Creates a new MleanCoPWriter.
	 * @param writer Output is redirected to this writer.
	 */
	public MleanCoPWriter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Creates a new MleanCoPWriter.
	 */
	public MleanCoPWriter() {
		this.writer = new StringWriter();
	}
	
	/**
	 * Prints the query.
	 * 
	 * @param f the query, a first-order logic or modal formula
	 * @throws IOException if an IO issue occurs.
	 */
	public void printQuery(RelationalFormula f) throws IOException {
		writer.write("f(" + printFormula(f) + ").");
	}
	
	/**
	 * Creates a representation of a formula in MleanCoP format.
	 * 
	 * @param  f a formula
	 * @return a string that represents the formula in MleanCoP format
	 */
	private String printFormula(RelationalFormula f) {
		if (f instanceof Possibility) {
			Possibility p = (Possibility) f;
			return parens("* " + parens(printFormula(p.getFormula())));
		}
		else if (f instanceof Necessity) {
			Necessity n = (Necessity) f;
			return parens("# " + parens(printFormula(n.getFormula())));
		}
		else if (f instanceof Negation) {
			Negation n = (Negation) f;
			return parens("~ " + parens(printFormula(n.getFormula())));
		}
		else if (f instanceof ForallQuantifiedFormula || f instanceof ExistsQuantifiedFormula) {
			FolFormula fqf = (FolFormula) f;
			boolean existential = f instanceof ExistsQuantifiedFormula;
			String result = "";
			for(Variable v: fqf.getQuantifierVariables()) {
				result += existential ? "ex " : "all ";
				result += v;
				result += ": " ;
			}
			result += printFormula(fqf.getFormula());
			return result;
		}
		else if (f instanceof AssociativeFolFormula) {
			AssociativeFolFormula d = (AssociativeFolFormula) f;
			Iterator<RelationalFormula> i = d.getFormulas().iterator();
			String result = printFormula(i.next());
			String delimiter = (f instanceof Conjunction) ? " , " : " ; ";
			while (i.hasNext())
				result += delimiter + printFormula(i.next());
			return parens(result);
		}	
		else if (f instanceof Implication) {
			Implication i = (Implication) f;
			return parens(printFormula(i.getFormulas().getFirst()) + "=>" + parens(printFormula(i.getFormulas().getSecond())));
		}
		else if (f instanceof Equivalence) {
			Equivalence i = (Equivalence) f;
			return parens(printFormula(i.getFormulas().getFirst()) + "<=>" + parens(printFormula(i.getFormulas().getSecond())));
		}
		return f.toString();
	}
	
	/**
	 * Puts a string in parentheses.
	 * 
	 * @param str
	 *            a string
	 * @return (str)
	 */
	private String parens(String str) {
		return "(" + str + ")";
	}
	
	/**
	 * Closes the Writer.
	 * @throws IOException if an IO issue occurs.
	 */
	public void close() throws IOException {
		writer.close();
	}


}

