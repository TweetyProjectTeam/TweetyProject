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

import libsvm.svm_node;

/**
 * An observation is some data point which can be classified.
 * 
 * @author Matthias Thimm
 */
public interface Observation {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode();

	/* (non-Javadoc)
 	 * @see java.lang.Object#equals(java.lang.Object)
 	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * Returns the svm_node (the data model of libsvm) representation
	 * of this observation.
	 * @return an array of svm_node.
	 */
	public svm_node[] toSvmNode();
}
