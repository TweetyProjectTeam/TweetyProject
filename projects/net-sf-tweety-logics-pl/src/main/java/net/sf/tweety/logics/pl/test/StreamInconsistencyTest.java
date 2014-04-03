package net.sf.tweety.logics.pl.test;

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.logics.commons.analysis.streams.DefaultInconsistencyListener;
import net.sf.tweety.logics.commons.analysis.streams.DefaultStreamBasedInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.DefaultConsistencyTester;
import net.sf.tweety.logics.pl.LingelingEntailment;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.HsInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

import net.sf.tweety.logics.pl.util.HsSampler;
import net.sf.tweety.streams.DefaultFormulaStream;

public class StreamInconsistencyTest {

	public static void main(String[] args) throws InterruptedException{
		PropositionalSignature sig = new PropositionalSignature(15);		
		HsSampler sampler = new HsSampler(sig,5);//
		
		PlBeliefSet bs = sampler.randomSample(10000, 10000);
		
		DefaultFormulaStream<PropositionalFormula> stream = new DefaultFormulaStream<PropositionalFormula>(bs, true);
		
		Map<String,Object> config = new HashMap<String,Object>();
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_SIGNATURE, sig);
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_WITNESSPROVIDER, new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling")));
		config.put(HsInconsistencyMeasurementProcess.CONFIG_KEY_NUMBEROFPOPULATIONS, 5);
				
		DefaultStreamBasedInconsistencyMeasure<PropositionalFormula> inc = new DefaultStreamBasedInconsistencyMeasure<PropositionalFormula>(HsInconsistencyMeasurementProcess.class,config);
		
		inc.addInconsistencyListener(new DefaultInconsistencyListener());
		
		InconsistencyMeasurementProcess<PropositionalFormula> proc = inc.getInconsistencyMeasureProcess(stream);
		
		proc.start();
	}
}
