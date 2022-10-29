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
package org.tweetyproject.arg.bipolar.inducers;



import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * AbstractPEAFInducer includes helper functions for concrete implementations
 *
 * @author Taha Dogan Gunes
 */
public abstract class AbstractPEAFInducer implements PEAFInducer {

    /**
     * PEAFTheory that will be induced.
     */
    protected PEAFTheory peafTheory;

    /**
     * Default constructor (checks if the given PEAF is cyclic or not.)
     * <p>
     * Cyclic in PEAF support links is forbidden in PEAF specification.
     *
     * @param peafTheory a PEAFTheory object
     */
    public AbstractPEAFInducer(PEAFTheory peafTheory) {
        this.peafTheory = peafTheory;

        if (isSupportLinksCyclic()) {
            throw new CyclicException("This PEAF can not be induced, because a cyclic was found.");
        }
    }

    /**
     * Check if the given PEAFTheory has cyclic or not in the support links.
     *
     * @return true if there is a cyclic
     */
    private boolean isSupportLinksCyclic() {
        // Adapted from https://www.geeksforgeeks.org/detect-cycle-in-a-graph/
        // Mark all the vertices as not visited and
        // not part of recursion stack

        Map<BArgument, Boolean> visited = new HashMap<BArgument, Boolean>();
        Map<BArgument, Boolean> recStack = new HashMap<BArgument, Boolean>();
        for (BArgument argument : peafTheory.getArguments()) {
            visited.put(argument, false);
            recStack.put(argument, false);
        }

        // Call the recursive helper function to
        // detect cycle in different DFS trees
        for (BArgument arg : this.peafTheory.getArguments()) {
            if (isCyclicUtil(arg, visited, recStack))
                return true;
        }

        return false;
    }

    /**
     * 
     * @param consumer a inducible eaf consumer
     * @param n number of iterations
     */
    public void induceNTimes(Consumer<InducibleEAF> consumer, int n) {
        while (n > 0) {
            this.induce(consumer);
            n--;
        }
    }

    /**
     * Returns the used PEAFTheory in inducer
     *
     * @return the given PEAFTheory object
     */
    public PEAFTheory getPeafTheory() {
        return peafTheory;
    }

    /**
     * Recursive DFS to find some cyclics in the PEAF
     * <p>
     * This function is a variation of DFSUtil() in
     * https://www.geeksforgeeks.org/archives/18212
     *
     * @param arg      starting EArgument to explore
     * @param visited  the map stores if the argument is explored or not
     * @param recStack recursion stack
     * @return return true if it is a cyclic
     */
    private boolean isCyclicUtil(BArgument arg, Map<BArgument, Boolean> visited,
                                 Map<BArgument, Boolean> recStack) {
        // Mark the current node as visited and
        // part of recursion stack
        if (recStack.get(arg))
            return true;

        if (visited.get(arg))
            return false;

        visited.put(arg, true);
        recStack.put(arg, true);
        Set<Support> set = peafTheory.getSupports(arg);

        for (Support c : set) {
            for (BArgument to : c.getSupported()) {
                if (isCyclicUtil(to, visited, recStack))
                    return true;
            }
        }
        recStack.put(arg, false);

        return false;
    }
}
