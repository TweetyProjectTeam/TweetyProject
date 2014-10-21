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
package net.sf.tweety.logics.pl.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.logics.commons.analysis.*;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.semantics.PossibleWorldIterator;
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
		ETA ("eta", "Eta Inconsistency Measure", "/inc/EtaInconsistencyMeasure.html"),
		CONTENSION ("contension", "Contension Inconsistency Measure", "/inc/ContensionInconsistencyMeasure.html"),
		PR ("pr", "P Inconsistency Measure", "/inc/PrInconsistencyMeasure.html"),
		HS ("hs", "Hitting Set Inconsistency Measure", "/inc/HittingSetInconsistencyMeasure.html"),
		DALALSUM ("dalalsum", "Dalal-Sum Inconsistency Measure", "/inc/DalalSumInconsistencyMeasure.html"),
		DALALMAX ("dalalmax", "Dalal-Max Inconsistency Measure", "/inc/DalalMaxInconsistencyMeasure.html"),
		DALALHIT ("dalalhit", "Dalal-Hit Inconsistency Measure", "/inc/DalalHitInconsistencyMeasure.html");
		
		public String id;
		public String label;
		public String description;
		
		Measure(String id, String label, String description){
			this.id = id;
			this.label = label;
			try {
				InputStream stream = InconsistencyMeasureFactory.class.getResourceAsStream(description);
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				String line;
				this.description = "";
				while((line = br.readLine()) != null) {
					this.description += line + " ";
				}
				br.close();
			} catch (IOException e) {
				this.description = "<Description not found>";
			}			
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
	public static InconsistencyMeasure<BeliefSet<PropositionalFormula>> getInconsistencyMeasure(Measure im){
		switch(im){
			case DRASTIC:
				return new DrasticInconsistencyMeasure<PropositionalFormula>(SatSolver.getDefaultSolver());
			case CONTENSION:
				return new ContensionInconsistencyMeasure();
			case MI:
				return new MiInconsistencyMeasure<PropositionalFormula>(PlMusEnumerator.getDefaultEnumerator());
			case MIC:
				return new MicInconsistencyMeasure<PropositionalFormula>(PlMusEnumerator.getDefaultEnumerator());
			case HS:
				return new HsInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator());
			case PR:
				return new PrInconsistencyMeasure<PropositionalFormula>(PlMusEnumerator.getDefaultEnumerator());
			case ETA:			
				return new EtaInconsistencyMeasure<PropositionalFormula>(new PossibleWorldIterator());
			case DALALSUM:
				return new DSumInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator());
			case DALALMAX:
				return new DMaxInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator());
			case DALALHIT:
				return new DHitInconsistencyMeasure<PossibleWorld,PropositionalFormula>(new DalalDistance(),new PossibleWorldIterator());
			default:
				throw new RuntimeException("No measure found for " + im.toString());
		}
	}
}
