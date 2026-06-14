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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.lang.NonNull;
import org.tweetyproject.web.services.sequenceexplanation.SequenceExplanationService.SequenceExplanations;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Response to {@link SequenceExplanationPost}
 *
 * @author Oleksandr Dzhychko
 */
public final class SequenceExplanationResponse {

    public enum Status {
        SUCCESS,
        TIMEOUT,
    }

    private final SequenceExplanationResult reply;
    private final String email;
    private final double time;
    private final String unit_timeout;
    private final Status status;

    /**
     * Creates a sequence explanation response.
     *
     * @param reply        the response result payload
     * @param email        the request email address
     * @param time         execution time
     * @param unit_timeout execution time unit
     * @param status       response status
     */
    public SequenceExplanationResponse(SequenceExplanationResult reply, String email, double time, @NonNull String unit_timeout, Status status) {
        this.reply = reply;
        this.email = email;
        this.time = time;
        this.status = status;
        this.unit_timeout = unit_timeout;
    }

    /**
     * Returns the response payload.
     *
     * @return sequence explanation result
     */
    public SequenceExplanationResult getReply() {
        return reply;
    }

    /**
     * Returns the email associated with the request.
     *
     * @return request email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the execution time for the request.
     *
     * @return execution time
     */
    public double getTime() {
        return time;
    }

    /**
     * Returns the time unit for the execution time.
     *
     * @return time unit
     */
    @NonNull
    public String getUnit_timeout() {
        return unit_timeout;
    }

    /**
     * Returns the status of the response.
     *
     * @return response status
     */
    public Status getStatus() {
        return status;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GetSequenceExplanationsResult.class, name = "get_sequence_explanations"),
    })
    /**
     * Marker interface for sequence explanation result payloads.
     */
    public interface SequenceExplanationResult {
    }

    /**
     * Result wrapper for a list of sequence explanations per argument.
     */
    public static class GetSequenceExplanationsResult implements SequenceExplanationResult {
        private final Map<String, List<DialectialSequenceExplanationDTO>> perArgumentSequenceExplanations;

        /**
         * Creates a result containing sequence explanations for each argument.
         *
         * @param perArgumentSequenceExplanations mapping of argument name to sequence explanations
         */
        public GetSequenceExplanationsResult(Map<String, List<DialectialSequenceExplanationDTO>> perArgumentSequenceExplanations) {
            this.perArgumentSequenceExplanations = perArgumentSequenceExplanations;
        }

        /**
         * Returns sequence explanations grouped by argument name.
         *
         * @return argument-to-explanations map
         */
        public Map<String, List<DialectialSequenceExplanationDTO>> getPerArgumentSequenceExplanations() {
            return perArgumentSequenceExplanations;
        }

        /**
         * Creates a DTO result from service-level sequence explanations.
         *
         * @param sequenceExplanations sequence explanations from the service
         * @return DTO result object
         */
        public static GetSequenceExplanationsResult from(SequenceExplanations sequenceExplanations) {

            var perArgumentSequenceExplanations = new LinkedHashMap<String, List<DialectialSequenceExplanationDTO>>();
            for (var entry: sequenceExplanations.getPerArgumentSequenceExplanations().entrySet()) {
                var argument = entry.getKey();
                var forArgumentSequenceExplanations = entry.getValue();
                var forArgumentSequenceExplanationDTOs = DialectialSequenceExplanationDTO.from(forArgumentSequenceExplanations);
                perArgumentSequenceExplanations.put(argument.getName(), forArgumentSequenceExplanationDTOs);
            }

            return new GetSequenceExplanationsResult(perArgumentSequenceExplanations);
        }
    }

}
