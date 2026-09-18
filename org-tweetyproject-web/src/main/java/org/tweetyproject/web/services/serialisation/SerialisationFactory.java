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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.serialisation;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.List;

/**
 * Main factory for retrieving serialisation related things
 *
 * @author Lars Bengel
 */
public class SerialisationFactory {
    /**
     * parses an extension from a list of integers
     * @param extension extension as a list on integers
     * @return the parsed extension
     */
    public static Extension<DungTheory> getExtension(List<Integer> extension) {
        if (extension==null) return null;
        Extension<DungTheory> result = new Extension<>();
        for (Integer i : extension) {
            result.add(new Argument(Integer.toString(i)));
        }
        return result;
    }

    /**
     * determine the selection function
     * @param selectionFunction string id of selection functions
     * @return the selection function
     */
    public static SelectionFunction getSelectionFunction(String selectionFunction) {
        if (selectionFunction==null) return null;
        switch (selectionFunction) {
            case "ADM" -> {
                return SelectionFunction.ADMISSIBLE;
            } case "UC" -> {
                return SelectionFunction.UNCHALLENGED;
            } case "GR" -> {
                return SelectionFunction.GROUNDED;
            } default -> throw new IllegalArgumentException("unknown selection function " + selectionFunction);
        }
    }

    /**
     * get all available selection functions
     * @return the selection functions
     */
    public static List<String> getSelectionFunctions() {
        return List.of("ADM", "UC", "GR");
    }

    /**
     * determine the termination function
     * @param terminationFunction string idf of termination function
     * @return the termination function
     */
    public static TerminationFunction getTerminationFunction(String terminationFunction) {
        if (terminationFunction==null) return null;
        switch (terminationFunction) {
            case "ADM" -> {
                return TerminationFunction.ADMISSIBLE;
            } case "UC" -> {
                return TerminationFunction.UNCHALLENGED;
            } case "CO" -> {
                return TerminationFunction.COMPLETE;
            } case "PR" -> {
                return TerminationFunction.PREFERRED;
            } case "ST" -> {
                return TerminationFunction.STABLE;
            } default -> throw new IllegalArgumentException("unknown selection function " + terminationFunction);
        }
    }

    /**
     * get all available termination functions
     * @return the termination functions
     */
    public static List<String> getTerminationFunction() {
        return List.of("ADM", "CO", "UC", "PR", "ST");
    }
}
