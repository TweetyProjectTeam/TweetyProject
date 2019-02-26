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
package net.sf.tweety.logics.pl.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.BeliefSetIterator;
import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;

/**
 * Generates all syntactic variations of knowledge bases 
 * @author Matthias Thimm
 *
 */
public class SyntacticEnumeratingIterator implements BeliefSetIterator<PlFormula,PlBeliefSet> {

	/** the maximal length of each formula (each proposition, negation, conjunction, and
	  	disjunction counts one). */
	private int formulaLength;
	/** the path to a folder where intermediate results (all formulas up to the required length)
	 	are stored*/
	private File pathToTmp;
	 /** if "true" then the temporary folder is cleaned after each sample (it is recommended to set this
	     to "false" to speed up sampling)*/
	private boolean deleteTmp;
	/** Whether the formulas have already been generated. */
	private boolean formulasGenerated = false;
	
	/**Used for iterating over all possible kbs */
	private IncreasingSubsetIterator<File> it = null;
	
	/**
	 * The used signature.
	 */
	private PlSignature signature;
	
	/** Creates a new sampler.
	 * @param signature the signature for formulas of the generated belief set.
	 * @param formulaLength the maximal length of each formula (each proposition, negation, conjunction, and
	 * 	disjunction counts one).
	 * @param pathToTmp the path to a folder where intermediate results (all formulas up to the required length)
	 * 	are stored
	 * @param deleteTmp if "true" then the temporary folder is cleaned after each sample (it is recommended to set this
	 * to "false" to speed up sampling)
	 */
	public SyntacticEnumeratingIterator(PlSignature signature, int formulaLength, File pathToTmp, boolean deleteTmp) {
		this.signature = signature;
		this.deleteTmp = deleteTmp;
		this.pathToTmp = pathToTmp;
		this.formulaLength = formulaLength;
	}

	/**
	 * Generates all formulas of the given length and stores them
	 * in "pathToTemp/length/"
	 * @param length the length of the formula
	 * @throws IOException 
	 */
	private void generateFormulasOfLength(int length) throws IOException{
		PlParser parser = new PlParser();
		File path = new File(this.pathToTmp.getAbsolutePath() + "/" + length);		
		path.mkdirs();
		File file;		
		int idx = 1;
		// end of recursion: length is one
		if(length == 1){					
			for(Proposition p: this.signature){
				file = new File(path.getAbsolutePath() + "/f_" + length + "_" + idx++ + ".plogic");
				file.createNewFile();
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.print(p);						
				writer.close();					
			}
		}else{			
			// generate first all formulas
			this.generateFormulasOfLength(length-1);
			// use formulas of length-1 and add a negation
			for (File fileEntry: new File(this.pathToTmp.getAbsolutePath() + "/" + (length-1)).listFiles()) {
				PlFormula f = (PlFormula) parser.parseFormulaFromFile(fileEntry.getAbsolutePath());
				f = new Negation(f);
				file = new File(path.getAbsolutePath() + "/f_" + length + "_" + idx++ + ".plogic");
				file.createNewFile();
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.print(f);						
				writer.close();
		    }			
			// use conjunction and disjunction only if length >2
			if(length>2){
				int left, right;
				for(left = 1; left < length-1; left++){
					right = length-left-1;
					// left = length of left formula
					// right = length of right formula 
					for (File leftEntry: new File(this.pathToTmp.getAbsolutePath() + "/" + left).listFiles()) {
						for (File rightEntry: new File(this.pathToTmp.getAbsolutePath() + "/" + right).listFiles()) {
							PlFormula leftF = (PlFormula) parser.parseFormulaFromFile(leftEntry.getAbsolutePath());
							PlFormula rightF = (PlFormula) parser.parseFormulaFromFile(rightEntry.getAbsolutePath());
							PlFormula conj = new Conjunction(leftF, rightF);
							PlFormula disj = new Disjunction(leftF, rightF);
							file = new File(path.getAbsolutePath() + "/f_" + length + "_" + idx++ + ".plogic");
							file.createNewFile();
							PrintWriter writer = new PrintWriter(file, "UTF-8");
							writer.print(conj);						
							writer.close();
							file = new File(path.getAbsolutePath() + "/f_" + length + "_" + idx++ + ".plogic");
							file.createNewFile();
							writer = new PrintWriter(file, "UTF-8");
							writer.print(disj);						
							writer.close();
						}
				    }	
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.it == null || this.it.hasNext();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetIterator#next()
	 */
	@Override
	public PlBeliefSet next() {
		try {
			if(!this.formulasGenerated){
				this.generateFormulasOfLength(this.formulaLength);
				this.formulasGenerated = true;
			}
			// check if we already initialized the subset iterator
			if(this.it == null){
				Set<File> allFormulas = new HashSet<File>();
				for (File file: this.pathToTmp.listFiles()) 
					if(file.isDirectory())
						for(File file2: file.listFiles())
							allFormulas.add(file2);
				this.it = new IncreasingSubsetIterator<File>(allFormulas);				
			}
			// get next valid set of formulas
			Set<File> files = this.it.next();
			// construct belief base
			PlBeliefSet bs = new PlBeliefSet();
			PlParser parser = new PlParser();
			for(File f: files)
				bs.add((PlFormula)parser.parseFormulaFromFile(f.getAbsolutePath()));			
			// delete files if required
			if(this.deleteTmp){
				this.formulasGenerated  = false;
				for (File file: this.pathToTmp.listFiles()) {
			        for(File file2: file.listFiles())
			        	file2.delete();			        
			        file.delete();
			    }
			}
			return bs;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
}
