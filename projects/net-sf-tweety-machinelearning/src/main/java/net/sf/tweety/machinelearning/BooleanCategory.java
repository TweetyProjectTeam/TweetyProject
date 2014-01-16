package net.sf.tweety.machinelearning;

/**
 * A category for one-class classifiers.
 * 
 * @author Matthias Thimm
 */
public class BooleanCategory implements Category {
	
	/** The value of this category. */
	private boolean cat;
	
	/**
	 * Creates a new category with the given value.
	 * @param cat some boolean
	 */
	public BooleanCategory(boolean cat){
		this.cat = cat;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cat ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BooleanCategory other = (BooleanCategory) obj;
		if (cat != other.cat)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Category#asDouble()
	 */
	@Override
	public double asDouble() {
		return this.cat ? 1d : 0d;
	}
	
}
