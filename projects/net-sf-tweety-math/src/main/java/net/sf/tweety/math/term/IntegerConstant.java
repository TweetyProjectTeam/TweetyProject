package net.sf.tweety.math.term;

/**
 * This class encapsulates an integer as a term.
 * @author Matthias Thimm
 */
public class IntegerConstant extends Constant{
	
	/**
	 * the actual integer.
	 */	
	private int i;
	
	/**
	 * Creates a new Integer.
	 * @param i an int.
	 */
	public IntegerConstant(int i){
		this.i = i;
	}
	
	/**
	 * Get the value of this integer.
	 * @return the value of this integer.
	 */
	public int getValue(){
		return this.i;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#isInteger()
	 */
	@Override
	public boolean isInteger(){
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.term.Term#toString()
	 */
	@Override
	public String toString(){
		return String.valueOf(this.i);
	}
}
