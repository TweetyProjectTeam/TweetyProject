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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * This class offers a generic wrapper for command line based sat
 * solvers.
 * 
 * Tested with the following solvers:
 * <ul>
 * <li> CaDiCal 1.3.1 {@link (http://fmv.jku.at/cadical/)} </li>
 * <li> Kissat 1.0.3 {@link (http://fmv.jku.at/kissat/)} </li>
 * <li> Lingeling bcp {@link (http://fmv.jku.at/lingeling/)} </li>
 * <ul>
 * 
 * @author Anna Gessler
 *
 */
public class CmdLineSatSolver extends SatSolver {
	/**
	 * The binary location of the solver.
	 */
	protected String binaryLocation = null;
	
	/**
	 * Command line options for the solver.
	 * @TODO
	 */
	private String options = "";
	
	public CmdLineSatSolver(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> formulas) {
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for(PlFormula f: formulas){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());	
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolver.createTmpDimacsFile(formulas,props);
			String output = NativeShell.invokeExecutable(this.binaryLocation + " -q " + f.getAbsolutePath());
			f.delete();
			if(output.indexOf("UNSATISFIABLE") != -1)
				return null;
			// parse the model	
			String model = output.substring(output.indexOf("v")+1,output.indexOf(" 0")).trim();
			System.out.println(model);
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
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		} 
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas) {
		try {			
			List<Proposition> props = new ArrayList<Proposition>();
			for(PlFormula f: formulas){
				props.removeAll(f.getAtoms());
				props.addAll(f.getAtoms());			
			}
			// create temporary file in Dimacs CNF format.
			File f = SatSolver.createTmpDimacsFile(formulas,props);
			String opts = " " + options;
			String output = NativeShell.invokeExecutable(this.binaryLocation + opts + f.getAbsolutePath());
			// delete file
			f.delete();
			return (output.indexOf("UNSATISFIABLE") == -1);			
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		} 
	}
}
