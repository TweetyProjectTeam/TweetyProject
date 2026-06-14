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

import org.tweetyproject.arg.explanations.semantics.DialectialSequenceExplanation;
import org.tweetyproject.web.util.ArgumentSerialization;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Oleksandr Dzhychko
 */
public final class DialectialSequenceExplanationDTO {
    /** the argument */
    private final String argument;
    /** the supporting sets of the sequence */
    private final List<List<String>> supporters;
    /** the defeated counterargument-sets of the sequence */
    private final List<List<String>> defeated;

    /**
     * Initialize a new DialectialSequenceExplanationDTO
     * @param argument      the target argument
     * @param supporters    the supporting sets
     * @param defeated      the defeated sets
     */
    public DialectialSequenceExplanationDTO(String argument, List<List<String>> supporters, List<List<String>> defeated) {
        this.argument = argument;
        this.supporters = supporters;
        this.defeated = defeated;
    }

    /**
     * return the serialized argument
     * @return the argument
     */
    public String getArgument() {
        return argument;
    }

    /**
     * return the serialized supporting sets
     * @return the serialized supporting sets
     */
    public List<List<String>> getSupporters() {
        return supporters;
    }

    /**
     * return the serialized defeated sets
     * @return the serialized defeated sets
     */
    public List<List<String>> getDefeated() {
        return defeated;
    }

    /**
     * Serialize a dialectial sequence explanation
     * @param explanation some explanation
     * @return the serialized object
     */
    public static DialectialSequenceExplanationDTO from(DialectialSequenceExplanation explanation) {
        return new DialectialSequenceExplanationDTO(
                explanation.getArgument().toString(),
                ArgumentSerialization.fromCollectionOfCollections(explanation.getSupporters()),
                ArgumentSerialization.fromCollectionOfCollections(explanation.getDefeated())
        );
    }

    /**
     * Serialize a list of dialectial sequence explanations
     * @param explanations a list of explanations
     * @return the serialized object
     */
    public static List<DialectialSequenceExplanationDTO> from(List<DialectialSequenceExplanation> explanations) {
        return explanations.stream().map(DialectialSequenceExplanationDTO::from)
                .collect(Collectors.toUnmodifiableList());
    }
}
