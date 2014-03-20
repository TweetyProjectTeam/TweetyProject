package net.sf.tweety.logics.pl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.util.Exec;

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
			// create temporary file in Dimacs CNF format.
			File f = SatSolverEntailment.createTmpDimacsFile(formulas);
			String output = Exec.invokeExecutable(this.binaryLocation + " -q " + f.getAbsolutePath());
			return (output.indexOf("UNSATISFIABLE") == -1);			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);						
		}
	}

}
