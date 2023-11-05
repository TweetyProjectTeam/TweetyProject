package org.tweetyproject.web.pyargservices;
import java.util.Objects;

public class InconsistencyPost extends Post {

    private String cmd;
    private String email;
    private String measure;
    private String kb;
    private String format;

    public InconsistencyPost() {
    }

    public InconsistencyPost(String cmd, String email, String measure, String kb, String format) {
        this.cmd = cmd;
        this.email = email;
        this.measure = measure;
        this.kb = kb;
        this.format = format;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    public InconsistencyPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    public InconsistencyPost email(String email) {
        setEmail(email);
        return this;
    }

    public InconsistencyPost measure(String measure) {
        setMeasure(measure);
        return this;
    }

    public InconsistencyPost kb(String kb) {
        setKb(kb);
        return this;
    }

    public InconsistencyPost format(String format) {
        setFormat(format);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InconsistencyPost)) {
            return false;
        }
        InconsistencyPost inconsistencyPost = (InconsistencyPost) o;
        return Objects.equals(cmd, inconsistencyPost.cmd) && Objects.equals(email, inconsistencyPost.email) && Objects.equals(measure, inconsistencyPost.measure) && Objects.equals(kb, inconsistencyPost.kb) && Objects.equals(format, inconsistencyPost.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, email, measure, kb, format);
    }

    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            ", email='" + getEmail() + "'" +
            ", measure='" + getMeasure() + "'" +
            ", kb='" + getKb() + "'" +
            ", format='" + getFormat() + "'" +
            "}";
    }
    
}
