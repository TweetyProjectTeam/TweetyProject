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
package net.sf.tweety.action;

import net.sf.tweety.logics.commons.analysis.ConsistencyTester;

/**
 * Classes implementing this interface are capable of checking whether a given
 * action description is consistent according to some consistency measurements.
 * 
 * @author Sebastian Homann
 * @author Tim Janus
 */
public interface ActionDescriptionConsistencyTester<T extends CausalLaw> extends ConsistencyTester<ActionDescription<T>>{
  
  /**
   * Checks whether the given set of causal rules is consistent.
   * 
   * @param causalRules a set of causal rules.
   * @return true iff the given set of causal rules is consistent.
   */
  boolean isConsistent(ActionDescription<T> causalRules );
}
