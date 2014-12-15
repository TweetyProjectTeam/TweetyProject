package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * Variables are used in quantified formulas
 * Each Variable must begin with an upper case letter followed by any amount (>=0) of lower case letters 
 * author Bastian Wolf
 */
public class TptpFofVariable {

    /**
     * The variable identifier must begin with an upper case character followed by optional lower case characters
     */
    private String identifier;

    /**
     * construct new variable with given (valid) identifier
     * @param identifier the identifier given
     */
    public TptpFofVariable(String identifier) {
    	if(identifier.matches("[A-Z][a-z]*")){
        	this.identifier = identifier;
    	}
        else if (identifier.length()>1){
        	this.identifier = identifier.substring(0, 0).toUpperCase() + identifier.substring(1, identifier.length()-1);
        }
        	else {
        	this.identifier = identifier.toUpperCase();	
        	}
    }

    /**
     * returns identifier
     * @return identifier
     */
	public String getIdentifier() {
		return identifier;
	}


	/**
	 * Simply just returns the identifier.
	 */
	@Override
	public String toString() {
		return identifier;
	}
}
