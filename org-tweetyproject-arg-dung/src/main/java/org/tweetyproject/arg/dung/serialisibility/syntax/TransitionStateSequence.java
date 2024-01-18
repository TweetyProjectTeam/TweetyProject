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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.syntax;

import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents a sequence of {@link TransitionState TransitionStates}.
 * Those {@link TransitionState TransitionStates} are traversed when serialising extensions,
 * in order to find the serialisable extensions of the 
 * {@link DungTheory abstract argumentation framework} in the first {@link TransitionState} 
 * of this sequence.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class TransitionStateSequence extends LinkedList<TransitionState>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new {@link TransitionStateSequence}
	 * @param origin {@link DungTheory Abstract argumentation framework}, which serialisability is described by this {@link TransitionStateSequence}
	 * @param sequence {@link SerialisationSequence}, which describes the se
	 */
	public TransitionStateSequence(DungTheory origin, SerialisationSequence sequence) {
		var tempAF = new TransitionState(origin, new Extension<DungTheory>());
		add(tempAF);
		for(var ext : sequence) {
			tempAF = tempAF.getNext(ext);
			add(tempAF);
		}
	}
	
	@Override
	public boolean add(TransitionState element) {
		if(this.contains(element)) {
			return false;
		}
		
		return super.add(element);
	}
	
	@Override
	public void add(int index, TransitionState element) {
		if(this.contains(element)) {
			return;
		}
		
		super.add(index, element);
		return;
	}

}
