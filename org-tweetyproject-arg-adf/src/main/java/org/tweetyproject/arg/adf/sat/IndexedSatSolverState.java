package org.tweetyproject.arg.adf.sat;
/**
 * 
 * @author  Matthias Thimm
 *
 */
public interface IndexedSatSolverState extends AutoCloseable {
	/**
	 * 
	 * @return satisfiable
	 */
	boolean satisfiable();
	/**
	 * 
	 * @param assumptions assumptions
	 * @return satisfiabl
	 */
	boolean satisfiable(int[] assumptions);

	/**
	 * 
	 * @param literals literals
	 * @return the truth values of the given literals, 0 if false or 1 if true
	 */
	int[] witness(int[] literals);
	/**
	 * 
	 * @param literals literals
	 * @param assumptions assumptions
	 * @return witness
	 */
	int[] witness(int[] literals, int[] assumptions);
/**
 * 
 * @param clause clause
 */
	void add(int[] clause);

	@Override
	void close();
	
}
