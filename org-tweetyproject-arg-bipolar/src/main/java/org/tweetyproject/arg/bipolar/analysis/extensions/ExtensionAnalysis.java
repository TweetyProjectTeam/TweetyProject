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
package org.tweetyproject.arg.bipolar.analysis.extensions;

import java.util.List;
import java.util.Set;

/**
 * The `ExtensionAnalysis` interface provides a contract for analyzing and retrieving extensions
 * in the context of argumentation frameworks, particularly within the domain of bipolar argumentation.
 */
public interface ExtensionAnalysis {

    /**
     * Computes and returns a list of extensions.
     *
     * <p>
     * Each extension is represented as a set of strings, where each string corresponds to an argument
     * in the argumentation framework. The method returns all extensions that are found according to
     * the specific analysis performed by the implementing class.
     * </p>
     *
     * @return a list of sets, where each set contains strings representing an extension of the argumentation framework.
     */
    List<Set<String>> getExtensions();
}