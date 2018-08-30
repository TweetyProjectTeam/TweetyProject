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
package net.sf.tweety.lp.asp.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;

/**
 * An answer set is a belief set which only contains literals and represents the
 * deductive belief set of an extended logic program under the answer set
 * semantic. (TODO this is the old description, is it accurate for asp core 2?
 * 
 * @author Thomas Vengels
 * @author Tim Janus
 * @author Anna Gessler
 */
public class AnswerSet extends BeliefSet<ASPLiteral> {
	public final int level;
	public final int weight;

	public AnswerSet() {
		level = 0;
		weight = 1;
	}

	public AnswerSet(Collection<ASPLiteral> lits, int level, int weight) {
		super(lits);
		this.level = level;
		this.weight = weight;
	}

	public AnswerSet(AnswerSet other) {
		super(other);
		this.level = other.level;
		this.weight = other.weight;
	}

	public Set<ASPLiteral> getLiteralsWithName(String name) {
		Set<ASPLiteral> reval = new HashSet<ASPLiteral>();
		for (ASPLiteral lit : this) {
			if (lit.getName().equals(name)) {
				reval.add(lit);
			}
		}
		return reval;
	}

	public Set<String> getFunctors() {
		Set<String> reval = new HashSet<String>();
		return reval;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + level + "," + weight + "]";
	}

	@Override
	public Object clone() {
		return new AnswerSet(this);
	}

	@Override
	public Signature getSignature() {
		FolSignature sig = new FolSignature();
		for (ASPLiteral lit : this) 
			sig.addSignature(lit.getSignature());
		return sig;
	}
}
