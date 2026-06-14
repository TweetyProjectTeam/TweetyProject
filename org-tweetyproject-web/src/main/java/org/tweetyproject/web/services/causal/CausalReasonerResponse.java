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

import org.springframework.lang.NonNull;

/**
 * Response to {@link CausalReasonerPost}
 *
 * @author Oleksandr Dzhychko
 */
public final class CausalReasonerResponse {

    /**
     * Enum for response status
     */
    public enum Status {
        /** success */
        SUCCESS,
        /** timeout */
        TIMEOUT,
    }

    /**
     * Result of execution {@link CausalReasonerPost#getCmd()} if {@link CausalReasonerResponse#status} is {@link Status#SUCCESS}.
     * Else {@code null}.
     */
    private final String reply;
    /**
     * E-Mail (or other identifier) as provided by {@link CausalReasonerPost#getEmail()}
     */
    private final String email;
    /**
     * Time it took execute the command
     */
    private final double time;
    /**
     * The time unit of {@link CausalReasonerResponse#time}
     */
    @NonNull
    private final String unit_timeout;
    /**
     * Whether the execution executed successfully or timed out.
     */
    private final Status status;

    /**
     * Initialize a new CausalReasonerResponse
     * @param reply         the reply
     * @param email         the user email
     * @param time          the computation time
     * @param unit_timeout  the unit of time
     * @param status        the response status
     */
    public CausalReasonerResponse(String reply, String email, double time, @NonNull String unit_timeout, Status status) {
        this.reply = reply;
        this.email = email;
        this.time = time;
        this.status = status;
        this.unit_timeout = unit_timeout;
    }

    public String getReply() {
        return reply;
    }

    public String getEmail() {
        return email;
    }

    public double getTime() {
        return time;
    }

    @NonNull
    public String getUnit_timeout() {
        return unit_timeout;
    }

    public Status getStatus() {
        return status;
    }
}
