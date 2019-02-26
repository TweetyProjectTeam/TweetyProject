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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Implements a MUs enumerator based on MARCO (http://sun.iwu.edu/~mliffito/marco/). Tested
 * with version 1.0.
 * 
 * @author Matthias Thimm
 *
 */
public class MarcoMusEnumerator extends PlMusEnumerator {

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
	public Collection<Collection<PlFormula>> minimalInconsistentSubsets(Collection<PlFormula> formulas) {
		if(formulas.isEmpty())
			return new HashSet<Collection<PlFormula>>();  
		try {
			List<Proposition> props = new ArrayList<Proposition>();
			for(PlFormula f: formulas)
				props.addAll(f.getAtoms());			
			// create temporary file in Dimacs CNF format.
			Pair<File,List<PlFormula>> p = SatSolver.createTmpDimacsFile(formulas);
			// we only read a maximum number of 1000 lines from MARCO (TODO: this should be parameterized) as we bias on MUSes and the rest
			// of the lines contains the description of the MCSes
			String output = NativeShell.invokeExecutable(this.pathToMarco + " -v -b MUSes " + p.getFirst().getAbsolutePath(), 1000);
			// delete file
			p.getFirst().delete();
			// parse output
			Collection<Collection<PlFormula>> preResult = new HashSet<Collection<PlFormula>>();
			StringTokenizer tokenizer = new StringTokenizer(output, "\n");
			Integer idx;
			while(tokenizer.hasMoreTokens()){
				String line = tokenizer.nextToken().trim();
				if(line.startsWith("U")){
					StringTokenizer tokenizer2 = new StringTokenizer(line.substring(2), " ");
					Collection<PlFormula> mus = new HashSet<PlFormula>();
					while(tokenizer2.hasMoreTokens()){
						idx = new Integer(tokenizer2.nextToken().trim()) - 1;
						mus.add(p.getSecond().get(idx));
					}					
					preResult.add(mus);
				}
			}
			// it may have happened that non-minimal sets have been added, 
			// so we have to check that
			Collection<Collection<PlFormula>> result = new HashSet<Collection<PlFormula>>();
			boolean subTest;
			for(Collection<PlFormula> c: preResult){
				subTest = true;
				for(Collection<PlFormula> c2: preResult){
					if(c != c2 && c.containsAll(c2)){
						subTest = false;
						break;
					}
				}
				if(subTest)
					result.add(c);
			}			 
			return result;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}				
	}
}
