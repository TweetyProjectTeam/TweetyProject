package org.tweetyproject.web.pyargservices;
import java.util.Objects;

public class InconsistencyResponse extends Response {
    private String reply;
    private String email;
    private String measure;
    private String kb;
    private String format;
    private double value;
    private double time;

    public InconsistencyResponse() {
    }

    public InconsistencyResponse(String reply, String email, String measure, String kb, String format, double value, double time) {
        this.reply = reply;
        this.email = email;
        this.measure = measure;
        this.kb = kb;
        this.format = format;
        this.value = value;
        this.time = time;
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

    public String getMeasure() {
        return this.measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getKb() {
        return this.kb;
    }

    public void setKb(String kb) {
        this.kb = kb;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public InconsistencyResponse reply(String reply) {
        setReply(reply);
        return this;
    }

    public InconsistencyResponse email(String email) {
        setEmail(email);
        return this;
    }

    public InconsistencyResponse measure(String measure) {
        setMeasure(measure);
        return this;
    }

    public InconsistencyResponse kb(String kb) {
        setKb(kb);
        return this;
    }

    public InconsistencyResponse format(String format) {
        setFormat(format);
        return this;
    }

    public InconsistencyResponse value(double value) {
        setValue(value);
        return this;
    }

    public InconsistencyResponse time(double time) {
        setTime(time);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InconsistencyResponse)) {
            return false;
        }
        InconsistencyResponse inconsistencyResponse = (InconsistencyResponse) o;
        return Objects.equals(reply, inconsistencyResponse.reply) && Objects.equals(email, inconsistencyResponse.email) && Objects.equals(measure, inconsistencyResponse.measure) && Objects.equals(kb, inconsistencyResponse.kb) && Objects.equals(format, inconsistencyResponse.format) && value == inconsistencyResponse.value && time == inconsistencyResponse.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reply, email, measure, kb, format, value, time);
    }

    @Override
    public String toString() {
        return "{" +
            " reply='" + getReply() + "'" +
            ", email='" + getEmail() + "'" +
            ", measure='" + getMeasure() + "'" +
            ", kb='" + getKb() + "'" +
            ", format='" + getFormat() + "'" +
            ", value='" + getValue() + "'" +
            ", time='" + getTime() + "'" +
            "}";
    }
    
}
