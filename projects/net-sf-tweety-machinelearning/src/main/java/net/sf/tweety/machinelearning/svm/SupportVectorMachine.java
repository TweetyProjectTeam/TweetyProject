package net.sf.tweety.machinelearning.svm;

import libsvm.svm;
import libsvm.svm_model;
import net.sf.tweety.machinelearning.Category;
import net.sf.tweety.machinelearning.Classifier;
import net.sf.tweety.machinelearning.DoubleCategory;
import net.sf.tweety.machinelearning.Observation;

/**
 * Realizes a support vector machine classifier utilizing LIBSVM.
 * @author Matthias Thimm
 */
public class SupportVectorMachine implements Classifier {

	/** The actual libsvm model. */
	private svm_model model = null;
	
	/**
	 * Creates a new SVM from the given libsvm model.
	 * @param model a libsvm model.
	 */
	protected SupportVectorMachine(svm_model model){
		this.model = model;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Classifier#classify(net.sf.tweety.machinelearning.Observation)
	 */
	@Override
	public Category classify(Observation obs) {
		if(this.model != null)
			return new DoubleCategory(svm.svm_predict(this.model, obs.toSvmNode()));
		throw new RuntimeException("Support Vector Machine is not initialized");
	}

}
