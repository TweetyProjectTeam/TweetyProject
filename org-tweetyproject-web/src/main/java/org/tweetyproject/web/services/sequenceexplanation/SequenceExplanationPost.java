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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Oleksandr Dzhychko
 */
public class SequenceExplanationPost {

    public SequenceExplanationPost(
            @JsonProperty("email")
            String email,
            @JsonProperty(value = "timeout", required = true)
            int timeout,
            @JsonProperty(value = "unit_timeout", required = true)
            String unit_timeout,
            @JsonProperty(value = "cmd", required = true)
            SequenceExplanationCmd cmd
    ) {
        this.email = email;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
        this.cmd = cmd;
    }

    private final @Nullable String email;
    private final int timeout;
    private final @NotNull String unit_timeout;
    private final @Valid @NotNull SequenceExplanationCmd cmd;

    public String getEmail() {
        return email;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getUnit_timeout() {
        return unit_timeout;
    }

    public SequenceExplanationCmd getCmd() {
        return cmd;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GetSequenceExplanationsCmd.class, name = "get_sequence_explanations"),
    })
    public interface SequenceExplanationCmd {
    }

    /**
     * helper class for sequence explanation commands
     */
    public static class GetSequenceExplanationsCmd implements SequenceExplanationCmd {
        /** The attack relations represented as a list of lists of integers.*/
        private final @Valid @NotNull List<@NotNull AttackDTO> attacks;
        private final @Valid @Nullable  List<@NotNull String> argumentFilter;

        public GetSequenceExplanationsCmd(
                @JsonProperty(value="attacks", required = true)
                List<AttackDTO> attacks,
                @JsonProperty(value = "argument_filter")
                List<String> argumentFilter) {
            this.attacks = attacks;
            this.argumentFilter = argumentFilter;
        }

        public List<AttackDTO> getAttacks() {
            return attacks;
        }

        @Nullable
        public List<String> getArgumentFilter() {
            return argumentFilter;
        }
    }

}
