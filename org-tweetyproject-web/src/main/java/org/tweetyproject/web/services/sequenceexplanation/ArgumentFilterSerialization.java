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
package org.tweetyproject.web.services.sequenceexplanation;

import org.springframework.lang.Nullable;
import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility for converting serialized argument filters into sets of arguments.
 *
 * @author Oleksandr Dzhychko
 */
public class ArgumentFilterSerialization {

    /** Prevents instantiation. */
    private ArgumentFilterSerialization() {
        // utility class
    }

    /**
     * Deserialize the filter for arguments.
     *
     * @param argumentFilter List of {@link Argument} names or {@code null}
     * @return Set of {@link Argument}s or {@code null} if the input is {@code null} or empty.
     */
    public static @Nullable Set<Argument> deserialize(@Nullable List<String> argumentFilter) {
        if (argumentFilter == null || argumentFilter.isEmpty()) {
            return null;
        }
        return argumentFilter.stream().map(Argument::new).collect(Collectors.toUnmodifiableSet());
    }
}
