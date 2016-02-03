/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
