/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.delp.syntax;

import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * This class models a strict rule in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public class StrictRule extends DelpRule {

	/**
	 * Default constructor; initializes head and body of the strict rule
	 * @param head a literal
	 * @param body a set of literals
	 */
	public StrictRule(FolFormula head, Set<FolFormula> body){
		super(head,body," <- ");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
    @Override
    public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
        return new StrictRule(
                getConclusion().substitute(v,t),
                body.stream()
                        .map(f -> f.substitute(v,t))
                        .collect(Collectors.toSet()));
    }

    @Override
    public RelationalFormula clone() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}
}
