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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.AssociativeFolFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Contradiction;
import net.sf.tweety.logics.fol.syntax.Equivalence;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Implication;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.Tautology;

/**
 * 
 * This class prints single first-order logic formulas and knowledge bases to 
 * the SPASS format.
 * <p>
 * A SPASS input file consists of the following parts:
 * <ul>
 * <li> Description: Contains meta-information about the problem, i.e. name, author, satisfiability
 * <li> Symbols: Signature declaration 
 * <li> Axioms: a list of formulas
 * <li> Conjectures: a list of formulas
 * </ul>
 * SPASS attempts to prove that the conjunction of all axioms implies the
 * disjunction of all conjectures.
 * 
 * @see net.sf.tweety.logics.fol.reasoner.SpassFolReasoner 
 * @author Anna Gessler
 */

public class SPASSWriter {
	
	/**
	 * Output is redirected to this writer.
	 */
	final Writer writer;
	
	/**
	 * Creates a new SPASSWriter.
	 * @param writer Output is redirected to this writer.
	 */
	public SPASSWriter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Creates a new SPASSWriter.
	 */
	public SPASSWriter() {
		this.writer = new StringWriter();
	}
	
	
	/**
	 * Prints the contents of a SPASS problem file for a given knowledge base and a formula.
	 * @param kb a knowledge base
	 * @param formula a relational formula
	 * @throws ParserException if parsing fails
	 * @throws IOException if an IO issue occurs
	 */
	public void printProblem(FolBeliefSet kb, RelationalFormula formula) throws ParserException, IOException {
		FolSignature signature = (FolSignature) kb.getMinimalSignature();
		signature.addSignature(formula.getSignature());
		
		String problem = "begin_problem(UnnamedProblem).\n\n";
		problem += printDescription()+ "\n";
		problem += printSignature(signature) + "\n";
		problem += printFormulas(kb, formula) + "\n";;
		problem += "end_problem.";

		writer.write(problem);
	}
	
	/**
	 * Generates a generic description part for a SPASS problem file.
	 * @return a string containing the description part
	 */
	private String printDescription() {
		String genericDescription =
		"list_of_descriptions.\n" + 
		"name({*UnnamedProblem*}).\n" + 
		"author({*TweetyUser*}).\n" + 
		"status(unknown).\n" + 
		"description({*No description*}).\n" + 
		"end_of_list.\n";
		return genericDescription;
	}
	
	
	/**
	 * Prints the symbols declaration for a SPASS input file.
	 * First-order quantifiers and operators are do not need to be declared. All declared
	 * symbols have to be different from each other.
	 * 
	 * @param signature FolSignature of the problem
	 * @return a string containing the signature declaration
	 */
	private String printSignature(FolSignature signature) {
		String symbols = "list_of_symbols.\n";
		
		Set<Functor> functors = signature.getFunctors();
		Set<Sort> sorts = signature.getSorts();
		
		//Collect all constants
		Set<String> constants = new HashSet<String>();
		for (Sort s : sorts) {
			Set<Term<?>> terms = s.getTerms(); 
			for (Term<?> t: terms) {
				if (!constants.contains(t.toString()) && !(t instanceof Variable)) 
					constants.add(t.toString());
			}
				
		}	
	
		//Print functors
		if (!functors.isEmpty() || !constants.isEmpty()) {
			String functorsString = "functions[";
			for (Functor f : functors) {
				functorsString += "(" + f.getName() + "," +  f.getArity() + "),";
			}
			for (String c : constants) {
				functorsString += "(" + c + ",0),";
			}
			functorsString = functorsString.substring(0, functorsString.length()-1)+ "].\n";
			symbols += functorsString;
		}
		
		//Print predicates
		Set<Predicate> predicates = signature.getPredicates();
		if (!predicates.isEmpty()) {
			String predicatesString = "predicates[";
			for (Predicate p : predicates) {
				predicatesString += "(" + p.getName() + "," +  p.getArity() + "),";
			}
			predicatesString = predicatesString.substring(0, predicatesString.length()-1)+ "].\n";
			symbols += predicatesString;
		}
		
		//Print sorts
		boolean hasSorts = false;
		if (!sorts.isEmpty()) {
			hasSorts = true;
			String sortsString = "sorts[";
			for (Sort s : sorts) {
				sortsString += s.getName() + ",";
			}
			sortsString = sortsString.substring(0, sortsString.length()-1)+ "].\n";
			symbols += sortsString;
		}
		
		symbols += "end_of_list.\n";
		
		if (hasSorts) {
			String sortsDeclarations = "\nlist_of_declarations.\n";
			for (Sort s : sorts) {
				Set<Term<?>> terms = s.getTerms(); 
				for (Term<?> t : terms) {
					if (!(t instanceof Variable))
						sortsDeclarations += s.getName() + "(" + t.toString() + ").\n"; 
					}
			}
			
			return symbols + sortsDeclarations + "end_of_list.\n";
		}	
		
		return symbols;		
	}
	
	/**
	 * Prints the axioms declaration and conjectures declaration for a SPASS input file. 
	 * All formulas must be closed. 
	 * @param kb a knowledge base of relational formulas that will be used as the axioms declarations
	 * @param formula a formula that will be used as the conjectures declaration
	 * @return a string containing the axioms and conjectures declaration
	 * @see <a href="https://webspass.spass-prover.org/help/spass-input-syntax15.pdf">https://webspass.spass-prover.org/help/spass-input-syntax15.pdf</a>
	 */
	private String printFormulas(FolBeliefSet kb, RelationalFormula formula) {
		String axioms = "list_of_formulae(axioms).\n";
		
		for (Object f : kb) {
			axioms += "formula(" + printFormula((RelationalFormula) f) + ").\n";
		}
		
		String conjectures = "list_of_formulae(conjectures).\n";
		conjectures += "formula(" + printFormula(formula) + ").\n";
	
		return axioms + "end_of_list.\n\n" + conjectures + "end_of_list.\n";
	}
	
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return "not(" + printFormula(n.getFormula()) + ")";
		}
		if (f instanceof ForallQuantifiedFormula || f instanceof ExistsQuantifiedFormula) {
			FolFormula fqf = (FolFormula) f;
			boolean existential = f instanceof ExistsQuantifiedFormula;
			String result = "";
			result += existential ? "exists([" : "forall([";
			
			for(Variable v: fqf.getQuantifierVariables()) {
				result += v;
				result += ",";
			}
			result = result.substring(0, result.length()-1) + "]";
			result += "," + printFormula(fqf.getFormula()) + ")";
			return result;
		}
		if (f instanceof AssociativeFolFormula) {
			AssociativeFolFormula d = (AssociativeFolFormula) f;
			Iterator<RelationalFormula> i = d.getFormulas().iterator();
			String result = (f instanceof Conjunction) ? "and(" : "or(";
			while (i.hasNext())
				result += printFormula(i.next()) + ",";
			return result.substring(0,result.length()-1) + ")";
		}
		if (f instanceof Implication) {
			Implication i = (Implication) f;
			return "implies(" + printFormula(i.getFormulas().getFirst()) + "," + printFormula(i.getFormulas().getSecond()) +")";
		}
		if (f instanceof Equivalence) {
			Equivalence e = (Equivalence) f;
			return "equiv(" + printFormula(e.getFormulas().getFirst()) + "," + printFormula(e.getFormulas().getSecond()) +")";
		}
		if (f instanceof Tautology) {
			return "true";
		}
		if (f instanceof Contradiction) {
			return "false";
		}
		return f.toString();
	}

	public void close() throws IOException {
		writer.close();
	}

}
