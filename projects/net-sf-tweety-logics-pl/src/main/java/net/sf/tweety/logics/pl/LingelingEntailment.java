package net.sf.tweety.logics.pl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.Exec;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Uses the Lingeling SAT solver for defining entailment
 * (tested with Lingeling version ats1 ce8c04fc97ef07cf279c0c5dcbbc7c5d9904230a).
 * See http://fmv.jku.at/lingeling/. 
 * NOTE: so far no further configuration possible 
 * @author Matthias Thimm
 */
public class LingelingEntailment extends SatSolverEntailment {

	/** The binary location of Lingeling. */
	private String binaryLocation;
	
	/**
	 * Creates a new entailment relation based on the Lingeling
	 * executable given as a parameter. 
	 * @param binaryLocation the path to the executable.
	 */
	public LingelingEntailment(String binaryLocation){
		this.binaryLocation = binaryLocation;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.SatSolverEntailment#isConsistent(java.util.Collection)
	 */
	@Override
	public boolean isConsistent(Collection<PropositionalFormula> formulas) {
		try {			
			List<Proposition> props = new ArrayList<Proposition>();
			for(PropositionalFormula f: formulas){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());			
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolverEntailment.createTmpDimacsFile(formulas,props);
			String output = Exec.invokeExecutable(this.binaryLocation + " -q " + f.getAbsolutePath());
			// delete file
			f.delete();
			return (output.indexOf("UNSATISFIABLE") == -1);			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);						
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.EntailmentRelation#getWitness(java.util.Collection)
	 */
	@Override
	public Interpretation getWitness(Collection<PropositionalFormula> formulas) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for(PropositionalFormula f: formulas){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());	
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolverEntailment.createTmpDimacsFile(formulas,props);
			String output = Exec.invokeExecutable(this.binaryLocation + " -q --witness " + f.getAbsolutePath());
			if(output.indexOf("UNSATISFIABLE") != -1)
				return null;
			// parse the model	
			String model = output.substring(output.indexOf("v")+1,output.indexOf("0")).trim();
			StringTokenizer tokenizer = new StringTokenizer(model, " ");
			PossibleWorld w = new PossibleWorld();
			while(tokenizer.hasMoreTokens()){
				String s = tokenizer.nextToken().trim();
				Integer i = new Integer(s);
				if(i > 0){
					w.add(props.get(i-1));
				}				
			}
			return w;			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);						
		}
	}

}
