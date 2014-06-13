/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
