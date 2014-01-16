package net.sf.tweety.machinelearning.test;

import java.io.File;
import java.io.IOException;

import net.sf.tweety.machinelearning.CrossValidator;
import net.sf.tweety.machinelearning.DefaultObservation;
import net.sf.tweety.machinelearning.DoubleCategory;
import net.sf.tweety.machinelearning.GridSearchParameterLearner;
import net.sf.tweety.machinelearning.TrainingSet;
import net.sf.tweety.machinelearning.svm.MultiClassRbfTrainer;
import net.sf.tweety.machinelearning.svm.SupportVectorMachine;

public class SVMTest {

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
