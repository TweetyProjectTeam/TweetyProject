/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.syntax;

import java.util.Set;
import java.util.SortedSet;

import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.ComplexLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolSignature;


/**
 * This class is a common interface for all ASP formulas. It
 * defines base methods every element of a program has to provide.
 * 
 * @author Tim Janus
 * @author Anna Gessler
 */
public abstract interface ASPElement extends ComplexLogicalFormula {
	
	/** @return all the literals used in the rule element */
	SortedSet<ASPLiteral> getLiterals();
	
	@Override
	Set<Predicate> getPredicates();
	
	@Override 
	Set<ASPAtom> getAtoms(); 
	
	@Override
	ASPElement substitute(Term<?> t, Term<?> v);
	
	@Override
	FolSignature getSignature();
	
	@Override
	ASPElement clone();
}
