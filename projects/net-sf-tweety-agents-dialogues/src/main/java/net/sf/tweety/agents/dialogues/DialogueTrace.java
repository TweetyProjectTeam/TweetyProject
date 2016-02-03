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
package net.sf.tweety.agents.dialogues;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Objects of this class represent traces of dialogue in an argumentation game,
 * ie. sequences of moves (e.g. sets of arguments or sets of formulas).
 * 
 * @author Tjitze Rienstra, Matthias Thimm
 * @param <S> The type of elements in a move
 * @param <T> The type of moves in this dialgoue trace
 */
public class DialogueTrace<S,T extends Collection<S>> extends LinkedList<T>{

	/** For serialization. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns all elements mentioned in this dialogue trace.
	 * @return a set of S.
	 */
	public Collection<S> getElements(){
		Collection<S> elements = new HashSet<S>();
		for(T e: this)
			elements.addAll(e);	
		return elements;
	}
	
	/**
	 * Returns a copy of this trace and adds the given move to this copy.
	 * 
	 * @param newMove Move to add to copy.
	 * @return Copy of this trace, with newMove appended.
	 */
	public DialogueTrace<S,T> addAndCopy(T newMove) {
		DialogueTrace<S,T> trace = new DialogueTrace<S,T>();
		trace.addAll(this);
		trace.add(newMove);
		return trace;
	}
	
	/* (non-Javadoc)
	 * @see java.util.LinkedList#size()
	 */
	public int size(){
		//return the number of elements, not the number of moves
		int size = 0;
		for(T e: this)
			size += e.size();
		return size;
	}
}
