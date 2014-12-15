package net.sf.tweety.logics.fol.syntax.tptp.fof;


/**
 * 
 * @author Bastian Wolf
 */
public class TptpFofOrFormula {

	/**
	 * the left part of the formula
	 */
    private TptpFofFormula first;

    /**
     * the right part of the formula
     */
    private TptpFofFormula second;

    /**
     * the binary associative, set to "|"
     */
    private TptpFofAssociative assoc = new TptpFofAssociative(TptpFofLogicalSymbols.DISJUNCTION());

    /**
     * Constructor given left and right part of the formula
     * @param first the left part of the formula
     * @param second the right part of the formula
     */
    public TptpFofOrFormula(TptpFofFormula first, TptpFofFormula second) {
        this.first = first;
        this.second = second;
    }

    /*
     * Getter
     */
    public TptpFofFormula getFirst() {
        return first;
    }

    public TptpFofFormula getSecond() {
        return second;
    }

    public TptpFofAssociative getAssoc() {
        return assoc;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofOrFormula that = (TptpFofOrFormula) o;

        if (assoc != null ? !assoc.equals(that.assoc) : that.assoc != null) return false;
        if (first != null ? !first.equals(that.first) : that.first != null) return false;
        if (second != null ? !second.equals(that.second) : that.second != null) return false;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (assoc != null ? assoc.hashCode() : 0);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.first.toString() + this.assoc + this.second.toString();
    }
}
