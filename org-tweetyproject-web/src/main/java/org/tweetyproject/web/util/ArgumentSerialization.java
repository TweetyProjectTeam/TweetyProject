/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.util;

import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Methods for serializing arguments as strings
 * @author Oleksandr Dzhychko
 */
public final class ArgumentSerialization {
    /**
     * Serialize argument object to string
     * @param argument some argument
     * @return string of argument name
     */
    public static String from(Argument argument) {
        return argument.toString();
    }

    /**
     * Serialize a set of arguments to strings
     * @param arguments a set of arguments
     * @return list of argument strings
     */
    public static List<String> fromCollection(Collection<Argument> arguments) {
        return arguments.stream()
                .map(ArgumentSerialization::from)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Serialize a set of sets of arguments to strings
     * @param collectionsOfArguments a set of sets of arguments
     * @return corresponding list of lists of strings
     */
    public static List<List<String>> fromCollectionOfCollections(List<Collection<Argument>> collectionsOfArguments) {
        return collectionsOfArguments.stream()
                .map(ArgumentSerialization::fromCollection)
                .collect(Collectors.toUnmodifiableList());
    }
}
