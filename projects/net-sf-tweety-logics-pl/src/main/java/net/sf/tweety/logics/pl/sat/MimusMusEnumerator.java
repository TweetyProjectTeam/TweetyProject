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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

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
	 * @see net.sf.tweety.logics.pl.sat.PlMusEnumerator#minimalInconsistentSubsets(java.util.Collection)
	 */
	@Override
	public Collection<Collection<PropositionalFormula>> minimalInconsistentSubsets(Collection<PropositionalFormula> formulas) {
		try {
			File file = File.createTempFile("tweety-mimus", ".mim");
			file.deleteOnExit();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for(PropositionalFormula f: formulas){
				// fortunately, the syntax of mimus is the same as for Tweety
				writer.write(f.toString() + "\n");
			}
			writer.close();
			String output = NativeShell.invokeExecutable(this.pathToMimus + " -i " + file.getAbsolutePath());			
			if(output.trim().equals(""))
				return new HashSet<Collection<PropositionalFormula>>();
			// each line is a minimal inconsistent subset
			String[] lines = output.split("\n");
			String[] elems;
			Collection<Collection<PropositionalFormula>> result = new HashSet<Collection<PropositionalFormula>>();
			Collection<PropositionalFormula> mus;
			PlParser parser = new PlParser();
			for(String line: lines){
				mus = new HashSet<PropositionalFormula>();
				elems = line.split(",");
				for(String elem: elems)
					mus.add((PropositionalFormula) parser.parseFormula(elem.trim()));
				result.add(mus);
			}
			
			return result;
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
