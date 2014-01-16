package net.sf.tweety.machinelearning;

import java.util.Vector;

import libsvm.svm_node;

/**
 * A default observation is a vector of double values.
 * 
 * @author Matthias Thimm
 */
public class DefaultObservation extends Vector<Double> implements Observation {

	/** For serialization */
	private static final long serialVersionUID = -6600763202428596342L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Observation#toSvmNode()
	 */
	public svm_node[] toSvmNode(){
		svm_node[] obs = new svm_node[this.size()];
		int idx = 0;
		for(Double d: this){
			obs[idx] = new svm_node();
			obs[idx].index = idx+1;
			obs[idx].value = d;
			idx++;
		}
		return obs;
	}	
}
