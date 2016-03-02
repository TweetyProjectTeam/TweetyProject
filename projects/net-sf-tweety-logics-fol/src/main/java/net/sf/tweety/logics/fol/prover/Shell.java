/**
 * 
 */
package net.sf.tweety.logics.fol.prover;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.tweety.commons.util.Exec;

/**
 * @author Nils Geilen
 * Provides several ways to run unix commands on different OSes.
 */
public abstract class Shell {
	private static Shell nat = new NativeShell();
	
	public static Shell getNativeShell(){
		return nat;
	}
	
	public static Shell getCygwinShell(String binary){
		return new CygwinShell(binary);
	}
	
	public abstract String run(String cmd) throws InterruptedException, IOException;
}

class NativeShell extends Shell {

	@Override
	public String run(String cmd) throws InterruptedException, IOException {
		 return Exec.invokeExecutable(cmd);
	}
	
}

class CygwinShell extends Shell{
	
	String binaryLocation;

	public CygwinShell(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public String run(String cmd)throws InterruptedException, IOException {
		Runtime runtime = Runtime.getRuntime();			
		Process proc = runtime.exec(new String[] {binaryLocation , "-c",cmd },new String[] {});
		proc.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String result = "";
		while (br.ready())
			result += br.readLine()+"\n";
		return result;
	}
	
}


