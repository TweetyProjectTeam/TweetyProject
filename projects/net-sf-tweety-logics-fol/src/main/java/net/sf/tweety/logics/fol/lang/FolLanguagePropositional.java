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
package net.sf.tweety.logics.fol.lang;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class represents a classical propositional language, i.e. a language without
 * variables, constants, functors, and predicates of arity greater zero.
 * @author Matthias Thimm
 */
public class FolLanguagePropositional extends FolLanguage {
	/**
	 * Creates a new language on the given signature.
	 * @param signature a signature.
	 */
	public FolLanguagePropositional(Signature signature){
		super(signature);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.lang.FolLanguage#isRepresentable(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean isRepresentable(Formula formula){
		if(!super.isRepresentable(formula)) return false;
		// it is sufficient to check whether there are predicates of arity greater zero.
		for(Predicate p: ((FolFormula)formula).getPredicates())
			if(p.getArity() != 0)
				return false;
		return true;
	}
}
