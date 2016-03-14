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

import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.TptpWriter;

/**
 * Invokes Eprover (http://eprover.org) and returns its results.
 * @author Bastian Wolf, Nils Geilen
 *
 */
public class EProver extends FolTheoremProver {

	/**
	 *  String representation of the E binary path,
	 *  directory, where the temporary files are stored
	 */
	private String binaryLocation;
	
	/**
	 * Shell to run eprover
	 */
	private Shell bash;
	
	/**
	 * Constructs a new instance pointing to a specific eprover 
	 * @param binaryLocation of the eprover executable on the hard drive
	 * @param bash shell to run commands
	 */
	public EProver(String binaryLocation, Shell bash) {
		super();
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific eprover 
	 * @param binaryLocation of the eprover executable on the hard drive
	 */
	public EProver(String binaryLocation) {
		this(binaryLocation,new NativeShell());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.prover.FolTheoremProver#query(net.sf.tweety.logics.fol.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public boolean query(FolBeliefSet kb, FolFormula query) {
		TptpWriter printer = new TptpWriter();
		try{
			File file  = File.createTempFile("tmp", ".txt");
			PrintWriter writer = new PrintWriter(file);
			printer.printBase(writer, kb);
			writer.write(printer.makeQuery("query", query));
			writer.close();
			
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
	 * returns the path of the provers binary
	 * @return the path of the provers binary
	 */
	public String getBinaryLocation() {
		return binaryLocation;
	}


	/**
	 * Change path of the binary
	 * @param binaryLocation the new path of the E binary
	 */
	public void setBinaryLocation(String binaryLocation) {
		this.binaryLocation = binaryLocation;
	}
}



