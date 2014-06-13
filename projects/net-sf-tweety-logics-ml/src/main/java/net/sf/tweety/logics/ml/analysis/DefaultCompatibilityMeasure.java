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
package net.sf.tweety.logics.ml.analysis;

import java.util.List;

import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.MarkovLogicNetwork;

/**
 * This class represents the default compatibility measure that uses
 * a coherence measure.
 * 
 * @author Matthias Thimm
 */
public class DefaultCompatibilityMeasure implements CompatibilityMeasure{

	/** The coherence measure used for computing compatibility. */
	private AbstractCoherenceMeasure coherenceMeasure;
	
	/** Creates a new compatibility measure.
	 * @param coherenceMeasure the coherence measure used for computing compatibility.
	 */
	public DefaultCompatibilityMeasure(AbstractCoherenceMeasure coherenceMeasure){
		this.coherenceMeasure = coherenceMeasure;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.analysis.CompatibilityMeasure#compatibility(java.util.List, net.sf.tweety.Reasoner, java.util.List)
	 */
	@Override
	public double compatibility(List<MarkovLogicNetwork> mlns, Reasoner reasoner, List<FolSignature> signatures) {
		if(mlns.size() != signatures.size())
			throw new IllegalArgumentException("For each MLN there has to be exactly one signature");
		double result = 0;
		MarkovLogicNetwork mergedMLN = new MarkovLogicNetwork();
		FolSignature mergedSig = new FolSignature();
		for(int i = 0; i < mlns.size(); i++){
			result -=  this.coherenceMeasure.coherence(mlns.get(i), reasoner, signatures.get(i));
			mergedMLN.addAll(mlns.get(i));
			mergedSig.addAll(signatures.get(i).getConstants());
			mergedSig.addAll(signatures.get(i).getPredicates());
			mergedSig.addAll(signatures.get(i).getFunctors());
			mergedSig.addAll(signatures.get(i).getSorts());
		}
		result = 1 + this.coherenceMeasure.coherence(mergedMLN, reasoner, mergedSig) - (1d/mlns.size()) * result;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "comp[" + this.coherenceMeasure.toString() + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((coherenceMeasure == null) ? 0 : coherenceMeasure.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultCompatibilityMeasure other = (DefaultCompatibilityMeasure) obj;
		if (coherenceMeasure == null) {
			if (other.coherenceMeasure != null)
				return false;
		} else if (!coherenceMeasure.equals(other.coherenceMeasure))
			return false;
		return true;
	}

}
