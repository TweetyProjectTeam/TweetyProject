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
package org.tweetyproject.web.services.causal;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;
import org.tweetyproject.causal.parser.CausalParser;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility for parsing serialized conclusions filters.
 *
 * @author Oleksandr Dzhychko
 */
public class ConclusionsFilterSerialization {

    /**
     * Prevents instantiation.
     */
    private ConclusionsFilterSerialization() {
    }

    /** Delimiter used to separate atoms in the filter string. */
    private static final String ATOM_DELIMITER = ",";
    /** Parser used to interpret the conclusions filter string. */
    private static final CausalParser CAUSAL_PARSER = new CausalParser();

    /**
     * Parse the filter string for conclusions.
     *
     * @param conclusionsFilterString {@link Proposition}s as parsable by {@link CausalParser#parseFormula(String)} seperated by {@link #ATOM_DELIMITER}
     * @return Set of {@link Proposition}s or {@code null} if the input is {@code null} or empty.
     */
    public static @Nullable Set<Proposition> parse(@Nullable String conclusionsFilterString) {
        if (conclusionsFilterString == null) {
            return null;
        }
        List<PlFormula> formulae;
        try {
            formulae = CAUSAL_PARSER.parseListOfFormulae(conclusionsFilterString, ATOM_DELIMITER);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        var propositions = formulae.stream()
                .map(formula -> {
                    if (formula instanceof Proposition) {
                        return (Proposition) formula;
                    }
                    String msg = String.format("Formula `%s` is not a proposition,", formula);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
                })
                .collect(Collectors.toUnmodifiableSet());

        if (propositions.isEmpty()) {
            return null;
        }
        return propositions;
    };
}
