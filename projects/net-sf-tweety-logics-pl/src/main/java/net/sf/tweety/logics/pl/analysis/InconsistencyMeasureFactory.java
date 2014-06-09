package net.sf.tweety.logics.pl.analysis;

import java.io.File;

import net.sf.tweety.BeliefSet;
import net.sf.tweety.logics.commons.analysis.BeliefSetConsistencyTester;
import net.sf.tweety.logics.commons.analysis.DrasticInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MiInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MicInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MusEnumerator;
import net.sf.tweety.logics.pl.DefaultConsistencyTester;
import net.sf.tweety.logics.pl.LingelingEntailment;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Main factory for retrieving inconsistency measures for propositional logic.
 * @author Matthias Thimm
 */
public abstract class InconsistencyMeasureFactory {

	/** An enumeration of all available inconsistency measures. */
	public enum Measure{
		DRASTIC ("drastic", "Drastic Inconsistency Measure", "/inc/DrasticInconsistencyMeasure.html"),
		MI ("mi", "MI Inconsistency Measure", "/inc/MiInconsistencyMeasure.html"),
		MIC ("mic", "MIC Inconsistency Measure", "/inc/MicInconsistencyMeasure.html"),
		CONTENSION ("contension", "Contension Inconsistency Measure", "/inc/ContensionInconsistencyMeasure.html");
		
		public String id;
		public String label;
		public File description;
		
		Measure(String id, String label, String description){
			this.id = id;
			this.label = label;
			this.description = new File(getClass().getResource(description).getFile());
		}
		
		public static Measure getMeasure(String id){
			for(Measure m: Measure.values())
				if(m.id.equals(id))
					return m;
			return null;
		}
	}
	
	/** The default belief set consistency tester. */
	private static BeliefSetConsistencyTester<PropositionalFormula> defaultBeliefSetConsistencyTester = new DefaultConsistencyTester(new LingelingEntailment("/Users/mthimm/Projects/misc_bins/lingeling"));
	/** The default MUS enumerator */
	private static MusEnumerator<PropositionalFormula> defaultMusEnumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");
	
	/**
	 * Sets the default consistency tester.
	 * @param tester some consistency tester.
	 */
	public static void setDefaultBeliefSetConsistencyTester(BeliefSetConsistencyTester<PropositionalFormula> tester){
		InconsistencyMeasureFactory.defaultBeliefSetConsistencyTester = tester;
	}
	
	/**
	 * Sets the default MUS enumerator.
	 * @param enumerator some MusEnumerator
	 */
	public static void setDefaultMusEnumerator(MusEnumerator<PropositionalFormula> enumerator){
		InconsistencyMeasureFactory.defaultMusEnumerator = enumerator;
	}
	
	/**
	 * Creates a new inconsistency measure of the given type with default
	 * settings.
	 * @param im some identifier of an inconsistency measure.
	 * @return the requested inconsistency measure.
	 */
	public static InconsistencyMeasure<BeliefSet<PropositionalFormula>> getInconsistencyMeasure(Measure im){
		switch(im){
			case DRASTIC:
				return new DrasticInconsistencyMeasure<PropositionalFormula>(InconsistencyMeasureFactory.defaultBeliefSetConsistencyTester);
			case CONTENSION:
				return new ContensionInconsistencyMeasure(InconsistencyMeasureFactory.defaultBeliefSetConsistencyTester);
			case MI:
				return new MiInconsistencyMeasure<PropositionalFormula>(InconsistencyMeasureFactory.defaultMusEnumerator);
			case MIC:
				return new MicInconsistencyMeasure<PropositionalFormula>(InconsistencyMeasureFactory.defaultMusEnumerator);
			default:
				throw new RuntimeException("No measure found for " + im.toString());
		}
	}
}
