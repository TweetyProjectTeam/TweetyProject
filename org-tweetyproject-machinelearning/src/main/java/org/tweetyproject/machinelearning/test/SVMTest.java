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
package org.tweetyproject.machinelearning.test;

import java.io.File;
import java.io.IOException;

import org.tweetyproject.machinelearning.CrossValidator;
import org.tweetyproject.machinelearning.DefaultObservation;
import org.tweetyproject.machinelearning.DoubleCategory;
import org.tweetyproject.machinelearning.GridSearchParameterLearner;
import org.tweetyproject.machinelearning.TrainingSet;
import org.tweetyproject.machinelearning.svm.MultiClassRbfTrainer;
import org.tweetyproject.machinelearning.svm.SupportVectorMachine;

/**
 * tests SVM
 * 
 * @author Matthias Thimm
 *
 */
public class SVMTest {

	/**
	 * 
	 * @param args command line argumnts
	 * @throws IOException and IOException
	 */
	public static void main(String[] args) throws IOException{
		TrainingSet<DefaultObservation,DoubleCategory> trainingSet = TrainingSet.loadLibsvmTrainingFile(new File("/Users/mthimm/Desktop/train.1.txt"));
		MultiClassRbfTrainer trainer = new MultiClassRbfTrainer();
		GridSearchParameterLearner<DefaultObservation,DoubleCategory> parameterLearner = new GridSearchParameterLearner<DefaultObservation,DoubleCategory>(trainer, new CrossValidator<DefaultObservation,DoubleCategory>(4), 2, 3); 
		SupportVectorMachine svm = (SupportVectorMachine) parameterLearner.train(trainingSet); 
	
		System.out.println();
		
		DefaultObservation testPoint = new DefaultObservation();
		testPoint.add(1.184799);
		testPoint.add(50d);
		testPoint.add(0.216);
		testPoint.add(57d);		
		 
		System.out.println(svm.classify(testPoint));
		
	}
}
