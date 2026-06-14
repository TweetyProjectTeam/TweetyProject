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
package org.tweetyproject.web.services.serialisation;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.services.Response;


/**
 * The SerialisationInfoResponse class extends the Response class and represents
 * a response containing information about Dung argumentation services.
 */
public class SerialisationInfoResponse extends Response {

    /** The reply message in the Serialisation info response */
    private String reply;

    /** The email associated with the Serialisation info response */
    private String email;

    /** The backend timeout value specified in the Serialisation info response */
    private int backend_timeout;

    /** The list of supported selection functions in the Serialisation info response */
    private List<String> selectionFunctions;

    /** The list of supported termination functions in the Serialisation info response */
    private List<String> terminationFunctions;

    /** The list of supported commands in the Serialisation info response */
    private List<String> commands;

    /**
     * Default constructor for SerialisationInfoResponse.
     */
    public SerialisationInfoResponse() {
    }

    /**
     * Parameterized constructor for SerialisationInfoResponse.
     *
     * @param reply                 The reply message
     * @param email                 The email associated with the response
     * @param backend_timeout       The backend timeout value
     * @param selectionFunctions    The list of supported selection functions
     * @param terminationFunctions  The list of supported termination functions
     * @param commands              The list of supported commands
     */
    public SerialisationInfoResponse(String reply, String email, int backend_timeout, List<String> selectionFunctions, List<String> terminationFunctions, List<String> commands) {
        this.reply = reply;
        this.email = email;
        this.backend_timeout = backend_timeout;
        this.selectionFunctions = selectionFunctions;
        this.terminationFunctions = terminationFunctions;
        this.commands = commands;
    }

    /**
     * Gets the reply message associated with this response.
     *
     * @return The current reply message as a string.
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * Sets the reply message for this response.
     *
     * @param reply The reply message to set.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email address associated with this response.
     *
     * @return The email address as a string.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address for this response.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the backend timeout setting for the service.
     *
     * @return The backend timeout in integer form.
     */
    public int getBackend_timeout() {
        return this.backend_timeout;
    }

    /**
     * Sets the backend timeout for the service.
     *
     * @param backend_timeout The backend timeout value in seconds.
     */
    public void setBackend_timeout(int backend_timeout) {
        this.backend_timeout = backend_timeout;
    }

    /**
     * Gets the list of selection functions associated with the service.
     *
     * @return A list of selection functions as strings.
     */
    public List<String> getSelectionFunctions() {
        return this.selectionFunctions;
    }

    /**
     * Gets the list of termination functions associated with the service.
     *
     * @return A list of termination functions as strings.
     */
    public List<String> getTerminationFunctions() {
        return this.terminationFunctions;
    }

    /**
     * Gets the list of commands associated with the service.
     *
     * @return A list of commands as strings.
     */
    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * Sets the list of commands for the service.
     *
     * @param commands A list of command strings to be utilized by the service.
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * Sets the list of selectionFunctions for the service.
     *
     * @param selectionFunctions A list of command strings to be utilized by the service.
     */
    public void setSelectionFunctions(List<String> selectionFunctions) {
        this.selectionFunctions = selectionFunctions;
    }

    /**
     * Sets the list of terminationFunctions for the service.
     *
     * @param terminationFunctions A list of command strings to be utilized by the service.
     */
    public void setTerminationFunctions(List<String> terminationFunctions) {
        this.terminationFunctions = terminationFunctions;
    }

    // Fluent setters

    /**
     * Sets the reply message and returns this instance for method chaining.
     *
     * @param reply The reply message to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email address and returns this instance for method chaining.
     *
     * @param email The email address to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the backend timeout and returns this instance for method chaining.
     *
     * @param backend_timeout The backend timeout in seconds to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse backend_timeout(int backend_timeout) {
        setBackend_timeout(backend_timeout);
        return this;
    }

    /**
     * Sets the selectionFunctions and returns this instance for method chaining.
     *
     * @param selectionFunctions A list of selectionFunctions strings to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse selectionFunctions(List<String> selectionFunctions) {
        setSelectionFunctions(selectionFunctions);
        return this;
    }

    /**
     * Sets the terminationFunctions and returns this instance for method chaining.
     *
     * @param terminationFunctions A list of terminationFunctions strings to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse terminationFunctions(List<String> terminationFunctions) {
        setTerminationFunctions(terminationFunctions);
        return this;
    }

    /**
     * Sets the commands and returns this instance for method chaining.
     *
     * @param commands A list of command strings to set.
     * @return This instance to facilitate further modifications.
     */
    public SerialisationInfoResponse commands(List<String> commands) {
        setCommands(commands);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SerialisationInfoResponse)) {
            return false;
        }
        SerialisationInfoResponse infoResponse = (SerialisationInfoResponse) o;
        return Objects.equals(reply, infoResponse.reply) && Objects.equals(email, infoResponse.email) && backend_timeout == infoResponse.backend_timeout && Objects.equals(selectionFunctions, infoResponse.selectionFunctions) && Objects.equals(terminationFunctions, infoResponse.terminationFunctions) && Objects.equals(commands, infoResponse.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, backend_timeout, selectionFunctions, terminationFunctions, commands);
    }

    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", backend_timeout='" + getBackend_timeout() + "'" +
                ", selectionFunctions='" + getSelectionFunctions() + "'" +
                ", terminationFunctions='" + getTerminationFunctions() + "'" +
                ", commands='" + getCommands() + "'" +
                "}";
    }
}
