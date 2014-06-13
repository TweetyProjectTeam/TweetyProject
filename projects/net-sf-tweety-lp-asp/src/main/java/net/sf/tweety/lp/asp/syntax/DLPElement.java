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

import java.util.Set;
import java.util.SortedSet;

import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This interface defines base methods every element of a
 * program has to provide. 
 * 
 * @author Tim Janus
 */
public interface DLPElement extends ComplexLogicalFormula {

	/** @return all the literals used in the rule element */
	SortedSet<DLPLiteral> getLiterals();
	
	@Override
	Set<DLPPredicate> getPredicates();
	
	@Override 
	Set<DLPAtom> getAtoms(); 
	
	@Override
	DLPElement substitute(Term<?> t, Term<?> v);
	
	@Override
	DLPSignature getSignature();
	
	@Override
	DLPElement clone();
}
