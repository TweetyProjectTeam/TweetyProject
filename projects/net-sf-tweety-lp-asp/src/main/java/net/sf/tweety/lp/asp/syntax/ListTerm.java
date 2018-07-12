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
package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.TermAdapter;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * This class models a list term that can be used for
 * DLV complex programs.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class ListTerm extends TermAdapter<ListTermValue> {
		
	public ListTerm() {
		super(new ListTermValue());
	}
	
	public ListTerm(ListTerm other) {
		super(other.value.clone());
	}
	
	public ListTerm(ListTermValue value) {
		super(value);
	}
	
	/**
	 * constructor for list elements with given [head|tail].
	 * @param head
	 * @param tail
	 */
	public ListTerm(Term<?> head, Term<?> tail) {
		super(new ListTermValue(head, tail));
	}

	@Override
	public ListTerm clone() {
		return new ListTerm(this);
	}
	
	protected String listPrint(Collection<Term<?>> tl) {
		String ret = "";
		Iterator<Term<?>> iter = tl.iterator();
		if (iter.hasNext())
			ret += iter.next().toString();
		while (iter.hasNext())
			ret += ", " + iter.next().toString();
		return ret;
	}
}
