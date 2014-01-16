package net.sf.tweety.math.norm;

/**
 * A norm for vector spaces.
 * @author Matthias Thimm
 */
public interface Norm<T>{

	/**
	 * Returns the norm of the given object
	 * @param obj some object
	 * @return the norm of the object
	 */
	public double norm(T obj);
	
	/**
	 * The distance between the two object, i.e.
	 * the norm of the difference vector.
	 * @param obj1 some object
	 * @param obj2 some object
	 * @return the distance between the two objects
	 */
	public double distance(T obj1, T obj2);
}
