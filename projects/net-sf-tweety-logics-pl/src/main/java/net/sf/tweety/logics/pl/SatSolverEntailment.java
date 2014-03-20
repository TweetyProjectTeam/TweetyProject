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

/**
 * Abstract parent class for entailment relations utilizing SAT solvers.
 * @author Matthias Thimm
 */
public abstract class SatSolverEntailment extends EntailmentRelation<PropositionalFormula>{

	/**
	 * Converts the given set of formulas to their string representation in 
	 * Dimacs CNF.
	 * @param formulas a collection of formulas
	 * @return a string in Dimacs CNF.
	 */
	public static String convertToDimacs(Collection<PropositionalFormula> formulas){
		Conjunction conj = new Conjunction();
		conj.addAll(formulas);
		conj = conj.toCnf();
		List<Proposition> props = new ArrayList<Proposition>();
		for(Proposition p: conj.getSignature())
			props.add(p);		
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
	 * Creates a temporary file in Dimacs format.
	 * @param formulas a collection of formulas
	 * @return the file handler.
	 * @throws IOException if something went wrong while creating a temporary file. 
	 */
	protected static File createTmpDimacsFile(Collection<PropositionalFormula> formulas) throws IOException{
		String r = SatSolverEntailment.convertToDimacs(formulas);
		File f = File.createTempFile("tweety-lingeling", ".cnf");
		f.deleteOnExit();
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println(r);
		writer.close();		
		return f;
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
