package org.tweetyproject.web.spring_services.aba;

import java.util.Objects;

import org.tweetyproject.web.spring_services.Response;
public class AbaReasonerResponse extends Response {

    private String reply;
    private String email;
    private String kb;
    private String kb_format;
    private String fol_signature;

    private String query_assumption;
    private String semantics;
    private int timeout;
    private String answer;
    private double time;
    private String unit_time;
    private String status;

    public String getFol_signature() {
      return fol_signature;
    }

    public void setFol_signature(String fol_signature) {
      this.fol_signature = fol_signature;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AbaReasonerResponse() {
    }

    public AbaReasonerResponse(String reply, String email, String kb, String kb_format, String fol_signature, String query_assumption, String semantics, int timeout, String answer, double time, String unit_time, String status) {
        this.reply = reply;
        this.email = email;
        this.kb = kb;
        this.kb_format = kb_format;
        this.query_assumption = query_assumption;
        this.semantics = semantics;
        this.timeout = timeout;
        this.answer = answer;
        this.time = time;
        this.unit_time = unit_time;
        this.status = status;
        this.fol_signature = fol_signature;
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

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getKb_format() {
        return this.kb_format;
    }

    public void setKb_format(String kb_format) {
        this.kb_format = kb_format;
    }

    public String getQuery_assumption() {
        return this.query_assumption;
    }

    public void setQuery_assumption(String query_assumption) {
        this.query_assumption = query_assumption;
    }

    public String getSemantics() {
        return this.semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
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

    public AbaReasonerResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public AbaReasonerResponse email(String email) {
        setEmail(email);
        return this;
    }

    public AbaReasonerResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    public AbaReasonerResponse kb_format(String kb_format) {
        setKb_format(kb_format);
        return this;
    }

    public AbaReasonerResponse query_assumption(String query_assumption) {
        setQuery_assumption(query_assumption);
        return this;
    }

    public AbaReasonerResponse semantics(String semantics) {
        setSemantics(semantics);
        return this;
    }

    public AbaReasonerResponse timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    public AbaReasonerResponse answer(String answer) {
        setAnswer(answer);
        return this;
    }

    public AbaReasonerResponse time(double time) {
        setTime(time);
        return this;
    }

    public AbaReasonerResponse unit_time(String unit_time) {
        setUnit_time(unit_time);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerResponse)) {
            return false;
        }
        AbaReasonerResponse abaReasonerResponse = (AbaReasonerResponse) o;
        return Objects.equals(reply, abaReasonerResponse.reply) && Objects.equals(email, abaReasonerResponse.email) && Objects.equals(kb, abaReasonerResponse.kb) && Objects.equals(kb_format, abaReasonerResponse.kb_format) && Objects.equals(query_assumption, abaReasonerResponse.query_assumption) && Objects.equals(semantics, abaReasonerResponse.semantics) && timeout == abaReasonerResponse.timeout && Objects.equals(answer, abaReasonerResponse.answer) && time == abaReasonerResponse.time && Objects.equals(unit_time, abaReasonerResponse.unit_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, kb, kb_format, query_assumption, semantics, timeout, answer, time, unit_time);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", kb='" + getKb() + "'" +
            ", kb_format='" + getKb_format() + "'" +
            ", query_assumption='" + getQuery_assumption() + "'" +
            ", semantics='" + getSemantics() + "'" +
            ", timeout='" + getTimeout() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", time='" + getTime() + "'" +
            ", unit_time='" + getUnit_time() + "'" +
            "}";
    }

}