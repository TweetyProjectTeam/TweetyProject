/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.paf;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * Info response for the probabilistic argumentation framework (PAF) web service.
 */
public class PafServicesInfoResponse extends Response {

    /** The textual reply message */
    private String reply;
    /** The email address */
    private String email;
    /** The backend timeout in seconds */
    private int backend_timeout;
    /** The list of supported semantics */
    private List<String> semantics;
    /** The list of available commands */
    private List<String> commands;
    /** The list of available solvers */
    private List<String> solvers;

    /**
     * Creates an empty PAF services info response.
     */
    public PafServicesInfoResponse() {}

    /**
     * Returns the textual reply message.
     *
     * @return reply message
     */
    public String getReply() { return reply; }

    /**
     * Sets the textual reply message.
     *
     * @param reply reply message
     */
    public void setReply(String reply) { this.reply = reply; }

    /**
     * Returns the contact email associated with the response.
     *
     * @return email address
     */
    public String getEmail() { return email; }

    /**
     * Sets the contact email associated with the response.
     *
     * @param email email address
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the backend timeout value.
     *
     * @return backend timeout in seconds
     */
    public int getBackend_timeout() { return backend_timeout; }

    /**
     * Sets the backend timeout value.
     *
     * @param backend_timeout backend timeout in seconds
     */
    public void setBackend_timeout(int backend_timeout) { this.backend_timeout = backend_timeout; }

    /**
     * Returns the supported semantics names.
     *
     * @return list of semantics names
     */
    public List<String> getSemantics() { return semantics; }

    /**
     * Sets the supported semantics names.
     *
     * @param semantics list of semantics names
     */
    public void setSemantics(List<String> semantics) { this.semantics = semantics; }

    /**
     * Returns the available commands supported by the service.
     *
     * @return list of command names
     */
    public List<String> getCommands() { return commands; }

    /**
     * Sets the available commands supported by the service.
     *
     * @param commands list of command names
     */
    public void setCommands(List<String> commands) { this.commands = commands; }

    /**
     * Returns the supported solver names.
     *
     * @return list of solver names
     */
    public List<String> getSolvers() { return solvers; }

    /**
     * Sets the supported solver names.
     *
     * @param solvers list of solver names
     */
    public void setSolvers(List<String> solvers) { this.solvers = solvers; }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PafServicesInfoResponse)) return false;
        PafServicesInfoResponse r = (PafServicesInfoResponse) o;
        return Objects.equals(reply, r.reply) && Objects.equals(email, r.email)
                && backend_timeout == r.backend_timeout && Objects.equals(semantics, r.semantics)
                && Objects.equals(commands, r.commands) && Objects.equals(solvers, r.solvers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, backend_timeout, semantics, commands, solvers);
    }

    @Override
    public String toString() {
        return "{reply='" + reply + "', email='" + email + "', backend_timeout=" + backend_timeout
                + ", semantics=" + semantics + ", commands=" + commands + ", solvers=" + solvers + "}";
    }
}
