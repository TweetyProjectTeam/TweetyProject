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

package org.tweetyproject.arg.dung.learning;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.learning.syntax.Input;

import java.util.Collection;

/**
 * Interface for the learning algorithm
 *
 * @author Lars Bengel
 */
public interface AFLearner {
    /**
     * learn a single input labeling and store in internal acceptance conditions
     * @param labeling some input labeling
     * @return true if the labeling has been processed successfully
     */
    boolean learnLabeling(Input labeling);

    /**
     * compute all argumentation frameworks that satisfy the internal acceptance conditions
     * @return the set of computed argumentation frameworks
     */
    Collection<DungTheory> getModels();

    /**
     * compute an argumentation framework that satisfy the internal acceptance conditions
     * @return some argumentation framework that satisfies the internal conditions
     */
    DungTheory getModel();

    /**
     * print the internal acceptance conditions
     */
    void printStatus();

    /**
     * compute the number of argumentation frameworks that satisfy the internal acceptance conditions
     * @return number of afs that produce all processed input labelings
     */
    long getNumberOfFrameworks();
    /**
     * 
     * @param shortcut whether shortcut is applied
     * @return number of frameworks
     */
    long getNumberOfFrameworks(boolean shortcut);
}
