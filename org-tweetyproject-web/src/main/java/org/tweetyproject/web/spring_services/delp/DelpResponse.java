package org.tweetyproject.web.spring_services.delp;
import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;
public class DelpResponse extends Response {
    private String reply;
    private String email;
    private String compcriterion;
    private String kb;
    private String query;
    private int timeout;
    private String answer;
    private double time;
    private String unit_time;
    private String status;


    public DelpResponse() {
    }

    public DelpResponse(String reply, String email, String compcriterion, String kb, String query, int timeout, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.compcriterion = compcriterion;
        this.kb = kb;
        this.query = query;
        this.timeout = timeout;
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

    public String getCompcriterion() {
        return this.compcriterion;
    }

    public void setCompcriterion(String compcriterion) {
        this.compcriterion = compcriterion;
    }

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public String getUnit_time() {
        return this.unit_time;
    }

    public void setUnit_time(String unit_time) {
        this.unit_time = unit_time;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DelpResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public DelpResponse email(String email) {
        setEmail(email);
        return this;
    }

    public DelpResponse compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    public DelpResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    public DelpResponse query(String query) {
        setQuery(query);
        return this;
    }

    public DelpResponse timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    public DelpResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    public DelpResponse time(double time) {
        setTime(time);
        return this;
    }

    public DelpResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    public DelpResponse status(String status) {
        setStatus(status);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DelpResponse)) {
            return false;
        }
        DelpResponse delpResponse = (DelpResponse) o;
        return Objects.equals(reply, delpResponse.reply) && Objects.equals(email, delpResponse.email) && Objects.equals(compcriterion, delpResponse.compcriterion) && Objects.equals(kb, delpResponse.kb) && Objects.equals(query, delpResponse.query) && timeout == delpResponse.timeout && Objects.equals(answer, delpResponse.answer) && time == delpResponse.time && Objects.equals(unit_time, delpResponse.unit_time) && Objects.equals(status, delpResponse.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, compcriterion, kb, query, timeout, answer, time, unit_time, status);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", compcriterion='" + getCompcriterion() + "'" +
            ", kb='" + getKb() + "'" +
            ", query='" + getQuery() + "'" +
            ", timeout='" + getTimeout() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", time='" + getTime() + "'" +
            ", unit_time='" + getUnit_time() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

}