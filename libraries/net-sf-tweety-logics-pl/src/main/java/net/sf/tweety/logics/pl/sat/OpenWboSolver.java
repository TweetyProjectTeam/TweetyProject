package net.sf.tweety.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Provides an interface to the open-wbo MaxSAT solver,
 * see https://github.com/sat-group/open-wbo.  
 * Tested with open-wbo 2.1
 * 
 * @author Matthias Thimm
 */
public class OpenWboSolver extends MaxSatSolver {

	/** The binary location of open-wbo. */
	private String binaryLocation;
	
	/**
	 * Creates a new solver based on the open-wbo
	 * executable given as a parameter. 
	 * @param binaryLocation the path to the executable.
	 */
	public OpenWboSolver(String binaryLocation){
		this.binaryLocation = binaryLocation;
	}
	
	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula, Integer> softConstraints) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for(PlFormula f: hardConstraints){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());	
			}
			for(PlFormula f: softConstraints.keySet()){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());	
			}
			// create temporary file in Dimacs WCNF format.
			File f = MaxSatSolver.createTmpDimacsWcnfFile(hardConstraints,softConstraints,props);
			String output = NativeShell.invokeExecutable(this.binaryLocation + " " + f.getAbsolutePath());
			f.delete();
			if(output.indexOf("UNSATISFIABLE") != -1)
				return null;
			// parse the model	
			String model = output.substring(output.indexOf("\nv")+1).trim();
			model = model.replaceAll("v", "");
			StringTokenizer tokenizer = new StringTokenizer(model, " ");
			PossibleWorld w = new PossibleWorld();
			while(tokenizer.hasMoreTokens()){
				String s = tokenizer.nextToken().trim();				
				Integer i = Integer.parseInt(s);
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
