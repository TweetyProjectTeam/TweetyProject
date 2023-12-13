package org.tweetyproject.web.pyargservices.aba;

import java.util.List;
import java.util.Objects;

import org.tweetyproject.web.pyargservices.Response;
public class AbaReasonerResponse extends Response {

    private String reply;
    private String email;
    private int nr_of_arguments;
    private List<List<Integer>> attacks;
    private String semantics;
    private String solver;
    private String answer;
    private double time;
    private String unit_time;
    
    public String getUnit_time() {
        return unit_time;
    }

    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    private String status;

    

    public AbaReasonerResponse() {
    }

    public AbaReasonerResponse(String reply, String email, int nr_of_arguments, List<List<Integer>> attacks, String semantics, String solver, String answer, int time, String unit_time, String status) {
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

    public String getReply() {
        return this.reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNr_of_arguments() {
        return this.nr_of_arguments;
    }

    public void setNr_of_arguments(int nr_of_arguments) {
        this.nr_of_arguments = nr_of_arguments;
    }

    public List<List<Integer>> getAttacks() {
        return this.attacks;
    }

    public void setAttacks(List<List<Integer>> attacks) {
        this.attacks = attacks;
    }

    public String getSemantics() {
        return this.semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public String getSolver() {
        return this.solver;
    }

    public void setSolver(String solver) {
        this.solver = solver;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AbaReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public AbaReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    public AbaReasonerResponse nr_of_arguments(int nr_of_arguments) {
        setNr_of_arguments(nr_of_arguments);
        return this;
    }

    public AbaReasonerResponse attacks(List<List<Integer>> attacks) {
        setAttacks(attacks);
        return this;
    }

    public AbaReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    public AbaReasonerResponse solver(String solver) {
        setSolver(solver);
        return this;
    }

    public AbaReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    public AbaReasonerResponse time(int time) {
        setTime(time);
        return this;
    }

    public AbaReasonerResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerResponse)) {
            return false;
        }
        AbaReasonerResponse tweetyResponse = (AbaReasonerResponse) o;
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

    // standard getters/setters
}