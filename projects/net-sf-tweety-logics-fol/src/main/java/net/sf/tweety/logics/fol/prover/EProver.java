/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.fol.prover;

import java.io.File;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.FolWriter;
import net.sf.tweety.logics.fol.writer.TptpWriter;

/**
 * Invokes Eprover (http://eprover.org) and returns its results.
 * 
 * @author Bastian Wolf, Nils Geilen, Matthias Thimm
 *
 */
public class EProver extends FolTheoremProver {

	/**
	 *  String representation of the EProver binary path. 
	 *  Temporary files are stored in this directory.
	 */
	private String binaryLocation;
	
	/**
	 * Additional arguments for the call to the EProver binary 
	 * (Default value is "--auto-schedule" which seems to be working
	 * best in general)
	 * */
	private String additionalArguments = "--auto-schedule";
	
	/**
	 * Shell to run EProver
	 */
	private Shell bash;
	
	/**
	 * Constructs a new instance pointing to a specific EProver.
	 * @param binaryLocation location of the EProver executable on the hard drive
	 * @param bash 		     shell to run commands
	 */
	public EProver(String binaryLocation, Shell bash) {
		super(new FolBeliefSet());
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific EProver.
	 * @param kb 			 a knowledge base
	 * @param binaryLocation location of the EProver executable on the hard drive
	 * @param bash 			 shell to run commands
	 */
	public EProver(BeliefBase kb, String binaryLocation, Shell bash) {
		super(kb);
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific EProver. 
	 * @param binaryLocation location of the Eprover executable on the hard drive
	 */
	public EProver(String binaryLocation) {
		this(binaryLocation,Shell.getNativeShell());
	}
	
	/**
	 * Sets the additional arguments given to the call of the
	 * EProver binary (Default value is "--auto-schedule").
	 * @param s some string
	 */
	public void setAdditionalArguments(String s){
		this.additionalArguments = s;
	}

	/**
	 * Returns the additional arguments given to the call of the
	 * EProver binary (Default value is "--auto-schedule").
	 */
	public String getAdditionalArguments(){
		return this.additionalArguments;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.prover.FolTheoremProver#query(net.sf.tweety.logics.fol.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public Answer query(Formula query) {
		FolBeliefSet kb = (FolBeliefSet) this.getKnowledgeBase();
		Answer answer = new Answer(kb,query);
		try{
			File file  = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();
			FolWriter printer = new TptpWriter(new PrintWriter(file));
			printer.printBase(kb);
			printer.printQuery((FolFormula) query);
			printer.close();
			
			//System.out.println(Files.readAllLines(file.toPath()));
			
			String cmd = binaryLocation +" " + this.additionalArguments + " --tptp3-format " + file.getAbsolutePath().replaceAll("\\\\", "/");
			//System.out.println(cmd);
			String output = bash.run(cmd);
			//String output = Exec.invokeExecutable(cmd);
			//System.out.print(output);
			if(Pattern.compile("# Proof found!").matcher(output).find()) { 
				answer.setAnswer(true);
				return answer;
			}
			if(Pattern.compile("# No proof found!").matcher(output).find()){ 
				answer.setAnswer(false);
				return answer;
			}
			throw new RuntimeException("Failed to invoke eprover: Eprover returned no result which can be interpreted.");
		}catch(Exception e){
			e.printStackTrace();
			answer.setAnswer(false);
			return answer;
		}	
	}
	
/*
 * (non-Javadoc)
 * @see net.sf.tweety.logics.fol.prover.FolTheoremProver#equivalent(net.sf.tweety.logics.fol.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula, net.sf.tweety.logics.fol.syntax.FolFormula)
 */
	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b) {
		try{
			File file  = File.createTempFile("tmp", ".txt");
			TptpWriter printer = new TptpWriter(new PrintWriter(file));
			printer.printBase(kb);
			printer.printEquivalence(a,b);
			printer.close();
			
			//System.out.println(Files.readAllLines(file.toPath()));
			
			String cmd = binaryLocation + " --tptp3-format " + file.getAbsolutePath().replaceAll("\\\\", "/");
			//System.out.println(cmd);
			String output = bash.run(cmd);
			//String output = Exec.invokeExecutable(cmd);
			//System.out.print(output);
			if(Pattern.compile("# Proof found!").matcher(output).find())
				return true;
			if(Pattern.compile("# No proof found!").matcher(output).find())
				return false;
			throw new RuntimeException("Failed to invoke eprover: Eprover returned no result which can be interpreted.");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}	
	}

	/**
	 * Returns the path of the EProver binary.
	 * @return the path of the EProver binary
	 */
	public String getBinaryLocation() {
		return binaryLocation;
	}

	/**
	 * Changes the path of the EProver binary.
	 * @param binaryLocation the new path of the EProver binary
	 */
	public void setBinaryLocation(String binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
}



