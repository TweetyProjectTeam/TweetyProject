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
package org.tweetyproject.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Provides an interface to the open-wbo MaxSAT solver,
 * see https://github.com/sat-group/open-wbo.  
 * Tested with open-wbo 2.1
 * 
 * @author Matthias Thimm
 */
public class OpenWboSolver extends DimacsMaxSatSolver {

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
	public Interpretation<PlBeliefSet, PlFormula> getWitness(Collection<PlFormula> hardConstraints, Map<PlFormula,Integer> softConstraints, Map<Proposition,Integer> prop_index, Map<Integer,Proposition> prop_inverted_index) {
		try {
			// create temporary file in Dimacs WCNF format.
			File f = DimacsMaxSatSolver.createTmpDimacsWcnfFile(hardConstraints,softConstraints,prop_index);
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
					w.add(prop_inverted_index.get(i));
				}
			}
			return w;			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);						
		}
	}

	@Override
	public boolean isInstalled() {
		try {
			NativeShell.invokeExecutable(this.binaryLocation);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	@Override
	public boolean isSatisfiable(Collection<PlFormula> formulas, Map<Proposition, Integer> prop_index) {
		try {
			// create temporary file in Dimacs WCNF format.
			File f = DimacsMaxSatSolver.createTmpDimacsWcnfFile(formulas,new HashMap<PlFormula,Integer>(),prop_index);
			String output = NativeShell.invokeExecutable(this.binaryLocation + " " + f.getAbsolutePath());
			f.delete();
			if(output.indexOf("UNSATISFIABLE") != -1)
				return false;
			return true;					
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);						
		}
	}
}
