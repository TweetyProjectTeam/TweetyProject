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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * 
 * @author Bastian Wolf, Nils Geilen
 *
 */
public class EProver extends FolTheoremProver {

	/**
	 *  String representation of the E binary path
	 */
	private String binaryLocation, workspace;
	
	Shell bash = new CygwinShell("C:/cygwin64/bin/bash.exe");
	
	
	
	public EProver(String binaryLocation, String workspace) {
		super();
		this.binaryLocation = binaryLocation;
		this.workspace = workspace;
	}

	@Override
	public boolean query(FolBeliefSet kb, FolFormula query) {
		TPTPPrinter printer = new TPTPPrinter();
		try{
			PrintWriter writer = new PrintWriter(workspace + "problem.tptp");
			printer.printBase(writer, kb);
			writer.write(printer.makeQuery("query", query));
			writer.close();
			bash.run(binaryLocation + " --tptp3-format " + workspace + "problem.tptp > " + workspace + "answer.txt");
			BufferedReader reader = new BufferedReader(new FileReader(workspace + "answer.txt"));
			String line;
			while((line = reader.readLine()) != null)
				if(line.matches("# Proof found!")){
					reader.close();
					return true;
				}else if(line.matches("# No proof found!")){
					reader.close();
					return false;
				}	
		}catch(Exception e){
			e.printStackTrace();
		}	
		return false;
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

interface Shell {
	public void run(String cmd);
}

class CygwinShell implements Shell{
	
	String binaryLocation;

	public CygwinShell(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public void run(String cmd) {
		Runtime runtime = Runtime.getRuntime();
		
		try {
			Process proc = runtime.exec(new String[] {binaryLocation , "-c",cmd },new String[] {});
			proc.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (br.ready())
				System.out.println(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}


