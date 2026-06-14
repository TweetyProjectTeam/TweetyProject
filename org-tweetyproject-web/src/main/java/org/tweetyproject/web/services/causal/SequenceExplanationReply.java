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

import org.tweetyproject.arg.explanations.semantics.DialectialSequenceExplanation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.web.services.sequenceexplanation.AttackDTO;
import org.tweetyproject.web.services.sequenceexplanation.DialectialSequenceExplanationDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reply payload for a sequence explanation request.
 *
 * <p>Contains the serialized attack relations and sequence explanations grouped
 * by argument.</p>
 *
 * @author Oleksandr Dzhychko
 */
public final class SequenceExplanationReply {
    private final List<AttackDTO> attacks;
    private final Map<String, List<DialectialSequenceExplanationDTO>> perAtomSequenceExplanations;

    /**
     * Creates a new sequence explanation reply.
     *
     * @param attacks                     list of serialized attacks
     * @param perAtomSequenceExplanations serialized sequence explanations grouped by argument
     */
    public SequenceExplanationReply(List<AttackDTO> attacks, Map<String, List<DialectialSequenceExplanationDTO>> perAtomSequenceExplanations) {
        this.attacks = attacks;
        this.perAtomSequenceExplanations = perAtomSequenceExplanations;
    }


    /**
     * Returns the serialized attack relations.
     *
     * @return list of attack DTOs
     */
    public List<AttackDTO> getAttacks() {
        return attacks;
    }

    /**
     * Returns sequence explanations grouped by argument name.
     *
     * @return map of argument name to sequence explanation DTOs
     */
    public Map<String, List<DialectialSequenceExplanationDTO>> getPerAtomSequenceExplanations() {
        return perAtomSequenceExplanations;
    }

    /**
     * Converts service-level sequence explanations into a reply payload.
     *
     * @param sequenceExplanations service sequence explanations
     * @return serialized reply payload
     */
    public static SequenceExplanationReply from(CausalReasonerService.SequenceExplanations sequenceExplanations) {
        var attacksConverted = AttackDTO.from(sequenceExplanations.getAttacks());
        var perAtomSequenceExplanationsConverted = from(sequenceExplanations.getPerAtomSequenceExplanations());
        return new SequenceExplanationReply(attacksConverted, perAtomSequenceExplanationsConverted);
    }

    private static Map<String, List<DialectialSequenceExplanationDTO>> from(Map<Proposition, List<DialectialSequenceExplanation>> perAtomSequenceExplanations) {
        return perAtomSequenceExplanations.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> DialectialSequenceExplanationDTO.from(entry.getValue()),
                        (sequences1, sequences2) -> { throw new IllegalStateException("Encountered duplicate serialization of proposition."); },
                        LinkedHashMap::new));
    }
}

