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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.semantics;

import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.AbstractInterpretation;

/**
 * This abstract class acts as a common ancestor for interpretations to
 * abstract argumentation frameworks.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractArgumentationInterpretation extends AbstractInterpretation<DungTheory,Argument> {

	/* (non-Javadoc)
	 * @see org.tweetyproject.Interpretation#satisfies(org.tweetyproject.Formula)
	 */
	@Override
	public boolean satisfies(Argument formula) throws IllegalArgumentException {
		return this.getArgumentsOfStatus(ArgumentStatus.IN).contains(formula);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.Interpretation#satisfies(org.tweetyproject.BeliefBase)
	 */
	@Override
	public boolean satisfies(DungTheory beliefBase) throws IllegalArgumentException {
		throw new IllegalArgumentException("Satisfaction of belief bases by extensions is undefined.");
	}

	/**
	 * returns true if every attacker on <code>argument</code> is attacked by some 
	 * accepted argument wrt. the given theory.
	 * @param argument an argument
	 * @param dungTheory a Dung theory (the knowledge base)
	 * @return true if every attacker on <code>argument</code> is attacked by some 
	 * accepted argument wrt. the given theory.
	 */
	public boolean isAcceptable(Argument argument, DungTheory dungTheory){
		Set<Argument> attackers = dungTheory.getAttackers(argument);
		Iterator<Argument> it = attackers.iterator();
		while (it.hasNext())			
			if(!dungTheory.isAttacked(it.next(),this.getArgumentsOfStatus(ArgumentStatus.IN)))
				return false;		
		return true;
	}
	
	/**
	 * returns true if no accepted argument attacks another accepted one in
	 * this interpretation wrt. the given theory.
	 * @param dungTheory a Dung theory.
	 * @return true if no accepted argument attacks another accepted one in
	 * this interpretation wrt. the given theory.
	 */
	public boolean isConflictFree(DungTheory dungTheory){
		for(Argument a: this.getArgumentsOfStatus(ArgumentStatus.IN))
			for(Argument b: this.getArgumentsOfStatus(ArgumentStatus.IN))
				if(dungTheory.isAttackedBy(a, b))
					return false;
		return true;
	}
	
	/**
	 * returns true if every accepted argument of this is defended by some accepted
	 * argument wrt. the given Dung theory.
	 * @param dungTheory a Dung theory. 
	 * @return true if every accepted argument of this is defended by some accepted
	 * argument wrt. the given Dung theory.
	 */
	public boolean isAdmissable(DungTheory dungTheory){
		if(!this.isConflictFree(dungTheory)) return false;
		Iterator<Argument> it = this.getArgumentsOfStatus(ArgumentStatus.IN).iterator();
		while(it.hasNext()){			
			if(!this.isAcceptable(it.next(),dungTheory))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns all arguments that have the given status in this interpretation.
	 * @param status the status of the arguments to be returned.
	 * @return the set of arguments with the given status.
	 */
	public abstract Extension getArgumentsOfStatus(ArgumentStatus status);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
}
