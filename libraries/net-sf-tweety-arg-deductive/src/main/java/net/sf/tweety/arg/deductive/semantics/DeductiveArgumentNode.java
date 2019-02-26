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
package net.sf.tweety.arg.deductive.semantics;

import java.util.Collection;

import net.sf.tweety.graphs.Node;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Extends a deductive argument by a unique identifier.
 * This allows the same deductive argument to appear in an argument tree multiple times. * 
 * @author Matthias Thimm
 */
public class DeductiveArgumentNode extends DeductiveArgument implements Node{

	/** A global counter for IDs. */
	private static int counter = 0;
	
	/** The id of this node. */
	private int id;
	
	/** 
	 * Creates a new deductive argument node with the given support
	 * and claim (a unique ID is generated)
	 * @param support a set of formulas.
	 * @param claim a formula.
	 */
	public DeductiveArgumentNode(Collection<? extends PlFormula> support, PlFormula claim) {
		super(support, claim);
		DeductiveArgumentNode.counter++;
		this.id = DeductiveArgumentNode.counter;		
	}
	
	/** 
	 * Creates a new deductive argument node from the given deductive argument
	 * (a unique ID is generated)
	 * @param arg a deductive argument.
	 */
	public DeductiveArgumentNode(DeductiveArgument arg) {
		super(arg.getSupport(), arg.getClaim());
		DeductiveArgumentNode.counter++;
		this.id = DeductiveArgumentNode.counter;		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeductiveArgumentNode other = (DeductiveArgumentNode) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
