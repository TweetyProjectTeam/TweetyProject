/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.prob.examples;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.PartialProbabilityAssignment;
import net.sf.tweety.arg.prob.analysis.PAInconsistencyMeasure;
import net.sf.tweety.arg.prob.dynamics.*;
import net.sf.tweety.arg.prob.semantics.*;
import net.sf.tweety.math.func.EntropyFunction;
import net.sf.tweety.math.norm.*;
import net.sf.tweety.math.probability.Probability;

/**
 * Example code for the PAInconsistencyMeasure.
 * @author Matthias Thimm
 *
 */
public class PAInconsistencyMeasureExample {
	public static void main(String[] args){
		DungTheory theory = new DungTheory();
		Argument a = new Argument("A");
		Argument b = new Argument("B");
		Argument c = new Argument("C");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,c));
		theory.add(new Attack(a,c));
		
		PartialProbabilityAssignment ppa = new PartialProbabilityAssignment();
		ppa.put(a, new Probability(0.9));
				
		PAInconsistencyMeasure mes = new PAInconsistencyMeasure(new PNorm(2), theory, new SemiOptimisticPASemantics());
		
		System.out.println(mes.inconsistencyMeasure(ppa));
		
		ChangeOperator op = new PAUpdateOperator(new CoherentPASemantics(), new EntropyNorm<Extension>(), new EntropyFunction());
		
		System.out.println(op.change(ppa, theory));
	}
}
