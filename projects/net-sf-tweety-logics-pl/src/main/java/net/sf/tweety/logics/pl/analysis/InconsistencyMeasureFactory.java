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

import java.io.File;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.logics.commons.analysis.DrasticInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MiInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MicInconsistencyMeasure;
import net.sf.tweety.logics.commons.analysis.MusEnumerator;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.sat.SatSolver;
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
	
	/** The default MUS enumerator */
	private static MusEnumerator<PropositionalFormula> defaultMusEnumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");
	
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
				return new DrasticInconsistencyMeasure<PropositionalFormula>(SatSolver.getDefaultSolver());
			case CONTENSION:
				return new ContensionInconsistencyMeasure();
			case MI:
				return new MiInconsistencyMeasure<PropositionalFormula>(InconsistencyMeasureFactory.defaultMusEnumerator);
			case MIC:
				return new MicInconsistencyMeasure<PropositionalFormula>(InconsistencyMeasureFactory.defaultMusEnumerator);
			default:
				throw new RuntimeException("No measure found for " + im.toString());
		}
	}
}
