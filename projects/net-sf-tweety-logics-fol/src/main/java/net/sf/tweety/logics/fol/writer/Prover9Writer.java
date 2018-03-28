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
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * Prints single first-order logic formulas and full knowledge bases to Prover9 format
 * (<a href="https://www.cs.unm.edu/~mccune/mace4/manual/2009-11A/">https://www.cs.unm.edu/~mccune/mace4/manual/2009-11A/</a>).
 * @see net.sf.tweety.logics.fol.prover.Prover9 
 * @author Nils Geilen
 */

public class Prover9Writer implements FolWriter {
	
	/**
	 * output is redirected to this writer
	 */
	Writer writer;
	
	/**
	 *  creates new Prover9Writer
	 * @param writer output is redirected to this writer
	 */
	public Prover9Writer(Writer writer) {
		super();
		this.writer = writer;
	}
	
	/**
	 * creates new Prover9Writer
	 */
	public Prover9Writer() {
		this.writer = new StringWriter();
	}


	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printQuery(net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	public void printQuery(FolFormula query) throws IOException {
		writer.write("formulas(goals).\n");
		writer.write("\t" + printFormula(query) + ".\n");
		writer.write("end_of_list.\n");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printEquivalence(net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException {
		writer.write("formulas(goals).\n");
		writer.write("\t" + printFormula(a) + " <-> " + printFormula(b) + ".\n");
		writer.write("end_of_list.\n");
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.writer.FolWriter#printBase(net.sf.tweety.logics.fol.FolBeliefSet)
	 */
	public void printBase(FolBeliefSet b)  throws IOException {
			writer.write("formulas(sos).\n");
			
			// print types
			FolSignature sig = (FolSignature) b.getSignature();
			for (Constant c : sig.getConstants())
				writer.write( "\t"+c.getSort() + "(" + c + ").\n");

			// print facts
			for (FolFormula f : b)
				writer.write("\t"+printFormula(f)+".\n");
			
			writer.write("end_of_list.\n");
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
	 * Creates a representation of a formula in Prover9 format.
	 * 
	 * @param f	
	 * 			a formula
	 * @return 
	 * 			a string that represents the formula in Prover9 format
	 */
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return parens("- " + parens(printFormula(n.getFormula())));
		}
		if (f instanceof ForallQuantifiedFormula || f instanceof ExistsQuantifiedFormula) {
			FolFormula fqf = (FolFormula) f;
			boolean existential = f instanceof ExistsQuantifiedFormula;
			String result = "";
			for(Variable v: fqf.getQuantifierVariables()) {
				result += existential ? "exists " : "all ";
				result += v;
				result += " (";
				result += printVar(v);
				result +=  existential ? " & " : " -> ";
				result += "(";
			}
			result += printFormula(fqf.getFormula());
			for(int i = 0; i < fqf.getQuantifierVariables().size(); i++) 
				result += "))";
			return result;
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
			return "$T";
		}
		if(f instanceof Contradiction) {
			return "$F";
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
