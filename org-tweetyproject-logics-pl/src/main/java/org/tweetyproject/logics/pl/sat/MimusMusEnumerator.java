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
package org.tweetyproject.logics.pl.sat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.commons.util.NativeShell;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Implements a MUs enumerator based on MIMUS (http://www.cs.qub.ac.uk/~kmcareavey01/mimus.html).
 * Tested with version 1.0.5.
 * 
 * @author Matthias Thimm
 *
 */
public class MimusMusEnumerator extends PlMusEnumerator  {

	/** The Mimus executable. */
	private String pathToMimus;
	
	/**
	 * Creates a new MUs enumerator.
	 * @param pathToMimus the path to the MIMUS executable.
	 */
	public MimusMusEnumerator(String pathToMimus){
		this.pathToMimus = pathToMimus;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.sat.PlMusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PlFormula>> minimalInconsistentSubsets(Collection<PlFormula> formulas) {
		try {
			File file = File.createTempFile("tweety-mimus", ".mim");
			file.deleteOnExit();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for(PlFormula f: formulas){
				// fortunately, the syntax of mimus is the same as for Tweety
				writer.write(f.toString() + "\n");
			}
			writer.close();
			String output = NativeShell.invokeExecutable(this.pathToMimus + " -i " + file.getAbsolutePath());			
			if(output.trim().equals(""))
				return new HashSet<Collection<PlFormula>>();
			// each line is a minimal inconsistent subset
			String[] lines = output.split("\n");
			String[] elems;
			Collection<Collection<PlFormula>> result = new HashSet<Collection<PlFormula>>();
			Collection<PlFormula> mus;
			PlParser parser = new PlParser();
			for(String line: lines){
				mus = new HashSet<PlFormula>();
				elems = line.split(",");
				for(String elem: elems)
					mus.add((PlFormula) parser.parseFormula(elem.trim()));
				result.add(mus);
			}
			
			return result;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
