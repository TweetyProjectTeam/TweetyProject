package net.sf.tweety.logics.pl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.tweety.BeliefBaseSampler;
import net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester;
import net.sf.tweety.logics.commons.analysis.ConsistencyWitnessProvider;
import net.sf.tweety.logics.commons.analysis.DrasticInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.EtaInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.HsInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MiInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MicInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MusEnumerator;
import net.sf.tweety.logics.commons.analysis.streams.DefaultInconsistencyListener;
import net.sf.tweety.logics.commons.analysis.streams.DefaultStreamBasedInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.streams.EvaluationInconsistencyListener;
import net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import net.sf.tweety.logics.commons.analysis.streams.StreamBasedInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.streams.WindowInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.DefaultConsistencyTester;
import net.sf.tweety.logics.pl.LingelingEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.SatSolverEntailment;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasure;
import net.sf.tweety.logics.pl.analysis.ContensionInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.analysis.HsInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.analysis.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.analysis.PlWindowInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;
import net.sf.tweety.logics.pl.util.CnfSampler;
import net.sf.tweety.math.opt.solver.LpSolve;
import net.sf.tweety.streams.DefaultFormulaStream;

public class StreamInconsistencyEvaluation {
	
	public static final int 												SIGNATURE_SIZE				= 15;
	public static final double 												CNF_RATIO					= 1/4d;
	public static final int 												NUMBER_OF_ITERATIONS 		= 100;
	public static final int 												SIZE_OF_KNOWLEDGEBASES 		= 1000;
	public static final double 												STANDARD_SMOOTHING_FACTOR  	= 0.75;
	public static final int 												STANDARD_WINDOW_SIZE	 	= 20;
	public static final int													STANDARD_EVENTS				= 20000;
	public static final BeliefSetConsistencyTester<PropositionalFormula>	STANDARD_CONSISTENCY_TESTER = new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling"));///Users/mthimm/Projects/misc_bins/lingeling
	public static final ConsistencyWitnessProvider<PropositionalFormula> 	STANDARD_WITNESS_PROVIDER	= new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling"));///Users/mthimm/Projects/misc_bins/lingeling
	public static final MusEnumerator<PropositionalFormula> 				STANDARD_MUS_ENUMERATOR     = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");///Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py
	public static final String 												RESULT_PATH					= "/Users/mthimm/Desktop";///Users/mthimm/Desktop
	public static final String												BELIEFSET_PATH				= "/Users/mthimm/Desktop/beliefsets.txt";///Users/mthimm/Desktop/beliefsets.txt
	public static final String												TMP_FILE_FOLDER				= "/Users/mthimm/Desktop/tmp";
	public static final long												TIMEOUT						= 120; //2 minutes
	
	public static void main(String[] args) throws InterruptedException{
		LpSolve.binary = "/home/shared/strinc/lp_solve";
		LpSolve.tmpFolder = new File(TMP_FILE_FOLDER);
		SatSolverEntailment.tempFolder = new File(TMP_FILE_FOLDER);
		PropositionalSignature signature = new PropositionalSignature(SIGNATURE_SIZE);
		BeliefBaseSampler<PlBeliefSet> sampler = new CnfSampler(signature,CNF_RATIO);
		// -----------------------------------------
		// the inconsistency measures to be compared
		// -----------------------------------------
		Collection<StreamBasedInconsistencyMeasure<PropositionalFormula>> measures = new HashSet<StreamBasedInconsistencyMeasure<PropositionalFormula>>(); 
		// Window-based drastic measure (window size = infinity, note that for this no smoothing is necessary)
		Map<String,Object> config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new DrasticInconsistencyMeasure<PropositionalFormula>(STANDARD_CONSISTENCY_TESTER));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-drastic-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> drastic_naive_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		drastic_naive_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-drastic-1.txt",STANDARD_EVENTS));
		drastic_naive_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(drastic_naive_1);
		// Window-based drastic measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new DrasticInconsistencyMeasure<PropositionalFormula>(STANDARD_CONSISTENCY_TESTER));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-drastic-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> drastic_naive_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		drastic_naive_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-drastic-2.txt",STANDARD_EVENTS));
		drastic_naive_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(drastic_naive_2);
		// Window-based contension measure (window size = infinity, note that for this no smoothing is necessary)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new ContensionInconsistencyMeasure(STANDARD_CONSISTENCY_TESTER));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-contension-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> contension_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		contension_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-contension-1.txt",STANDARD_EVENTS));
		contension_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(contension_1);
		// Window-based contension measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new ContensionInconsistencyMeasure(STANDARD_CONSISTENCY_TESTER));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-contension-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> contension_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		contension_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-contension-2.txt",STANDARD_EVENTS));
		contension_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(contension_2);
		// Stream contension measure (population size = 10)
		config = new HashMap<String,Object>();
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 10);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
		StreamBasedInconsistencyMeasure<PropositionalFormula> cont_stream_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
		cont_stream_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-cont-1.txt",STANDARD_EVENTS));
		cont_stream_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(cont_stream_1);
		// Stream contension measure (population size = 100)
		config = new HashMap<String,Object>();
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 100);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
		config.put(ContensionInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
		StreamBasedInconsistencyMeasure<PropositionalFormula> cont_stream_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(ContensionInconsistencyMeasurementProcess.class,config);
		cont_stream_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-cont-2.txt",STANDARD_EVENTS));
		cont_stream_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(cont_stream_2);
		// Window-based MI measure (window size = infinity, note that for this no smoothing is necessary)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MiInconsistencyMeasure<PropositionalFormula>(STANDARD_MUS_ENUMERATOR));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mi-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> mi_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mi_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mi-1.txt",STANDARD_EVENTS));
		mi_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mi_1);
		// Window-based MI measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MiInconsistencyMeasure<PropositionalFormula>(STANDARD_MUS_ENUMERATOR));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mi-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> mi_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mi_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mi-2.txt",STANDARD_EVENTS));
		mi_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mi_2);
		// Window-based MI^C measure (window size = infinity, note that for this no smoothing is necessary)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MicInconsistencyMeasure<PropositionalFormula>(STANDARD_MUS_ENUMERATOR));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mic-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> mic_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mic_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mic-1.txt",STANDARD_EVENTS));
		mic_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mic_1);
		// Window-based MI^C measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new MicInconsistencyMeasure<PropositionalFormula>(STANDARD_MUS_ENUMERATOR));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-mic-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> mic_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		mic_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-mic-2.txt",STANDARD_EVENTS));
		mic_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(mic_2);
		// Window-based Eta measure (window size = infinity, note that for this no smoothing is necessary)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new EtaInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(signature)));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-eta-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> eta_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		eta_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-eta-1.txt",STANDARD_EVENTS));
		eta_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(eta_1);
		// Window-based Eta measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new EtaInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(signature)));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);		
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-eta-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> eta_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		eta_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-eta-2.txt",STANDARD_EVENTS));
		eta_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(eta_2);
		// Window-based hs measure (window size = infinity, note that for this no smoothing is necessary)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new HsInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(signature)));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, -1d);		
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-naive-1");
		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_naive_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		hs_naive_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-hs-1.txt",STANDARD_EVENTS));
		hs_naive_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(hs_naive_1);
		// Window-based hs measure (window size = standard window size)
		config = new HashMap<String,Object>();
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE, new HsInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator(signature)));
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE, STANDARD_WINDOW_SIZE);
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(WindowInconsistencyMeasurementProcess.CONFIG_NAME, "-naive-2");
		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_naive_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(PlWindowInconsistencyMeasurementProcess.class,config);
		hs_naive_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/naive-hs-2.txt",STANDARD_EVENTS));
		hs_naive_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(hs_naive_2);		
		// Stream hs measure (population size = 10)
		config = new HashMap<String,Object>();
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 10);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_stream_1 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
		hs_stream_1.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-hs-1.txt",STANDARD_EVENTS));
		hs_stream_1.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(hs_stream_1);
		// Stream hs measure (population size = 100)
		config = new HashMap<String,Object>();
		config.put(InconsistencyMeasurementProcess.CONFIG_TIMEOUT, TIMEOUT);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, STANDARD_WITNESS_PROVIDER);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 100);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, signature);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR, STANDARD_SMOOTHING_FACTOR);
		StreamBasedInconsistencyMeasure<PropositionalFormula> hs_stream_2 = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
		hs_stream_2.addInconsistencyListener(new EvaluationInconsistencyListener(RESULT_PATH+"/stream-hs-2.txt",STANDARD_EVENTS));
		hs_stream_2.addInconsistencyListener(new DefaultInconsistencyListener());
		measures.add(hs_stream_2);

		// -----------------------------------------
		// iterate
		// -----------------------------------------
		for(int iteration = 0; iteration < NUMBER_OF_ITERATIONS; iteration++){
			PlBeliefSet bs = sampler.randomSample(SIZE_OF_KNOWLEDGEBASES, SIZE_OF_KNOWLEDGEBASES);
			System.out.println(bs);
			try {
				FileWriter writer = new FileWriter(new File(BELIEFSET_PATH), true);
				writer.append(bs.toString() + "\n");
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}	
			for(StreamBasedInconsistencyMeasure<PropositionalFormula> sbim: measures){
				InconsistencyMeasurementProcess<PropositionalFormula> imp = sbim.getInconsistencyMeasureProcess(new DefaultFormulaStream<PropositionalFormula>(bs,true));
				imp.start();
				Thread.sleep(2000);
				while(imp.isAlive()){
					Thread.sleep(2000);
				}
			}
		}
	}
}
