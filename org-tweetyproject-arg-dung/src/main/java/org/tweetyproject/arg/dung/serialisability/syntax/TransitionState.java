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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * A state of the transition system for serialised semantics
 * It consists of an argumentation framework and a set of arguments
 *
 * @author Lars Bengel
 */
public class TransitionState {
	/**
	 * theory
	 */
    private DungTheory theory;
    /**
     * extension
     */
    private Extension<DungTheory> extension;
    /**
     * 
     * @param theory a dung theory
     * @param extension an extension
     */
    public TransitionState(DungTheory theory, Extension<DungTheory> extension) {
        this.theory = theory;
        this.extension = extension;
    }

    /**
     * compute the successor state for the given extension
     * @param ext a set of arguments that has been selected by a selection function
     * @return the successor state
     */
    public TransitionState getNext(Extension<DungTheory> ext) {
        DungTheory reduct = this.theory.getReduct(ext);
        Extension<DungTheory> newExt = new Extension<>(this.extension);
        newExt.addAll(ext);

        return new TransitionState(reduct, newExt);
    }
    /**
     * 
     * @return the theory
     */
    public DungTheory getTheory() {
        return theory;
    }
    /**
     * 
     * @return the extension
     */
    public Extension<DungTheory> getExtension() {
        return extension;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
    	if(!(obj instanceof TransitionState))
    		return false;
    	
    	var other = (TransitionState) obj;
    	return this.theory.equals(other.getTheory()) && this.extension.equals(other.extension);
    }
    
    @Override
	public int hashCode() {
		final int prime = 67;
		int result = 1;
		result = prime * result
				+ ((theory == null) ? 0 : theory.hashCode());
		result = prime * result
				+ ((extension == null) ? 0 : extension.hashCode());
		return result;
	}
}
