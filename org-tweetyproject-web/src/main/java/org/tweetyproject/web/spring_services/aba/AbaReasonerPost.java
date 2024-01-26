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
package org.tweetyproject.web.spring_services.aba;

import java.util.Objects;

/**
 * The AbaReasonerPost class represents a data structure for sending post requests
 * to an Argumentation-Based Argumentation (ABA) reasoner.
 */
public class AbaReasonerPost {

  /** The command for the ABA reasoner post request */
  private String cmd;

  /** The email associated with the request */
  private String email;

  /** The knowledge base (KB) for the ABA reasoner post request */
  private String kb;

  /** The format of the knowledge base (KB) */
  private String kb_format;

  /** The first-order logic (FOL) signature */
  private String fol_signature;

  /** The query assumption for the ABA reasoner post request */
  private String query_assumption;

  /** The semantics to be used in the ABA reasoner post request */
  private String semantics;

  /** The timeout in seconds for the ABA reasoner post request */
  private int timeout;

  /** The unit timeout for the ABA reasoner post request */
  private String unit_timeout;



    /**
     * Default constructor for AbaReasonerPost.
     */
    public AbaReasonerPost() {
    }

    /**
     * Parameterized constructor for AbaReasonerPost.
     *
     * @param cmd              The command for the ABA reasoner post request
     * @param email            The email associated with the request
     * @param kb               The knowledge base (KB) for the ABA reasoner post request
     * @param kb_format        The format of the knowledge base (KB)
     * @param fol_signature    The first-order logic (FOL) signature
     * @param query_assumption The query assumption for the ABA reasoner post request
     * @param semantics        The semantics to be used in the ABA reasoner post request
     * @param timeout          The timeout in seconds for the ABA reasoner post request
     * @param unit_timeout     The unit timeout for the ABA reasoner post request
     */
    public String getFol_signature() {
      return fol_signature;
    }
    public void setFol_signature(String fol_signature) {
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