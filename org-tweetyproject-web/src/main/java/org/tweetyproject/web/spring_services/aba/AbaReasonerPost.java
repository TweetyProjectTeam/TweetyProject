package org.tweetyproject.web.spring_services.aba;

import java.util.Objects;

public class AbaReasonerPost
 {

    private String cmd;
    private String email;
    private String kb;
    private String kb_format;
    private String fol_signature;
    public String getFol_signature() {
      return fol_signature;
    }

    public void setFol_signature(String fol_signature) {
      this.fol_signature = fol_signature;
    }

    private String query_assumption;
    private String semantics;
    private int timeout;
    private String unit_timeout;


  public AbaReasonerPost() {
  }

  public AbaReasonerPost(String cmd, String email, String kb, String kb_format, String fol_signature, String query_assumption, String semantics, int timeout, String unit_timeout) {
    this.cmd = cmd;
    this.email = email;
    this.kb = kb;
    this.kb_format = kb_format;
    this.query_assumption = query_assumption;
    this.semantics = semantics;
    this.timeout = timeout;
    this.unit_timeout = unit_timeout;
    this.fol_signature = fol_signature;
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

  public String getUnit_timeout() {
    return this.unit_timeout;
  }

  public void setUnit_timeout(String unit_timeout) {
    this.unit_timeout = unit_timeout;
  }

  public AbaReasonerPost cmd(String cmd) {
    setCmd(cmd);
    return this;
  }

  public AbaReasonerPost email(String email) {
    setEmail(email);
    return this;
  }

  public AbaReasonerPost kb(String kb) {
    setKb(kb);
    return this;
  }

  public AbaReasonerPost kb_format(String kb_format) {
    setKb_format(kb_format);
    return this;
  }

  public AbaReasonerPost query_assumption(String query_assumption) {
    setQuery_assumption(query_assumption);
    return this;
  }

  public AbaReasonerPost semantics(String semantics) {
    setSemantics(semantics);
    return this;
  }

  public AbaReasonerPost timeout(int timeout) {
    setTimeout(timeout);
    return this;
  }

  public AbaReasonerPost unit_timeout(String unit_timeout) {
    setUnit_timeout(unit_timeout);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AbaReasonerPost)) {
            return false;
        }
        AbaReasonerPost abaReasonerPost = (AbaReasonerPost) o;
        return Objects.equals(cmd, abaReasonerPost.cmd) && Objects.equals(email, abaReasonerPost.email) && Objects.equals(kb, abaReasonerPost.kb) && Objects.equals(kb_format, abaReasonerPost.kb_format) && Objects.equals(query_assumption, abaReasonerPost.query_assumption) && Objects.equals(semantics, abaReasonerPost.semantics) && timeout == abaReasonerPost.timeout && Objects.equals(unit_timeout, abaReasonerPost.unit_timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cmd, email, kb, kb_format, query_assumption, semantics, timeout, unit_timeout);
  }

  @Override
  public String toString() {
    return "{" +
      " cmd='" + getCmd() + "'" +
      ", email='" + getEmail() + "'" +
      ", kb='" + getKb() + "'" +
      ", kb_format='" + getKb_format() + "'" +
      ", query_assumption='" + getQuery_assumption() + "'" +
      ", semantics='" + getSemantics() + "'" +
      ", timeout='" + getTimeout() + "'" +
      ", unit_timeout='" + getUnit_timeout() + "'" +
      "}";
  }


}