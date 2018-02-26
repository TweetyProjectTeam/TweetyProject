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
 *  Copyright 2017 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.simplelogicdeductive.syntax;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.DigraphNode;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * 
 * @author Federico Cerutti <federico.cerutti@acm.org>
 *
 * Argument structure as defined in 
 * http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf
 *
 */
public class SimplePlLogicArgument extends Argument{

	private Collection<SimplePlRule> support = null;
	private PropositionalFormula claim = null;

	public SimplePlLogicArgument(Collection<SimplePlRule> _support,
			PropositionalFormula _claim) {
		super(null);
		this.support = _support;
		this.claim = _claim;
	}

	/**
	 * Constructor
	 * 
	 * @param 	node of a DigraphNode of a DerivationGraph<PropositionalFormula, SimplePlRule>
	 * 			@see SimplePlLogicDeductiveKnowledgebase
	 */
	public SimplePlLogicArgument(DigraphNode<SimplePlRule> node) {
		super(null);
		this.support = new HashSet<SimplePlRule>();
		this.support.add(node.getValue());

		ArrayDeque<DigraphNode<SimplePlRule>> ancestor = new ArrayDeque<DigraphNode<SimplePlRule>>();
		ancestor.add(node);
		HashSet<DigraphNode<SimplePlRule>> visited = new HashSet<DigraphNode<SimplePlRule>>();
		while (!ancestor.isEmpty()) {
			DigraphNode<SimplePlRule> n = ancestor.pop();
				
			for (DigraphNode<SimplePlRule> nparent : n.getParents()) {
				if (!visited.contains(nparent)) {
					this.support.add(nparent.getValue());
					ancestor.add(nparent);
				}
			}
			visited.add(n);

		}
		this.claim = node.getValue().getConclusion();

	}

	public Collection<? extends SimplePlRule> getSupport() {
		return this.support;
	}

	public PropositionalFormula getClaim() {
		return this.claim;
	}

	public String toString() {
		return "<" + this.support.toString() + "," + this.claim.toString()
				+ ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((support == null) ? 0 : support.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePlLogicArgument other = (SimplePlLogicArgument) obj;
		if (!this.claim.equals(other.claim))
			return false;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.equals(other.support))
			return false;
		return true;
	}
}
