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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.spring_services.dung;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;


/**
 * The DungReasonerResponse class extends the Response class and represents
 * a response containing information related to a Dung argumentation reasoner result.
 */
public class DungReasonerResponse extends Response {

    /** The reply message in the Dung reasoner response */
    private String reply;

    /** The email associated with the Dung reasoner response */
    private String email;

    /** The number of arguments in the Dung reasoner response */
    private int nr_of_arguments;

    /** The attacks information in the Dung reasoner response */
    private List<List<Integer>> attacks;

    /** The semantics specified in the Dung reasoner response */
    private String semantics;

    /** The solver specified in the Dung reasoner response */
    private String solver;

    /** The answer provided by the Dung reasoner response */
    private String answer;

    /** The time taken for the Dung reasoner operation (in seconds) */
    private double time;

    /** The unit time specified in the Dung reasoner response */
    private String unit_time;

    /** The status of the Dung reasoner response */
    private String status;

    public String getUnit_time() {
        return unit_time;
    }

    /**
     * *description missing*
     * @param unit_time *description missing*
     */
    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    public DungReasonerResponse() {
    }
    /**
     * Parameterized constructor for DungReasonerResponse.
     *
     * @param reply           The reply message
     * @param email           The email associated with the response
     * @param nr_of_arguments The number of arguments
     * @param attacks         The attacks information
     * @param semantics       The semantics specified
     * @param solver          The solver specified
     * @param answer          The answer provided
     * @param time            The time taken for the operation
     * @param unit_time       The unit time specified
     * @param status          The status of the response
     */
    public DungReasonerResponse(String reply, String email, int nr_of_arguments, List<List<Integer>> attacks, String semantics, String solver, String answer, int time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.nr_of_arguments = nr_of_arguments;
        this.attacks = attacks;
        this.semantics = semantics;
        this.solver = solver;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getReply() {
        return this.reply;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * *description missing*
     * @param email *description missing*
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public int getNr_of_arguments() {
        return this.nr_of_arguments;
    }

    /**
     * *description missing*
     * @param nr_of_arguments *description missing*
     */
    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public List<List<Integer>> getAttacks() {
        return this.attacks;
    }

    /**
     * *description missing*
     * @param attacks *description missing*
     */
    public void setAttacks(List<List<Integer>> attacks) {
        this.attacks = attacks;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getSemantics() {
        return this.semantics;
    }

    /**
     * *description missing*
     * @param semantics *description missing*
     */
    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getSolver() {
        return this.solver;
    }

    /**
     * *description missing*
     * @param solver *description missing*
     */
    public void setSolver(String solver) {
        this.solver = solver;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public String getAnswer() {
        return this.answer;
    }

    /**
     * *description missing*
     * @param answer *description missing*
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * *description missing*
     * @return *description missing*
     */
    public double getTime() {
        return this.time;
    }

    /**
     * *description missing*
     * @param time *description missing*
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * *description missing*
     * @return  *description missing*
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * *description missing*
     * @param status *description missing*
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * *description missing*
     * @param reply *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    /**
     * *description missing*
     * @param email *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    /**
     * *description missing*
     * @param nr_of_arguments *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse nr_of_arguments(int nr_of_arguments) {
        setNr_of_arguments(nr_of_arguments);
        return this;
    }

    /**
     * *description missing*
     * @param attacks *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse attacks(List<List<Integer>> attacks) {
        setAttacks(attacks);
        return this;
    }

    /**
     * *description missing*
     * @param semantics *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    /**
     * *description missing*
     * @param solver *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse solver(String solver) {
        setSolver(solver);
        return this;
    }

    /**
     * *description missing*
     * @param answer *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    /**
     * *description missing*
     * @param time *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse time(int time) {
        setTime(time);
        return this;
    }

    /**
     * *description missing*
     * @param status *description missing*
     * @return *description missing*
     */
    public DungReasonerResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DungReasonerResponse)) {
            return false;
        }
        DungReasonerResponse tweetyResponse = (DungReasonerResponse) o;
        return Objects.equals(reply, tweetyResponse.reply) && Objects.equals(email, tweetyResponse.email) && nr_of_arguments == tweetyResponse.nr_of_arguments && Objects.equals(attacks, tweetyResponse.attacks) && Objects.equals(semantics, tweetyResponse.semantics) && Objects.equals(solver, tweetyResponse.solver) && Objects.equals(answer, tweetyResponse.answer) && time == tweetyResponse.time && Objects.equals(status, tweetyResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, nr_of_arguments, attacks, semantics, solver, answer, time, status);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", nr_of_arguments='" + getNr_of_arguments() + "'" +
            ", attacks='" + getAttacks() + "'" +
            ", semantics='" + getSemantics() + "'" +
            ", solver='" + getSolver() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", time='" + getTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }


}