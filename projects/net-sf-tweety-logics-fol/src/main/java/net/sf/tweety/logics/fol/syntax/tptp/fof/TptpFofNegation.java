package net.sf.tweety.logics.fol.syntax.tptp.fof;


/**
 * Negation for a given formula
 * @author Bastian Wolf
 */
public class TptpFofNegation extends TptpFofFormula {
	/**
	 * The actual formula 
	 */
    private TptpFofFormula formula;

    /**
     * Static negation symbol
     */
    private static String negation = TptpFofLogicalSymbols.TPTP_NEGATION();

    /*
     * (non-Javadoc)
     * @see net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula#toString()
     */
    @Override
    public String toString() {
        return negation + this.formula.toString();
    }
}
