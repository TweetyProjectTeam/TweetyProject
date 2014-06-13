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
package net.sf.tweety.lp.asp.syntax;

import net.sf.tweety.logics.commons.syntax.ComplexLogicalFormulaAdapter;
import net.sf.tweety.logics.commons.syntax.Predicate;

/**
 * This acts as abstract base class for classes implement 
 * the ELPElement interface
 * @author Tim Janus
 */
public abstract class DLPElementAdapter 
	extends ComplexLogicalFormulaAdapter 
	implements DLPElement {

	@Override
	public Class<? extends Predicate> getPredicateCls() {
		return DLPPredicate.class;
	}
	
	public abstract DLPElement clone();
}
