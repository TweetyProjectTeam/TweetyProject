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

import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models a defeasible rule in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public class DefeasibleRule extends DelpRule {

	/**
	 * Initializes the defeasible rule with the given parameters
	 * @param head a literal
	 * @param body a set of literals
	 */
	public DefeasibleRule(FolFormula head, Set<FolFormula> body){
		super(head,body);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#toString()
	 */
	public String toString(){
		String str = head.toString()+" -< ";
		Iterator<FolFormula> it = body.iterator();
		if(it.hasNext()){
			FolFormula lit = (FolFormula) it.next();
			str += lit.toString();
		}
		while(it.hasNext()){
			FolFormula lit = (FolFormula) it.next();
			str += ","+lit.toString();
		}
		str += ".";
		return str;
	}

	/**
	 * returns the translation of this rule as a strict rule
	 * @return the translation of this rule as a strict rule
	 */
	public StrictRule toStrictRule(){
		return new StrictRule(this.head,this.body);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		FolFormula newHead = (FolFormula)((FolFormula)this.getConclusion()).substitute(v,t);
		Set<FolFormula> newBody = new HashSet<FolFormula>();
		for(FolFormula f: this.body)
			newBody.add((FolFormula)f.substitute(v, t));
		return new DefeasibleRule(newHead,newBody);
	}

	@Override
	public RelationalFormula clone() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}
}
