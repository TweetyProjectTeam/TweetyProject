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
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.QuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.Tautology;

public class Prover9Writer implements FolWriter {
	
	Writer writer;
	
	public Prover9Writer(Writer writer) {
		super();
		this.writer = writer;
	}
	
	public Prover9Writer() {
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
	public void printQuery(FolFormula query) throws IOException {
		writer.write("formulas(goals).\n");
		writer.write("\t" + printFormula(query) + ".\n");
		writer.write("end_of_list.\n");
	}
	
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException {
		writer.write("formulas(goals).\n");
		writer.write("\t" + printFormula(a) + " <-> " + printFormula(b) + ".\n");
		writer.write("end_of_list.\n");
	}

	/**
	 * Prints TPTP representation of a knowledge base to w.
	 * 
	 * @param w
	 *            a writer
	 * @param b
	 *            a knowledge base
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
	 * Creates a TPTP representation of a formula.
	 * 
	 * @param f
	 *            a formula
	 * @return f in TPTP format
	 */
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return parens("- " + printFormula(n.getFormula()));
		}
		if (f instanceof QuantifiedFormula) {
			QuantifiedFormula fqf = (QuantifiedFormula) f;
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

	
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public String toString() {
		return writer.toString();
	}
	
	

}
