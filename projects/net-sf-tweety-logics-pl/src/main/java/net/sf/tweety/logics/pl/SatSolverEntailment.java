package net.sf.tweety.logics.pl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.EntailmentRelation;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.util.Pair;

/**
 * Abstract parent class for entailment relations utilizing SAT solvers.
 * @author Matthias Thimm
 */
public abstract class SatSolverEntailment extends EntailmentRelation<PropositionalFormula>{

	/** For temporary files. */
	public static File tempFolder = null;
	
	/**
	 * Converts the given set of formulas to their string representation in 
	 * Dimacs CNF. Note that a single formula may be represented as multiple
	 * clauses, so there is no simple correspondence between the formulas of the
	 * set and the Dimacs representation. Use <code>convertToDimacs(.)</code> for
	 * obtaining a map between those.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return a string in Dimacs CNF.
	 */
	public static String convertToDimacs(Collection<PropositionalFormula> formulas, List<Proposition> props){
		Conjunction conj = new Conjunction();
		conj.addAll(formulas);
		conj = conj.toCnf();			
		String s = "p cnf " + props.size() + " " + conj.size() + "\n";
		for(PropositionalFormula p1: conj){
			// as conj is in CNF all formulas should be disjunctions
			Disjunction disj = (Disjunction) p1;
			for(PropositionalFormula p2: disj){
				if(p2 instanceof Proposition)
					s += (props.indexOf(p2) + 1) + " ";
				else if(p2 instanceof Negation){
					s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
				}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");				
			}			
			s += "0\n";
		}
		return s;
	}

	/**
	 * Converts the given set of formulas to their string representation in 
	 * Dimacs CNF. The return value is a pair of<br/>
	 * 1.) the string representation<br/>
	 * 2.) a list of collections of formulas (all from the given set); the interpretation of this list
	 * is that the generated clause no K originated from the propositional formula given at index k.
	 * @param formulas a collection of formulas.
	 * @return a string in Dimacs CNF and a mapping between clauses and original formulas.
	 */
	public static Pair<String,List<PropositionalFormula>> convertToDimacs(Collection<PropositionalFormula> formulas){
		List<Proposition> props = new ArrayList<Proposition>();
		for(PropositionalFormula f: formulas){
			props.removeAll(f.getAtoms());
			props.addAll(f.getAtoms());		
		}		
		List<PropositionalFormula> clauses = new ArrayList<PropositionalFormula>();
		List<PropositionalFormula> mappings = new ArrayList<PropositionalFormula>();
		for(PropositionalFormula p: formulas){
			Conjunction pcnf = p.toCnf();
			for(PropositionalFormula sub: pcnf){
				clauses.add(sub);
				mappings.add(p);
			}			
		}		
		String s = "p cnf " + props.size() + " " + clauses.size() + "\n";
		for(PropositionalFormula p1: clauses){
			// as conj is in CNF all formulas should be disjunctions
			Disjunction disj = (Disjunction) p1;
			for(PropositionalFormula p2: disj){
				if(p2 instanceof Proposition)
					s += (props.indexOf(p2) + 1) + " ";
				else if(p2 instanceof Negation){
					s += "-" + (props.indexOf((Proposition)((Negation)p2).getFormula()) + 1) + " ";
				}else throw new RuntimeException("This should not happen: formula is supposed to be in CNF but another formula than a literal has been encountered.");				
			}			
			s += "0\n";
		}
		return new Pair<String,List<PropositionalFormula>>(s,mappings);
	}
	
	/**
	 * Creates a temporary file in Dimacs format with the given proposition2variable mapping.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses.
	 * @return the file handler. 
	 * @throws IOException if something went wrong while creating a temporary file. 
	 */
	public static File createTmpDimacsFile(Collection<PropositionalFormula> formulas, List<Proposition> props) throws IOException{
		String r = SatSolverEntailment.convertToDimacs(formulas, props);
		File f = File.createTempFile("tweety-sat", ".cnf", SatSolverEntailment.tempFolder);		
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r);
		writer.close();		
		return f;
	}
	
	/**
	 * Creates a temporary file in Dimacs format and also returns a mapping between formulas and clauses.
	 * @param formulas a collection of formulas
	 * @param a list of propositions (=signature) where the indices are used for writing the clauses
	 * (a list of collections of formulas (all from the given set); the interpretation of this list
	 * is that the generated clause no K originated from the propositional formula given at index k).
	 * @return the file handler and a mapping between clauses and original formulas. 
	 * @throws IOException if something went wrong while creating a temporary file. 
	 */
	public static Pair<File,List<PropositionalFormula>> createTmpDimacsFile(Collection<PropositionalFormula> formulas) throws IOException{
		Pair<String,List<PropositionalFormula>> r = SatSolverEntailment.convertToDimacs(formulas);
		File f = File.createTempFile("tweety-sat", ".cnf");
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.print(r.getFirst());
		writer.close();		
		return new Pair<File,List<PropositionalFormula>>(f,r.getSecond());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#entails(java.util.Collection, net.sf.tweety.Formula)
	 */
	@Override
	public boolean entails(Collection<PropositionalFormula> formulas, PropositionalFormula formula) {
		Set<PropositionalFormula> fset = new HashSet<PropositionalFormula>(formulas);
		fset.add((PropositionalFormula)formula.complement());
		return !this.isConsistent(fset);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#isConsistent(java.util.Collection)
	 */
	@Override
	public abstract boolean isConsistent(Collection<PropositionalFormula> formulas);
}
