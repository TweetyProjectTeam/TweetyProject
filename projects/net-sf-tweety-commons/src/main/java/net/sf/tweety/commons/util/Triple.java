package net.sf.tweety.commons.util;

/**
 * This class implements a simple triple of elements.
 *
 * @author Matthias Thimm
 *
 * @param <E> the type of the first element
 * @param <F> the type of the second element
 * @param <G> the type of the third element
 */
public class Triple<E,F,G> {
	/**
	 * The first element of this triple
	 */
	E obj1;

	/**
	 * The second element of this triple
	 */
	F obj2;

	/**
	 * The third element of this triple
	 */
	G obj3;

	/**
	 * Initializes the elements of this triple with the given parameters
	 * @param obj1 the first element of this triple
	 * @param obj2 the second element of this triple
	 * @param obj3 the third element of this triple
	 */
	public Triple(E obj1, F obj2, G obj3){
		this.obj1 = obj1;
		this.obj2 = obj2;
		this.obj3 = obj3;
	}
	
	/**
	 * Initializes an empty triple.
	 */
	public Triple(){		
	}

	// Misc Methods

	/**
	 * returns the first element of this triple
	 * @return the first element of this triple
	 */
	public E getFirst() {
		return obj1;
	}

	/**
	 * sets the first element of this triple
	 * @param obj1 an object of type E
	 */
	public void setFirst(E obj1) {
		this.obj1 = obj1;
	}

	/**
	 * returns the second element of this triple
	 * @return the second element of this triple
	 */
	public F getSecond() {
		return obj2;
	}

	/**
	 * sets the second element of this triple
	 * @param obj2 an object of type F
	 */
	public void setSecond(F obj2) {
		this.obj2 = obj2;
	}

	/**
	 * returns the third element of this triple
	 * @return the third element of this triple
	 */
	public G getThird() {
		return obj3;
	}

	/**
	 * sets the third element of this triple
	 * @param obj3 an object of type G
	 */
	public void setThird(G obj3) {
		this.obj3 = obj3;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj1 == null) ? 0 : obj1.hashCode());
		result = prime * result + ((obj2 == null) ? 0 : obj2.hashCode());
		result = prime * result + ((obj3 == null) ? 0 : obj3.hashCode());
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
		Triple<?,?,?> other = (Triple<?,?,?>) obj;
		if (obj1 == null) {
			if (other.obj1 != null)
				return false;
		} else if (!obj1.equals(other.obj1))
			return false;
		if (obj2 == null) {
			if (other.obj2 != null)
				return false;
		} else if (!obj2.equals(other.obj2))
			return false;
		if (obj3 == null) {
			if (other.obj3 != null)
				return false;
		} else if (!obj3.equals(other.obj3))
			return false;
		return true;
	}

}