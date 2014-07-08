package net.sf.tweety.logics.fol.prover;

/**
 * 
 * @author Bastian Wolf
 *
 */
public class EProver extends FolTheoremProver {

	/**
	 *  String representation of the E binary path
	 */
	private String binaryLocation;
	
	/**
	 * Creates a new EProver with the E binary
	 * path given as parameter
	 * @param binaryLocation the E binary path
	 */
	public EProver(String binaryLocation){
		this.setBinaryLocation(binaryLocation);
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
