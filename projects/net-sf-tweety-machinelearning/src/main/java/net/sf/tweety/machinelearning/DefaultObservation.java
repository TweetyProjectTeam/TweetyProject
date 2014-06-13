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
package net.sf.tweety.machinelearning;

import java.util.Vector;

import libsvm.svm_node;

/**
 * A default observation is a vector of double values.
 * 
 * @author Matthias Thimm
 */
public class DefaultObservation extends Vector<Double> implements Observation {

	/** For serialization */
	private static final long serialVersionUID = -6600763202428596342L;

	/* (non-Javadoc)
	 * @see net.sf.tweety.machinelearning.Observation#toSvmNode()
	 */
	public svm_node[] toSvmNode(){
		svm_node[] obs = new svm_node[this.size()];
		int idx = 0;
		for(Double d: this){
			obs[idx] = new svm_node();
			obs[idx].index = idx+1;
			obs[idx].value = d;
			idx++;
		}
		return obs;
	}	
}
