package net.sf.tweety.beliefdynamics.operators;

import net.sf.tweety.beliefdynamics.kernels.*;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements a simple kernel base contraction for propositional logic with 
 * an incision function that randomly selects its incisions.
 * <br>
 * NOTE: results of this operator are not deterministic and may not be reproduced (however, each
 * 	 result is a valid kernel contraction)
 * 
 * @author Matthias Thimm
 */
public class RandomKernelContractionOperator extends KernelContractionOperator<PropositionalFormula> {

	/**
	 * Creates a new contraction operator.
	 */
	public RandomKernelContractionOperator() {
		super(new RandomIncisionFunction<PropositionalFormula>(), new ClassicalEntailment());
	}

}
