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

import java.util.Collection;
import java.util.Set;

import org.tweetyproject.agents.dialogues.DialogueTrace;
import org.tweetyproject.arg.dung.syntax.*;
/**
 * Objects of this class represent utility function that assess
 * dialogue traces.
 *
 * @author Tjitze Rienstra, Matthias Thimm
 * @param <S> The type of elements in a move
 * @param <T> The type of moves in this dialgoue trace
 */
public abstract class UtilityFunction<S,T extends Collection<S>> {

	/**
	 * Gives the utility of the given dialogue trace.
	 * @param t some dialogue trace.
	 * @return the utility of the trace
	 */
	public abstract double getUtility(DialogueTrace<S,T> t);

	/**
	 * Gives the utility of the given dialogue trace that
	 * takes the additional arguments and attacks into account.
	 * @param t some dialogue trace.
	 * @param additionalArguments a set of arguments that have to
	 * be taken into account
	 * @param additionalAttacks a set of attacks that have to
	 * be taken into account
	 * @return the utility of the trace
	 */
	public abstract double getUtility(DialogueTrace<S,T> t, Set<S> additionalArguments, Set<Attack> additionalAttacks);


    /** Default Constructor */
    public UtilityFunction(){}
}
