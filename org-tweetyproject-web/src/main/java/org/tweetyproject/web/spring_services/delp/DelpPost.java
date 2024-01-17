package org.tweetyproject.web.spring_services.delp;

import java.util.Objects;

public class DelpPost {

    private String cmd;
    private String email;
    private String compcriterion;
    private String kb;
    private String query;
    private int timeout;
    private String unit_timeout;


    public DelpPost() {
    }

    public DelpPost(String cmd, String email, String compcriterion, String kb, String query, int timeout, String unit_timeout) {
        this.cmd = cmd;
        this.email = email;
        this.compcriterion = compcriterion;
        this.kb = kb;
        this.query = query;
        this.timeout = timeout;
        this.unit_timeout = unit_timeout;
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

    public String getUnit_timeout() {
        return this.unit_timeout;
    }

    public void setUnit_timeout(String unit_timeout) {
        this.unit_timeout = unit_timeout;
    }

    public DelpPost cmd(String cmd) {
        setCmd(cmd);
        return this;
    }

    public DelpPost email(String email) {
        setEmail(email);
        return this;
    }

    public DelpPost compcriterion(String compcriterion) {
        setCompcriterion(compcriterion);
        return this;
    }

    public DelpPost kb(String kb) {
        setKb(kb);
        return this;
    }

    public DelpPost query(String query) {
        setQuery(query);
        return this;
    }

    public DelpPost timeout(int timeout) {
        setTimeout(timeout);
        return this;
    }

    public DelpPost unit_timeout(String unit_timeout) {
        setUnit_timeout(unit_timeout);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DelpPost)) {
            return false;
        }
        DelpPost delpPost = (DelpPost) o;
        return Objects.equals(cmd, delpPost.cmd) && Objects.equals(email, delpPost.email) && Objects.equals(compcriterion, delpPost.compcriterion) && Objects.equals(kb, delpPost.kb) && Objects.equals(query, delpPost.query) && timeout == delpPost.timeout && Objects.equals(unit_timeout, delpPost.unit_timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cmd, email, compcriterion, kb, query, timeout, unit_timeout);
    }

    @Override
    public String toString() {
        return "{" +
            " cmd='" + getCmd() + "'" +
            ", email='" + getEmail() + "'" +
            ", compcriterion='" + getCompcriterion() + "'" +
            ", kb='" + getKb() + "'" +
            ", query='" + getQuery() + "'" +
            ", timeout='" + getTimeout() + "'" +
            ", unit_timeout='" + getUnit_timeout() + "'" +
            "}";
    }

}
