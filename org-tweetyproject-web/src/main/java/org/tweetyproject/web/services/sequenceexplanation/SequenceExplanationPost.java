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
 * Request payload for sequence explanation web service calls.
 *
 * This class encapsulates the optional notification email, the timeout
 * configuration, and the concrete command information required to execute
 * a sequence explanation request.
 */
public class SequenceExplanationPost {

    /**
     * Creates a new request for sequence explanations.
     *
     * @param email        optional notification email address
     * @param timeout      timeout value for the request
     * @param unit_timeout timeout unit for the request
     * @param cmd          command payload that defines the requested action
     */
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

    /**
     * Returns the optional email address for request notifications.
     *
     * @return email address or null if not provided
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the timeout value for the request.
     *
     * @return request timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Returns the timeout unit for the request.
     *
     * @return timeout unit string
     */
    public String getUnit_timeout() {
        return unit_timeout;
    }

    /**
     * Returns the command payload for this request.
     *
     * @return sequence explanation command
     */
    public SequenceExplanationCmd getCmd() {
        return cmd;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GetSequenceExplanationsCmd.class, name = "get_sequence_explanations"),
    })
    /**
     * Marker interface for sequence explanation request commands.
     *
     * <p>Concrete implementations define the payload content for a specific
     * sequence explanation action.</p>
     */
    public interface SequenceExplanationCmd {
    }

    /**
     * helper class for sequence explanation commands
     */
    public static class GetSequenceExplanationsCmd implements SequenceExplanationCmd {
        /** The attack relations represented as a list of lists of integers.*/
        private final @Valid @NotNull List<@NotNull AttackDTO> attacks;
        private final @Valid @Nullable  List<@NotNull String> argumentFilter;

        /**
         * Creates a new command for retrieving sequence explanations.
         *
         * @param attacks        the attack relations used for sequence explanation
         * @param argumentFilter optional argument filter to restrict the result set
         */
        public GetSequenceExplanationsCmd(
                @JsonProperty(value="attacks", required = true)
                List<AttackDTO> attacks,
                @JsonProperty(value = "argument_filter")
                List<String> argumentFilter) {
            this.attacks = attacks;
            this.argumentFilter = argumentFilter;
        }

        /**
         * Returns the attack relations used for the request.
         *
         * @return the attack relation list
         */
        public List<AttackDTO> getAttacks() {
            return attacks;
        }

        /**
         * Returns an optional filter of argument identifiers.
         *
         * @return argument filter list or null when not specified
         */
        @Nullable
        public List<String> getArgumentFilter() {
            return argumentFilter;
        }
    }

}
