package org.tweetyproject.arg.adf.sat;

public interface IndexedSatSolverState extends AutoCloseable {
	
	boolean satisfiable();
	
	boolean satisfiable(int[] assumptions);

	/**
	 * 
	 * @param literals
	 * @return the truth values of the given literals, 0 if false or 1 if true
	 */
	int[] witness(int[] literals);
	
	int[] witness(int[] literals, int[] assumptions);

	void add(int[] clause);

	@Override
	void close();
	
}
