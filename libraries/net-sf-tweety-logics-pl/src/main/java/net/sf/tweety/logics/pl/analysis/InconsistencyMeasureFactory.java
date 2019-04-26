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
package net.sf.tweety.logics.pl.analysis;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.logics.commons.analysis.*;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.math.func.FracAggrFunction;
import net.sf.tweety.math.opt.Solver;

/**
 * Main factory for retrieving inconsistency measures for propositional logic.
 * @author Matthias Thimm
 */
public abstract class InconsistencyMeasureFactory {

	/** An enumeration of all available inconsistency measures. */
	public enum Measure{
		DRASTIC ("drastic", "Drastic Inconsistency Measure"),
		MI ("mi", "MI Inconsistency Measure"),
		MIC ("mic", "MIC Inconsistency Measure"),
		ETA ("eta", "Eta Inconsistency Measure"),
		CONTENSION ("contension", "Contension Inconsistency Measure"),
		MC ("mc", "MaxCons Inconsistency Measure"),
		PR ("pr", "P Inconsistency Measure"),
		HS ("hs", "Hitting Set Inconsistency Measure"),
		DALALSUM ("dalalsum", "Dalal-Sum Inconsistency Measure"),
		DALALMAX ("dalalmax", "Dalal-Max Inconsistency Measure"),
		DALALHIT ("dalalhit", "Dalal-Hit Inconsistency Measure"),
		DF ("df", "Df Inconsistency Measure"),
		PM ("pm", "Pm Inconsistency Measure"),
		MV ("mv", "MusVar Inconsistency Measure"),
		NC ("nc", "NCons Inconsistency Measure"),
		MCSC ("mcsc", "MCSC Inconsistency Measure"),
		CC ("cc","CC Inconsistency Measure"),
		CSP ("csp","CSP Inconsistency Measure"),
		FB ("fb", "Forgetting-based Inconsistency Measure"),
		IS ("is", "Independent set-based Inconsistency Measure");
		
		public String id;
		public String label;
		
		Measure(String id, String label){
			this.id = id;
			this.label = label;			
		}
		
		public static Measure getMeasure(String id){
			for(Measure m: Measure.values())
				if(m.id.equals(id))
					return m;
			return null;
		}
	}
		
	/**
	 * Creates a new inconsistency measure of the given type with default
	 * settings.
	 * @param im some identifier of an inconsistency measure.
	 * @return the requested inconsistency measure.
	 */
	public static InconsistencyMeasure<BeliefSet<PlFormula,?>> getInconsistencyMeasure(Measure im){
		switch(im){
			case DRASTIC:
				return new DrasticInconsistencyMeasure<PlFormula>(SatSolver.getDefaultSolver());
			case CONTENSION:
				return new ContensionInconsistencyMeasure();
			case MC:
				return new MaInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case MI:
				return new MiInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case MIC:
				return new MicInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case HS:
				return new HsInconsistencyMeasure<PlBeliefSet,PlFormula>(new PossibleWorldIterator());
			case PR:
				return new PrInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case ETA:			
				return new EtaInconsistencyMeasure<PlBeliefSet,PlFormula>(new PossibleWorldIterator());
			case DALALSUM:
				return new DSumInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator());
			case DALALMAX:
				return new DMaxInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator());
			case DALALHIT:
				return new DHitInconsistencyMeasure<PossibleWorld,PlBeliefSet,PlFormula>(new DalalDistance(),new PossibleWorldIterator());
			case DF:
				return new DfInconsistencyMeasure<PlFormula>(new FracAggrFunction(),PlMusEnumerator.getDefaultEnumerator());
			case PM:
				return new PmInconsistencyMeasure();
			case MV:
				return new MusVarInconsistencyMeasure();
			case NC:
				return new NConsInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case MCSC:
				return new McscInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			case CC:
				return new CcInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator(), Solver.getDefaultIntegerLinearSolver());
			case CSP:
				return new CspInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator(), Solver.getDefaultIntegerLinearSolver());
			case FB:
				return new FbInconsistencyMeasure();
			case IS:
				return new IsInconsistencyMeasure<PlFormula>(PlMusEnumerator.getDefaultEnumerator());
			default:
				throw new RuntimeException("No measure found for " + im.toString());
		}
	}
}
