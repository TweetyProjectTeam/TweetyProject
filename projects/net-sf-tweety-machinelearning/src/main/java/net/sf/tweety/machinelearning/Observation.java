package net.sf.tweety.machinelearning;

import libsvm.svm_node;

/**
 * An observation is some data point which can be classified.
 * 
 * @author Matthias Thimm
 */
public interface Observation {

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
	 * Returns the svm_node (the data model of libsvm) representation
	 * of this observation.
	 * @return an array of svm_node.
	 */
	public svm_node[] toSvmNode();
}
