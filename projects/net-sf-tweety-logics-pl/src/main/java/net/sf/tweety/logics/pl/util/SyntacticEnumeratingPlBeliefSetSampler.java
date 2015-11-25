package net.sf.tweety.logics.pl.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.tweety.commons.BeliefBaseSampler;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * Generates all syntactic variations of knowledge bases up to a 
 * given size.
 * @author Matthias Thimm
 *
 */
public class SyntacticEnumeratingPlBeliefSetSampler extends BeliefBaseSampler<PlBeliefSet> {

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
		
	private int previousMinLength = -1;
	private int previousMaxLength = -1;
	
	/** Creates a new sampler.
	 * @param signature the signature for formulas of the generated belief set.
	 * @param formulaLength the maximal length of each formula (each proposition, negation, conjunction, and
	 * 	disjunction counts one).
	 * @param pathToTmp the path to a folder where intermediate results (all formulas up to the required length)
	 * 	are stored
	 * @param deleteTmp if "true" then the temporary folder is cleaned after each sample (it is recommended to set this
	 * to "false" to speed up sampling)
	 */
	public SyntacticEnumeratingPlBeliefSetSampler(Signature signature, int formulaLength, File pathToTmp, boolean deleteTmp) {
		super(signature);
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
	public void generateFormulasOfLength(int length) throws IOException{
		PlParser parser = new PlParser();
		File path = new File(this.pathToTmp.getAbsolutePath() + "/" + length);		
		path.mkdirs();
		File file;		
		int idx = 1;
		// end of recursion: length is one
		if(length == 1){					
			for(Proposition p: (PropositionalSignature)this.getSignature()){
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
				PropositionalFormula f = (PropositionalFormula) parser.parseFormulaFromFile(fileEntry.getAbsolutePath());
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
							PropositionalFormula leftF = (PropositionalFormula) parser.parseFormulaFromFile(leftEntry.getAbsolutePath());
							PropositionalFormula rightF = (PropositionalFormula) parser.parseFormulaFromFile(rightEntry.getAbsolutePath());
							PropositionalFormula conj = new Conjunction(leftF, rightF);
							PropositionalFormula disj = new Disjunction(leftF, rightF);
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
	 * @see net.sf.tweety.commons.BeliefBaseSampler#randomSample(int, int)
	 */
	@Override
	public PlBeliefSet randomSample(int minLength, int maxLength) {
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
				this.previousMaxLength = maxLength;
				this.previousMinLength = minLength;				
			}
			// check if minlength and maxlength is as before (in that case
			// one should re-initialize the sampler)
			if(this.previousMinLength != minLength || this.previousMaxLength != maxLength)
				throw new IllegalArgumentException("Different min or max length as for the previous call. Re-initialize sampler to get knowledge base of different sizes.");
			// get next valid set of formulas
			Set<File> files;
			do{
				files = this.it.next();
			}while(files.size() < minLength);
			if(files.size()> maxLength)
				throw new NoSuchElementException("All belief bases have been generated");
			// construct belief base
			PlBeliefSet bs = new PlBeliefSet();
			PlParser parser = new PlParser();
			for(File f: files)
				bs.add((PropositionalFormula)parser.parseFormulaFromFile(f.getAbsolutePath()));			
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
