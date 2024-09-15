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
package org.tweetyproject.logics.pl.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.commons.BeliefSetSampler;
import org.tweetyproject.commons.streams.DefaultFormulaStream;
import org.tweetyproject.logics.commons.analysis.MiInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.MicInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.streams.DefaultInconsistencyListener;
import org.tweetyproject.logics.commons.analysis.streams.DefaultStreamBasedInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.streams.EvaluationInconsistencyListener;
import org.tweetyproject.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import org.tweetyproject.logics.commons.analysis.streams.StreamBasedInconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.streams.WindowInconsistencyMeasurementProcess;
//import org.tweetyproject.logics.pl.analysis.ContensionInconsistencyMeasurementProcess;
//import org.tweetyproject.logics.pl.analysis.HsInconsistencyMeasurementProcess;
import org.tweetyproject.logics.pl.analysis.PlWindowInconsistencyMeasurementProcess;
import org.tweetyproject.logics.pl.sat.CmdLineSatSolver;
import org.tweetyproject.logics.pl.sat.DimacsSatSolver;
import org.tweetyproject.logics.pl.sat.MarcoMusEnumerator;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.CnfSampler;
//import org.tweetyproject.logics.pl.util.ContensionSampler;
import org.tweetyproject.logics.pl.util.MiSampler;
import org.tweetyproject.math.func.BinaryFunction;
import org.tweetyproject.math.func.MaxFunction;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.math.opt.solver.LpSolve;

/**
 * Illustrates stream-based inconsistency measurement.
 * @author Matthias Thimm
 * 
 */
public class StreamInconsistencyEvaluationExample2 {
	
	/**SIGNATURE SIZE*/
	public static final int 												SIGNATURE_SIZE				= 60;//30;
	/**CNF RATIO*/
	public static final double 												CNF_RATIO					= 1/8d;
	/**NUMBER OF ITERATIONS*/
	public static final int 												NUMBER_OF_ITERATIONS 		= 100;
	/**SIZE OF KNOWLEDGEBASES*/
	public static final int 												SIZE_OF_KNOWLEDGEBASES 		= 5000;
	/**STANDARD AGGFUNCTION*/
	public static final BinaryFunction<Double,Double,Double>				STANDARD_AGGFUNCTION		= new MaxFunction();
	/**STANDARD EVENTS*/
	public static final int													STANDARD_EVENTS				= 10000;//40000;
	/**RESULT PATH*/
	public static final String 												RESULT_PATH					= "/Users/mthimm/Desktop";//"/Users/mthimm/Desktop";
	/**BELIEFSET PATH*/
	public static final String												BELIEFSET_PATH				= "/home/mthimm/strinc/beliefsets.txt";//"/Users/mthimm/Desktop/beliefsets.txt";
	/**TMP FILE FOLDER*/
	public static final String												TMP_FILE_FOLDER				= "/Users/mthimm/Desktop/tmp";//"/Users/mthimm/Desktop/tmp";
	/**TIMEOUT*/
	public static final long												TIMEOUT						= -1;//120; //2 minutes
	

	/**
	 * main
	 * @param args arguments
	 * @throws InterruptedException InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException{
		LpSolve.setBinary("/home/mthimm/strinc/lpsolve/lp_solve");
		Solver.setDefaultLinearSolver(new LpSolve());
		DimacsSatSolver.setTempFolder(new File(TMP_FILE_FOLDER));
		SatSolver.setDefaultSolver(new CmdLineSatSolver("/home/mthimm/strinc/lingeling/lingeling"));
		PlMusEnumerator.setDefaultEnumerator(new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"));
		PlSignature signature = new PlSignature(SIGNATURE_SIZE);
		// -----------------------------------------
		// the inconsistency measures to be compared
		// -----------------------------------------
		Collection<StreamBasedInconsistencyMeasure<PlFormula>> measures = new HashSet<StreamBasedInconsistencyMeasure<PlFormula>>(); 
//		// Stream contension measure (population size = 10)
//		Map<String,Object>  config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 10);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> cont_stream_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
//		cont_stream_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-cont-1.txt",STANDARD_EVENTS));
//		cont_stream_1.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(cont_stream_1);
//		// Stream contension measure (population size = 100)
//		config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 100);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> cont_stream_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
//		cont_stream_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-cont-2.txt",STANDARD_EVENTS));
//		cont_stream_2.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(cont_stream_2);
//		// Stream contension measure (population size = 500)
//		config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 500);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> cont_stream_3 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
//		cont_stream_3.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-cont-3.txt",STANDARD_EVENTS));
//		cont_stream_3.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(cont_stream_3);
//		// Window-based MI measure (window size = 500)
		Map<String,Object> config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MiInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 500);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mi-1");
		StreamBasedInconsistencyMeasure<PlFormula> mi_1 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mi_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mi-1.txt",STANDARD_EVENTS));
		mi_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mi_1);
		// Window-based MI measure (window size = 1000)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MiInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 1000);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mi-2");
		StreamBasedInconsistencyMeasure<PlFormula> mi_2 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mi_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mi-2.txt",STANDARD_EVENTS));
		mi_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mi_2);
		// Window-based MI measure (window size = 2000)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MiInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 2000);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mi-3");
		StreamBasedInconsistencyMeasure<PlFormula> mi_3 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mi_3.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mi-3.txt",STANDARD_EVENTS));
		mi_3.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mi_3);
		// Window-based MI^C measure (window size = 500)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MicInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 500);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mic-1");
		StreamBasedInconsistencyMeasure<PlFormula> mic_1 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mic_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mic-1.txt",STANDARD_EVENTS));
		mic_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mic_1);
		// Window-based MI^C measure (window size = 1000)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MicInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 1000);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mic-2");
		StreamBasedInconsistencyMeasure<PlFormula> mic_2 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mic_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mic-2.txt",STANDARD_EVENTS));
		mic_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mic_2);
		// Window-based MI^C measure (window size = 2000)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MicInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator()));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION, STANDARD_AGGFUNCTION);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, 2000);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mic-3");
		StreamBasedInconsistencyMeasure<PlFormula> mic_3 = new DefaultStreamBasedInconsistencyMeasure<PlFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mic_3.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mic-3.txt",STANDARD_EVENTS));
		mic_3.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mic_3);
//		// Stream hs measure (population size = 10)
//		config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 10);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_stream_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
//		hs_stream_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-hs-1.txt",STANDARD_EVENTS));
//		hs_stream_1.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(hs_stream_1);
//		// Stream hs measure (population size = 100)
//		config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 100);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_stream_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
//		hs_stream_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-hs-2.txt",STANDARD_EVENTS));
//		hs_stream_2.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(hs_stream_2);
//		// Stream hs measure (population size = 500)
//		config = new HashMap<String,Object>();
//		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 500);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
//		config.put(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
//		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_stream_3 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
//		hs_stream_3.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-hs-3.txt",STANDARD_EVENTS));
//		hs_stream_3.addInconsistencyListener(new DefaultInconsistencyListener());
//		measures.add(hs_stream_3);
		
		// -----------------------------------------
		// iterate
		// -----------------------------------------
		int incvalue = 21;
		double ival;
		long millis;
		MiInconsistencyMeasure<PlFormula> testmeasure = new MiInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
		BeliefSetSampler<PlFormula,PlBeliefSet> sampler = new CnfSampler(signature,CNF_RATIO);
		for(int iteration = 0; iteration < NUMBER_OF_ITERATIONS; iteration++){
			if(iteration % 10 == 0){
				incvalue--;
				if(incvalue == 0) System.exit(0);
				sampler = new MiSampler(signature,incvalue,SIZE_OF_KNOWLEDGEBASES, SIZE_OF_KNOWLEDGEBASES);
			}
			PlBeliefSet bs = sampler.next();
			System.out.println(incvalue + " - " + bs);
			// try out standard measure
			millis = System.currentTimeMillis();
			ival = testmeasure.inconsistencyMeasure(bs);
			millis = System.currentTimeMillis() - millis;
			System.out.println(incvalue + "\tX" + millis + ";" + ival + "X\t" + bs);System.exit(0);
			try {
				FileWriter writer = new FileWriter(new File(BELIEFSET_PATH), true);
				//writer.append(incvalue + "\tX" + millis + ";" + ival + "X\t" + bs.toString() + "\n");
				writer.append(incvalue + " - " + bs.toString() + "\n");
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}	
			for(StreamBasedInconsistencyMeasure<PlFormula> sbim: measures){
				InconsistencyMeasurementProcess<PlFormula> imp = sbim.getInconsistencyMeasureProcess(new DefaultFormulaStream<PlFormula>(bs,true));
				imp.start();
				Thread.sleep(2000);
				while(imp.isAlive()){
					Thread.sleep(2000);
				}
			}
		}
	}

    /** Default Constructor */
    public StreamInconsistencyEvaluationExample2(){}
}
