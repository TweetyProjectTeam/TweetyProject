package net.sf.tweety.machinelearning;

/**
 * A category within the space of observations. 
 * @author Matthias Thimm
 */
public interface Category {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode();

	/* (non-Javadoc)
 	 * @see java.lang.Object#equals(java.lang.Object)
 	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * Returns a double representation of this category.
	 * @return a double representation of this category.
	 */
	public double asDouble();
}
