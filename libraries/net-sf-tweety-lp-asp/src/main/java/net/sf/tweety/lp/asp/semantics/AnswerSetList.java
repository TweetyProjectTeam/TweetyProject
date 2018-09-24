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
package net.sf.tweety.lp.asp.semantics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.lp.asp.syntax.ASPLiteral;

/**
 * This class represents a collection of answer sets and provides some basic
 * reasoning modes. TODO old description
 * 
 * @author Thomas Vengels
 * @author Tim Janus
 * @author Anna Gessler
 */
public class AnswerSetList extends ArrayList<AnswerSet> {

	/** kill warning */
	private static final long serialVersionUID = 1130680162671151620L;

	/**
	 * constant id for the credolous policy for operations of the AnswerSetList
	 * object.
	 */
	static public final int POLICY_CREDOLOUS = 1;

	/**
	 * constant id for the skeptical policy for operations of the AnswerSetList
	 * object.
	 */
	static public final int POLICY_SKEPTICAL = 2;

	/**
	 * Empty constructor.
	 */
	public AnswerSetList() {
	}

	public AnswerSetList(AnswerSetList other) {
		for (AnswerSet as : other) {
			add(new AnswerSet(as));
		}
	}

	public Set<ASPLiteral> getFactsByName(String name) {
		return getFactsByName(name, POLICY_SKEPTICAL);
	}

	/**
	 * Returns all the literals in the answer set which have a given name.
	 * 
	 * @param name
	 *            the name of the literal, e.g. 'married'
	 * @param policy
	 *            The used policy, either credolous (1) or sceptical (2).
	 * @return A set of literals which are also in the answer set.
	 */
	public Set<ASPLiteral> getFactsByName(String name, int policy) {
		Set<ASPLiteral> reval = new HashSet<ASPLiteral>();
		boolean first = true;
		for (AnswerSet as : this) {
			if (first == false && policy == POLICY_SKEPTICAL) {
				reval.retainAll(as.getLiteralsWithName(name));
			} else {
				reval.addAll(as.getLiteralsWithName(name));
			}
			first = false;
		}
		return reval;
	}

	/**
	 * This method returns true if at least one answer set supports q.
	 * 
	 * @param q
	 */
	public boolean holdsOne(ASPLiteral q) {
		for (AnswerSet as : this) {
			if (as.contains(q)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns true iff all answer sets support q.
	 * 
	 * @param q
	 */
	public boolean holdsAll(ASPLiteral q) {
		for (AnswerSet as : this) {
			if (!as.contains(q)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("# of answer sets: " + this.size());
		
		for (AnswerSet s : this) {
			sb.append("\n");
			sb.append(s);
		}
		sb.append("\n");

		return sb.toString();
	}

	@Override
	public Object clone() {
		return new AnswerSetList(this);
	}

}
