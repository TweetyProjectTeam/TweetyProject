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
package org.tweetyproject.agents.dialogues.oppmodels;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * This class realizes a recognition function as explained in
 * [Rienstra, Thimm, in preparation].
 * 
 * @author Matthias Thimm
 */
public class RecognitionFunction extends HashMap<Argument,Set<Argument>> {

	/** For serialization */
	private static final long serialVersionUID = 1L;
	
	/** Returns the argument which maps to the set containing
	 * the given argument.
	 * @param a an argument.
	 * @return an argument.
	 */
	public Argument getPreimage(Argument a){
		for(Map.Entry<Argument,Set<Argument>> entry: this.entrySet()){
			if(entry.getValue().contains(a))
				return entry.getKey();
		}
		return null;
	}


    /** Default Constructor */
    public RecognitionFunction(){}
}
