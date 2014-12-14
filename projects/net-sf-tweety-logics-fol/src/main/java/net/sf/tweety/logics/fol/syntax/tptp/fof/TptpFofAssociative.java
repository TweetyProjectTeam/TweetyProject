package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * This class models the connection between two formulae
 * @author Bastian Wolf
 */
public class TptpFofAssociative {

	/**
	 *	the Associative
	 */
    private static String assoc;

    /**
     * Constructor using given associative symbol
     * @param sym given associative symbol
     */
    public TptpFofAssociative(String sym){
        assoc = sym;
    }

    /*
     * Getter 
     */
	public String getAssoc() {
		return assoc;
	}
    
    
}
