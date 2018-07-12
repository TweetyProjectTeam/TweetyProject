/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.machinelearning.svm;

import libsvm.svm;
import libsvm.svm_parameter;
import net.sf.tweety.machinelearning.DefaultObservation;
import net.sf.tweety.machinelearning.DoubleCategory;
import net.sf.tweety.machinelearning.ParameterSet;
import net.sf.tweety.machinelearning.Trainer;
import net.sf.tweety.machinelearning.TrainingParameter;
import net.sf.tweety.machinelearning.TrainingSet;

/**
 * Trains a standard multi-class RBF support vector machine.
 * @author Matthias Thimm
 *
 */
public class MultiClassRbfTrainer implements Trainer<DefaultObservation,DoubleCategory> {

	/**The c parameter for learning */
	public static final TrainingParameter C_PARAMETER = new TrainingParameter("C", 2E3, 2E3, 2E-5, 2E15);
	/** The gamma parameter for learning */
	public static final TrainingParameter GAMMA_PARAMETER = new TrainingParameter("gamma", 2E-8, 2E-8, 2E-15, 2E3);
	
	/**The c parameter for learning */
	private TrainingParameter c; 
	/** The gamma parameter for learning */
	private TrainingParameter gamma;
	
	/**
	 * Initializes the trainer with the default parameters.
	 */
	public MultiClassRbfTrainer(){
		this.c = MultiClassRbfTrainer.C_PARAMETER.instantiateWithDefaultValue();
		this.gamma = MultiClassRbfTrainer.GAMMA_PARAMETER.instantiateWithDefaultValue();
	}
	
	/**
	 * Initializes the trainer with the given parameters.
	 * @param c the c parameter for learning
	 * @param gamma the gamma parameter for learning
	 */
	public MultiClassRbfTrainer(double c, double gamma){
		this.c = MultiClassRbfTrainer.C_PARAMETER.instantiate(c);
		this.gamma = MultiClassRbfTrainer.GAMMA_PARAMETER.instantiate(gamma);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#train(net.sf.tweety.machinelearning.TrainingSet)
	 */
	@Override
	public SupportVectorMachine train(TrainingSet<DefaultObservation, DoubleCategory> trainingSet) {
		ParameterSet set = new ParameterSet();
		set.add(this.c);
		set.add(this.gamma);
		return this.train(trainingSet, set);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#train(net.sf.tweety.machinelearning.TrainingSet, net.sf.tweety.machinelearning.ParameterSet)
	 */
	@Override
	public SupportVectorMachine train(TrainingSet<DefaultObservation, DoubleCategory> trainingSet, ParameterSet params) {
		if(!params.containsParameter(C_PARAMETER) || !params.containsParameter(GAMMA_PARAMETER))
			throw new IllegalArgumentException("Parameters missing.");
		svm_parameter param = new svm_parameter();
		//TODO the following properties should be parameterized as well
		// Type of SVM
		param.svm_type = svm_parameter.C_SVC;
		// Kernel type (leave it at RBF for now)
		param.kernel_type = svm_parameter.RBF;
		// stopping criteria
		param.eps = 0.001;
		// cache size of kernel
		param.cache_size = 256;
		// do not set penalties for specific classes
		param.nr_weight = 0;

		// Given parameters
		// gamma parameter of RBF kernel
		param.gamma = params.getParameter(GAMMA_PARAMETER).getValue();
		// C parameter of RBF kernel
		param.C = params.getParameter(C_PARAMETER).getValue();
				
		return new SupportVectorMachine(svm.svm_train(trainingSet.toLibsvmProblem(), param));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#getParameterSet()
	 */
	@Override
	public ParameterSet getParameterSet() {
		ParameterSet set = new ParameterSet();
		set.add(this.c);
		set.add(this.gamma);
		return set;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Trainer#setParameterSet(net.sf.tweety.machinelearning.ParameterSet)
	 */
	@Override
	public boolean setParameterSet(ParameterSet params) {
		if(!params.containsParameter(C_PARAMETER) || !params.containsParameter(GAMMA_PARAMETER))
			throw new IllegalArgumentException("Parameters missing.");
		this.c = params.getParameter(C_PARAMETER);
		this.gamma = params.getParameter(GAMMA_PARAMETER);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "RBF<" + this.c.getValue() + "," + this.gamma.getValue() + ">";
	}
}
