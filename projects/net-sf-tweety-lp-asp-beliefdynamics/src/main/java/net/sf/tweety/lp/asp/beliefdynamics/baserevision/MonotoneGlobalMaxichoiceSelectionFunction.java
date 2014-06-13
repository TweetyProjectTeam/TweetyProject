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
package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;
import java.util.Collections;

import net.sf.tweety.lp.asp.syntax.Rule;

/**
 * This class implements a monotone global maxichoise selection function
 * for remainder sets of extended logic programs as introduced in [KKI12].
 * 
 *  [KKI12] Kruempelmann, Patrick und Gabriele Kern-Isberner: 
 * 	Belief Base Change Operations for Answer Set Programming. 
 *  In: Cerro, Luis Farinas, Andreas Herzig und Jerome Mengin (Herausgeber):
 *  Proceedings of the 13th European conference on Logics in Artificial 
 *  Intelligence, Band 7519, Seiten 294-306, Toulouse, France, 2012. 
 *  Springer Berlin Heidelberg.
 *  
 * @author Sebastian Homann
 */
public class MonotoneGlobalMaxichoiceSelectionFunction implements SelectionFunction<Rule> {

	/**
	 * Selects the maximal remainder set from the set of all remainder sets according to
	 * a total order on all extended logic programs. This entails the monotony-property
	 * for this selection function.
	 * @param remainderSets set of all remainder sets
	 * @return a single remainder set or P, if there is no remainder set of P with screen R
	 */
	public Collection<Rule> select(ScreenedRemainderSets remainderSets) {
		if(remainderSets.isEmpty()) {
			return remainderSets.getSourceBeliefBase();
		}
		
		return Collections.max(remainderSets.asPrograms(), new ELPLexicographicalComparator());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.logicprogramming.asplibrary.beliefdynamics.baserevision.SelectionFunction#select(net.sf.tweety.logicprogramming.asplibrary.beliefdynamics.baserevision.RemainderSets)
	 */
	@Override
	public Collection<Rule> select(RemainderSets<Rule> remainderSets) {
		if(remainderSets instanceof ScreenedRemainderSets) {
			return select((ScreenedRemainderSets) remainderSets);
		}
		return null;
	}

}