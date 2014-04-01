package net.sf.tweety.logics.pl.analysis;

import net.sf.tweety.logics.commons.analysis.streams.WindowInconsistencyMeasurementProcess;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * A window inconsistency measurement process for propositional logic 
 * (this class needs to be there as no generics are allowed when instantiating 
 * a DefaultStreamBasedInconsistencyMeasure. 
 * 
 * @author Matthias Thimm
 */
public class PlWindowInconsistencyMeasurementProcess extends WindowInconsistencyMeasurementProcess<PropositionalFormula, PlBeliefSet>{
}
