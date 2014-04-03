package net.sf.tweety.logics.pl.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator;
import net.sf.tweety.logics.pl.SatSolverEntailment;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.util.Exec;
import net.sf.tweety.util.Pair;

/**
 * Implements a MUs enumerator based on MARCO (http://sun.iwu.edu/~mliffito/marco/). Tested
 * with version 1.0.
 * 
 * @author Matthias Thimm
 *
 */
public class MarcoMusEnumerator extends AbstractMusEnumerator<PropositionalFormula> {

	/** The MARCO executable. */
	private String pathToMarco;
	
	/**
	 * Creates a new MUs enumerator.
	 * @param pathToMarco the path to the MARCO executable.
	 */
	public MarcoMusEnumerator(String pathToMarco){
		this.pathToMarco = pathToMarco;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PropositionalFormula>> minimalInconsistentSubsets(Collection<PropositionalFormula> formulas) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for(PropositionalFormula f: formulas)
				props.addAll(f.getAtoms());			
			// create temporary file in Dimacs CNF format.
			Pair<File,List<PropositionalFormula>> p = SatSolverEntailment.createTmpDimacsFile(formulas);
			String output = Exec.invokeExecutable(this.pathToMarco + " -v " + p.getFirst().getAbsolutePath());
			// parse output
			Collection<Collection<PropositionalFormula>> result = new HashSet<Collection<PropositionalFormula>>();
			StringTokenizer tokenizer = new StringTokenizer(output, "\n");
			Integer idx;
			while(tokenizer.hasMoreTokens()){
				String line = tokenizer.nextToken().trim();
				if(line.startsWith("U")){
					StringTokenizer tokenizer2 = new StringTokenizer(line.substring(2), " ");
					Collection<PropositionalFormula> mus = new HashSet<PropositionalFormula>();
					while(tokenizer2.hasMoreTokens()){
						idx = new Integer(tokenizer2.nextToken().trim()) - 1;
						mus.add(p.getSecond().get(idx));
					}
					result.add(mus);
				}
			}
			return result;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}				
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator#maximalConsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PropositionalFormula>> maximalConsistentSubsets(Collection<PropositionalFormula> formulas) {
		// TODO Auto-generated method stub
		return null;
	}

}
