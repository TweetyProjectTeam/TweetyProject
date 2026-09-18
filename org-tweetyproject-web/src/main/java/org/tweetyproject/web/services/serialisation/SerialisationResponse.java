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
 * The SerialisationResponse class extends the Response class and represents
 * a response containing information related to a serialisation
 * result.
 */
public class SerialisationResponse extends Response {

    /** The reply message in the serialisation response */
    private String reply;

    /** The email associated with the serialisation response */
    private String email;

    /** The number of arguments in the serialisation response */
    private int nr_of_arguments;

    /** The attacks information in the serialisation response */
    private List<List<Integer>> attacks;

    /** The extension in the serialisation response */
    private List<Integer> extension;

    /** The selection function specified in the serialisation response */
    private String selectionFunction;

    /** The termination function specified in the serialisation response */
    private String terminationFunction;

    /** The answer provided by the serialisation response */
    private String answer;

    /** The time taken for the serialisation operation (in seconds) */
    private double time;

    /** The unit time specified in the serialisation response */
    private String unit_time;

    /** The status of the serialisation response */
    private String status;

    /**
     * Default constructor for SerialisationResponse.
     */
    public SerialisationResponse() {
    }

    /**
     * Parameterized constructor for SerialisationResponse.
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param nr_of_arguments The number of arguments
     * @param attacks         The attacks information
     * @param extension       The extension
     * @param selectionFunction       The semantics specified
     * @param terminationFunction          The solver specified
     * @param answer          The answer provided
     * @param time            The time taken for the operation
     * @param unit_time       The unit time specified
     * @param status          The status of the response
     */
    public SerialisationResponse(String reply, String email, int nr_of_arguments, List<List<Integer>> attacks, List<Integer> extension,
                                 String selectionFunction, String terminationFunction, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.nr_of_arguments = nr_of_arguments;
        this.attacks = attacks;
        this.extension = extension;
        this.selectionFunction = selectionFunction;
        this.terminationFunction = terminationFunction;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * Gets the reply message in the serialisation response.
     *
     * @return The reply message in the serialisation response.
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply message in the serialisation response.
     *
     * @param reply The reply message in the serialisation response.
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Gets the email associated with the serialisation response.
     *
     * @return The email associated with the serialisation response.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the serialisation response.
     *
     * @param email The email associated with the serialisation response.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the number of arguments in the serialisation response.
     *
     * @return The number of arguments in the serialisation response.
     */
    public int getNr_of_arguments() {
        return nr_of_arguments;
    }

    /**
     * Sets the number of arguments in the serialisation response.
     *
     * @param nr_of_arguments The number of arguments in the serialisation response.
     */
    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    /**
     * Gets the attacks information in the serialisation response.
     *
     * @return The attacks information in the serialisation response.
     */
    public List<List<Integer>> getAttacks() {
        return attacks;
    }

    /**
     * Sets the attacks information in the serialisation response.
     *
     * @param attacks The attacks information in the serialisation response.
     */
    public void setAttacks(List<List<Integer>> attacks) {
        this.attacks = attacks;
    }

    /**
     * Gets the extension in the serialisation response.
     *
     * @return The extension in the serialisation response.
     */
    public List<Integer> getExtension() {
        return extension;
    }

    /**
     * Sets the extension in the serialisation response.
     *
     * @param extension The extension in the serialisation response.
     */
    public void setExtension(List<Integer> extension) {
        this.extension = extension;
    }

    /**
     * Gets the selection function specified in the serialisation response.
     *
     * @return The selection function specified in the serialisation response.
     */
    public String getSelectionFunction() {
        return selectionFunction;
    }

    /**
     * Sets the selection function specified in the serialisation response.
     *
     * @param selectionFunction The selection function specified in the serialisation response.
     */
    public void setSelectionFunction(String selectionFunction) {
        this.selectionFunction = selectionFunction;
    }

    /**
     * Gets the termination function specified in the serialisation response.
     *
     * @return The termination function specified in the serialisation response.
     */
    public String getTerminationFunction() {
        return terminationFunction;
    }

    /**
     * Sets the termination function specified in the serialisation response.
     *
     * @param terminationFunction The termination function specified in the serialisation response.
     */
    public void setTerminationFunction(String terminationFunction) {
        this.terminationFunction = terminationFunction;
    }

    /**
     * Gets the answer provided by the serialisation response.
     *
     * @return The answer provided by the serialisation response.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Sets the answer provided by the serialisation response.
     *
     * @param answer The answer provided by the serialisation response.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Gets the time taken for the serialisation operation (in seconds).
     *
     * @return The time taken for the serialisation operation (in seconds).
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the time taken for the serialisation operation (in seconds).
     *
     * @param time The time taken for the serialisation operation (in seconds).
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the unit time specified in the serialisation response.
     *
     * @return The unit time specified in the serialisation response.
     */
    public String getUnit_time() {
        return unit_time;
    }

    /**
     * Sets the unit time specified in the serialisation response.
     *
     * @param unit_time The unit time specified in the serialisation response.
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    /**
     * Gets the status of the serialisation response.
     *
     * @return The status of the serialisation response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the serialisation response.
     *
     * @param status The status of the serialisation response.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the reply message in the serialisation response.
     *
     * @param reply The reply message in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * Sets the email associated with the serialisation response.
     *
     * @param email The email associated with the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * Sets the number of arguments in the serialisation response.
     *
     * @param nr_of_arguments The number of arguments in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse nr_of_arguments(int nr_of_arguments) {
        setNr_of_arguments(nr_of_arguments);
        return this;
    }

    /**
     * Sets the attacks information in the serialisation response.
     *
     * @param attacks The attacks information in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse attacks(List<List<Integer>> attacks) {
        setAttacks(attacks);
        return this;
    }

    /**
     * Sets the extension in the serialisation response.
     *
     * @param extension The extension in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse extension(List<Integer> extension) {
        setExtension(extension);
        return this;
    }

    /**
     * Sets the selection function specified in the serialisation response.
     *
     * @param selectionFunction The selection function specified in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse selectionFunction(String selectionFunction) {
        setSelectionFunction(selectionFunction);
        return this;
    }

    /**
     * Sets the termination function specified in the serialisation response.
     *
     * @param terminationFunction The termination function specified in the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse terminationFunction(String terminationFunction) {
        setTerminationFunction(terminationFunction);
        return this;
    }

    /**
     * Sets the answer provided by the serialisation response.
     *
     * @param answer The answer provided by the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * Sets the time taken for the serialisation operation (in seconds).
     *
     * @param time The time taken for the serialisation operation (in seconds).
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse time(int time) {
        setTime(time);
        return this;
    }

    /**
     * Sets the status of the serialisation response.
     *
     * @param status The status of the serialisation response.
     * @return The current instance of SerialisationResponse.
     */
    public SerialisationResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SerialisationResponse)) {
            return false;
        }
        SerialisationResponse tweetyResponse = (SerialisationResponse) o;
        return Objects.equals(reply, tweetyResponse.reply) && Objects.equals(email, tweetyResponse.email)
                && nr_of_arguments == tweetyResponse.nr_of_arguments && Objects.equals(attacks, tweetyResponse.attacks) && Objects.equals(extension, tweetyResponse.extension)
                && Objects.equals(selectionFunction, tweetyResponse.selectionFunction) && Objects.equals(terminationFunction, tweetyResponse.terminationFunction)
                && Objects.equals(answer, tweetyResponse.answer) && time == tweetyResponse.time
                && Objects.equals(status, tweetyResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, nr_of_arguments, attacks, selectionFunction, terminationFunction, answer, time, status);
    }

    @Override
    public String toString() {
        return "{" +
                " reply='" + getReply() + "'" +
                ", email='" + getEmail() + "'" +
                ", nr_of_arguments='" + getNr_of_arguments() + "'" +
                ", attacks='" + getAttacks() + "'" +
                ", extension='" + getExtension() + "'" +
                ", selectionFunction='" + getSelectionFunction() + "'" +
                ", terminationFunction='" + getTerminationFunction() + "'" +
                ", answer='" + getAnswer() + "'" +
                ", time='" + getTime() + "'" +
                ", status='" + getStatus() + "'" +
                "}";
    }

}