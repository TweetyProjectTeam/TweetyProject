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
package net.sf.tweety.beliefdynamics;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.commons.Formula;

/**
 * Implements the revision method with two belief bases by delegating the processing
 * to the revision method bases on an ordered list of belief bases. It acts as base
 * class for revision approaches which support the revision of multiple belief bases
 * in one step.
 * 
 * @author Tim Janus
 *
 * @param <T> The type of the belief base
 */
public abstract class CredibilityRevisionNonIterative<T extends Formula> 
	extends CredibilityRevision<T>{

	@Override
	public Collection<T> revise(Collection<T> beliefBase1, Collection<T> beliefBase2) {
		List<Collection<T>> param = new LinkedList<Collection<T>>();
		param.add(beliefBase1);
		param.add(beliefBase2);
		return revise(param);
	}

}
