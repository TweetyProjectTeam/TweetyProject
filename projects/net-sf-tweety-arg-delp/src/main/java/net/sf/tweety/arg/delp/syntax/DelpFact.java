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
package net.sf.tweety.arg.delp.syntax;

import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class implements a fact in defeasible logic programming which encapsulates a literal.
 *
 * @author Matthias Thimm
 *
 */
public class DelpFact extends DelpRule {
	
	/**
	 * Default constructor; initializes this fact with the given literal
	 * @param literal a literal
	 */
	public DelpFact(FolFormula literal){
		super(literal, new HashSet<FolFormula>());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return super.getConclusion().toString()+".";
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new DelpFact((FolFormula)((FolFormula)this.getConclusion()).substitute(v, t));
	}

	@Override
	public DelpFact clone() {
		return new DelpFact((FolFormula) this.getFormula().clone());
	}

}
