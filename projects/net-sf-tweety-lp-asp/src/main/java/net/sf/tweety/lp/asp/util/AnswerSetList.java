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
package net.sf.tweety.lp.asp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.lp.asp.syntax.DLPLiteral;

/**
 * this class represents a collection of answer sets and
 * provides some basic reasoning modes.
 * 
 * @author Thomas Vengels
 * @author Tim Janus
 */
public class AnswerSetList extends ArrayList<AnswerSet> {
	
	/** kill warning */
	private static final long serialVersionUID = 1130680162671151620L;

	/** constant id for the credolous policy for operations of the AnswerSetList object. */
	static public final int POLICY_CREDOLOUS = 1;
	
	/** constant id for the skeptical policy for operations of the AnswerSetList object. */
	static public final int POLICY_SKEPTICAL = 2;
	
	public AnswerSetList() {}
	
	public AnswerSetList(AnswerSetList other) {
		for(AnswerSet as : other) {
			add(new AnswerSet(as));
		}
	}

	public Set<DLPLiteral> getFactsByName(String name) {
		return getFactsByName(name, POLICY_SKEPTICAL);
	}

	/**
	 * Returns all the literals in the answer-set which have a given name.
	 * @param name		the name of the literal 'married' as example.
	 * @param policy	The used policy might be skeptical or credolous.
	 * @return			A set of literals which are also in the answerset.
	 */
	public Set<DLPLiteral> getFactsByName(String name, int policy) {
		Set<DLPLiteral> reval = new HashSet<DLPLiteral>();
		boolean first = true;
		for(AnswerSet as : this) {
			if(first == false && policy == POLICY_SKEPTICAL) {
				reval.retainAll(as.getLiteralsWithName(name));
			} else {
				reval.addAll(as.getLiteralsWithName(name));
			}
			first = false;
		}
		return reval;
	}
	
	/**
	 * this method returns true if at least one
	 * answer set support q.
	 * @param q
	 */
	public boolean	holdsOne( DLPLiteral q ) {
		for(AnswerSet as : this) {
			if(as.contains(q)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this method returns ture iff all
	 * answer sets support q.
	 * @param q
	 */
	public boolean	holdsAll( DLPLiteral q ) {
		for(AnswerSet as : this) {
			if(!as.contains(q)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Answer Sets: "+this.size());
		sb.append("\n--------------");
		for (AnswerSet s : this) {
			sb.append("\n");
			sb.append(s);
			sb.append("\n--------------");
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	@Override
	public Object clone() {
		return new AnswerSetList(this);
	}
}
