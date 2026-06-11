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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services.paf;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;

/**
 * Info response for the probabilistic argumentation framework (PAF) web service.
 */
public class PafServicesInfoResponse extends Response {

    private String reply;
    private String email;
    private int backend_timeout;
    private List<String> semantics;
    private List<String> commands;
    private List<String> solvers;

    public PafServicesInfoResponse() {}

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBackend_timeout() { return backend_timeout; }
    public void setBackend_timeout(int backend_timeout) { this.backend_timeout = backend_timeout; }

    public List<String> getSemantics() { return semantics; }
    public void setSemantics(List<String> semantics) { this.semantics = semantics; }

    public List<String> getCommands() { return commands; }
    public void setCommands(List<String> commands) { this.commands = commands; }

    public List<String> getSolvers() { return solvers; }
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
