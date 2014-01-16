package net.sf.tweety.logics.cl.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.tweety.logics.cl.RuleBasedCReasoner.Rule;
import net.sf.tweety.logics.cl.kappa.KappaValue;
import net.sf.tweety.logics.cl.semantics.ConditionalStructure;

/**
 * A simple adapter class that wraps the straightforward methods
 * of the Rule interface, such that the developer can spare the work
 * to implement the setter methods.
 * 
 * @author Tim janus
 */
public abstract class RuleAdapter implements Rule {
	/** the kappas the rule works on */
	protected List<KappaValue> kappas;
	
	/** the conditional structure the rule works on */
	protected ConditionalStructure conditionalStructure;
	
	@Override
	public void setKappas(Collection<KappaValue> kappas) {
		this.kappas = new ArrayList<KappaValue>(kappas);
	}

	@Override
	public void setConditonalStructure(ConditionalStructure cs) {
		this.conditionalStructure = cs;
	}
}
