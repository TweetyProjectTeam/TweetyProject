package net.sf.tweety.logics.fol.prover;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.regex.Pattern;

import net.sf.tweety.commons.util.NativeShell;
import net.sf.tweety.commons.util.Shell;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.writer.Prover9Writer;
import net.sf.tweety.logics.fol.writer.TptpWriter;

public class Prover9 extends FolTheoremProver {

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
	public Prover9(String binaryLocation, Shell bash) {
		super();
		this.binaryLocation = binaryLocation;
		this.bash = bash;
	}
	
	/**
	 * Constructs a new instance pointing to a specific eprover 
	 * @param binaryLocation of the eprover executable on the hard drive
	 */
	public Prover9(String binaryLocation) {
		this(binaryLocation,new NativeShell());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.fol.prover.FolTheoremProver#query(net.sf.tweety.logics.fol.FolBeliefSet, net.sf.tweety.logics.fol.syntax.FolFormula)
	 */
	@Override
	public boolean query(FolBeliefSet kb, FolFormula query) {
		try{
			File file  = File.createTempFile("tmp", ".txt");
			Prover9Writer printer = new Prover9Writer(new PrintWriter(file));
			printer.printBase(kb);
			printer.printQuery(query);
			printer.close();
			
			System.out.println(Files.readAllLines(file.toPath()));
			
			String cmd = binaryLocation + " -f " + file.getAbsolutePath();
			System.out.println(cmd);
			String output = bash.run(cmd);
			//String output = Exec.invokeExecutable(cmd);
			//System.out.print(output);
			if(Pattern.compile("THEOREM PROVED").matcher(output).find())
				return true;
			if(Pattern.compile("SEARCH FAILED").matcher(output).find())
				return false;
			throw new RuntimeException("Failed to invoke eprover: Eprover returned no result which can be interpreted.");
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}	
	}
	
	

	@Override
	public boolean equivalent(FolBeliefSet kb, FolFormula a, FolFormula b) {
		try{
			File file  = File.createTempFile("tmp", ".txt");
			TptpWriter printer = new TptpWriter(new PrintWriter(file));
			printer.printBase(kb);
			printer.printEquivalence( a,b);
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
