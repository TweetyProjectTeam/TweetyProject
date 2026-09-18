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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;
import org.tweetyproject.arg.dung.syntax.Attack;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for serializing an attack between two arguments.
 *
 * @author Oleksandr Dzhychko
 */
public final class AttackDTO {
    /** the attacker */
    private final @NonNull @NotNull String attacker;
    /** the attacked argument */
    private final @NonNull @NotNull String attacked;

    /**
     * Initialize new instance from given arguments
     * @param attacker name of attacker argument
     * @param attacked name of attacked argument
     */
    public AttackDTO(
            @JsonProperty(value="attacker", required = true) @NonNull @NotNull String attacker,
            @JsonProperty(value="attacked", required = true) @NonNull @NotNull String attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
    }

    /**
     * Serialize a collection of attacks
     * @param attacks set of attacks
     * @return List of serialized objects
     */
    public static List<AttackDTO> from(Collection<Attack> attacks) {
        return attacks.stream()
                .map(AttackDTO::from)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Serialize an attacks
     * @param attack an attack
     * @return serialized attack
     */
    public static AttackDTO from(Attack attack) {
        return new AttackDTO(attack.getAttacker().toString(), attack.getAttacked().toString());
    }

    /**
     * get attacker argument name
     * @return name of attacker argument
     */
    public String getAttacker() {
        return attacker;
    }

    /**
     * get attacked argument name
     * @return name of attacked argument
     */
    public String getAttacked() {
        return attacked;
    }
}
