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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.dung;

import org.tweetyproject.arg.dung.reasoner.*;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.DirectedEdge;
import org.tweetyproject.graphs.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract factory for retrieving Dung extension reasoners.
 *
 * @author Jonas Klein, Lars Bengel
 */
public abstract class AbstractExtensionReasonerFactory {

    /**
     * Prevents instantiation.
     */
    protected AbstractExtensionReasonerFactory() {
    }

	/**
	 * Returns an array of all available semantics.
	 *
	 * @return An array of all available semantics.
	 */
	public static Semantics[] getSemantics() {
		return Semantics.values();
	}

	/**
	 * Creates a new reasoner measure of the given semantics with default
	 * settings.
	 * 
	 * @param sem some identifier of an semantics.
	 * @return the requested reasoner.
	 */
	public static AbstractExtensionReasoner getReasoner(Semantics sem) {
		return AbstractExtensionReasoner.getSimpleReasonerForSemantics(sem);
	}

    /**
     * Creates a DungTheory from the given number of arguments and attack relations.
     *
     * @param nr_of_arguments The number of arguments in the DungTheory.
     * @param attacks        The attack relations represented as a list of lists of integers.
     * @return A DungTheory constructed from the given arguments and attacks.
     */

    public static DungTheory getDungTheory(int nr_of_arguments, List<List<Integer>> attacks) {
        Graph<Argument> af_graph = new DefaultGraph<Argument>();
        List<Argument> arguments = new ArrayList<Argument>();
        for (int i = 1; i <= nr_of_arguments; i++){
            arguments.add(new Argument(Integer.toString(i)));
        }

        for (Argument arg: arguments){
            af_graph.add(arg);
        }
        for (List<Integer> list : attacks) {
            af_graph.add(new DirectedEdge<Argument>(arguments.get(list.get(0) - 1),arguments.get(list.get(1) - 1)));
        }

        DungTheory dungTheory = new DungTheory(af_graph);
        return dungTheory;
    }
}
