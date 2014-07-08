package net.sf.tweety.logics.fol.prover;

/**
 * 
 * @author Bastian Wolf
 *
 */
public abstract class FolTheoremProver {

	/**
	 * Empty default prover
	 */
	public static FolTheoremProver defaultProver = null;
	
	/**
	 * Set default prover with given
	 * @param prover
	 */
	public static void setDefaultProver(FolTheoremProver prover){
		FolTheoremProver.defaultProver = prover;
	}
	
	/**
	 * 
	 * @return
	 */
	public static FolTheoremProver getDefaultProver(){
		if(FolTheoremProver.defaultProver != null){
			return FolTheoremProver.defaultProver;
		} else{
			// TODO return standard prover?
			System.err.println("No default E Prover set! Returning null...");
			return null;
		}
	}
	
}
