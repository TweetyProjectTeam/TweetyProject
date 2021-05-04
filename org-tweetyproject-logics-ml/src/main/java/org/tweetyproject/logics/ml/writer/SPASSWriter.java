package org.tweetyproject.logics.ml.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.AssociativeFolFormula;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Contradiction;
import org.tweetyproject.logics.fol.syntax.Equivalence;
import org.tweetyproject.logics.fol.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.ForallQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.Implication;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.fol.syntax.Tautology;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;

/**
 * This class prints single first-order modal logic formulas and knowledge bases to 
 * the SPASS format.
 * <p>
 * A SPASS input file consists of the following parts:
 * <ul>
 * <li> Description: Contains meta-information about the problem, i.e. name, author, satisfiability
 * <li> Symbols: Signature declaration 
 * <li> Axioms: a list of formulas
 * <li> Conjectures: a list of formulas
 * </ul>
 * 
 * SPASS attempts to prove that the conjunction of all axioms implies the
 * disjunction of all conjectures.
 * 
 * @see org.tweetyproject.logics.ml.reasoner.SPASSMlReasoner 
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
	 * @throws IOException if an IO issue occurs.
	 */
	public void printProblem(MlBeliefSet kb, RelationalFormula formula) throws ParserException, IOException {
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
	 * First-order quantifiers and operators do not need to be declared. All declared
	 * symbols have to be unique.
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
			predicatesString += "(genericAgent,0)" + "].\n";
			//predicatesString = predicatesString.substring(0, predicatesString.length()-1)+ "].\n";
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
	private String printFormulas(MlBeliefSet kb, RelationalFormula formula) {
		//list_of_special_formulae has to be used to allow for modal formulas
		//For pure first-order logic, list_of_formulae(axioms) is sufficient
		//EML specifies the type of special formulae, in this case "extended modal logic"
		//For SPASS Version 3.9, "eml" has to be used instead
		String axioms = "list_of_special_formulae(axioms,EML).\n";
		
		for (Object f : kb) 
			axioms += "prop_formula(" + printFormula((RelationalFormula) f) + ").\n";
		
		String conjectures = "list_of_special_formulae(conjectures,EML).\n";
		conjectures += "prop_formula(" + printFormula(formula) + ").\n";
	
		return axioms + "end_of_list.\n\n" + conjectures + "end_of_list.\n";
	}
	
	private String printFormula(RelationalFormula f) {
		if (f instanceof Negation) {
			Negation n = (Negation) f;
			return "not(" + printFormula(n.getFormula()) + ")";
		}
		if (f instanceof Possibility) {
			Possibility p = (Possibility) f;
			return "dia(genericAgent," + printFormula(p.getFormula()) + ")";
		}
		if (f instanceof Necessity) {
			Necessity n = (Necessity) f;
			return "box(genericAgent," + printFormula(n.getFormula()) + ")";
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
