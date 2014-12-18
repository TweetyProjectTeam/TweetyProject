package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * @author Bastian Wolf
 */
public abstract class TptpFofBinaryAssociativeFormula extends TptpFofBinaryFormula {

    private TptpFofFormula left;

    private TptpFofFormula right;

    private TptpFofAssociative assoc;

    public TptpFofBinaryAssociativeFormula() {
    }

    public TptpFofBinaryAssociativeFormula(TptpFofFormula left, TptpFofFormula right, TptpFofAssociative assoc) {
        this.left = left;
        this.right = right;
        this.assoc = assoc;
    }

    public TptpFofFormula getLeft() {
        return left;
    }

    public void setLeft(TptpFofFormula left) {
        this.left = left;
    }

    public TptpFofFormula getRight() {
        return right;
    }

    public void setRight(TptpFofFormula right) {
        this.right = right;
    }

    public TptpFofAssociative getAssoc() {
        return assoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TptpFofBinaryAssociativeFormula that = (TptpFofBinaryAssociativeFormula) o;

        if (assoc != null ? !assoc.equals(that.assoc) : that.assoc != null) return false;
        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        if (right != null ? !right.equals(that.right) : that.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        result = 31 * result + (assoc != null ? assoc.hashCode() : 0);
        return result;
    }

    public String toString(){
        return TptpFofLogicalSymbols.PARENTHESES_LEFT() + left.toString() +  " " + assoc.toString() + " " + right.toString() + TptpFofLogicalSymbols.PARENTHESES_RIGHT();
    }
    
	@Override
	public boolean isAssociative() {
		
		return true;
	}

	@Override
	public boolean isNonAssociative() {
		
		return false;
	}
    
}
